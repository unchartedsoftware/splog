# splog [![Build Status](https://travis-ci.org/unchartedsoftware/splog.svg?branch=master)](https://travis-ci.org/unchartedsoftware/splog) [![Coverage Status](https://coveralls.io/repos/github/unchartedsoftware/splog/badge.svg?branch=master)](https://coveralls.io/github/unchartedsoftware/splog?branch=master)

> Because splogging doesn't sound wrong at all.

`splog` is a simple logging framework for Apache Spark which spits all log content, from any node in the cluster, to the driver's `stdout` immediately upon receipt. Log messages can be generated both inside and outside serialized closures, and are logged whenever that closure is executed (usually when you perform a terminating action like `collect` or `take`).

## Getting started

*build.gradle*

```groovy
compile  "software.uncharted.splog:splog:0.1.0"
```

Logging with splog is intimately tied to spark, allowing the logging mechanism to communicate from worker to master).  Because of this, instead of obtaining loggers statically at class construction time, as is often typical, one instead should obtain loggers dynamically, from an existing spark context or spark session. For example:

*Script.scala*

```scala
import software.uncharted.splog.LoggerFactory

class Foo extends software.uncharted.splog.SparkLogging {
  def doStuffWithContext (sc: SparkContext, data: RDD[Int]): Any = {
    val logger = sc.getLogger("test 1")
    logger.info("Hello world!") // we can log outside!
    data.foreach(n =>
      // we can log inside!
      logger.info(s"We got number $n")
    )
  }
  def doStuffWithSession (session: SparkSession, data: Dataset[Int]): Any = {
    val logger = session.getLogger("test 2")
    logger.info("Hello world!") // we can log outside!
    data.foreach(n =>
      // we can log inside!
      logger.info(s"We got number $n")
    )
  }
}
// we can log everywhere!!!
```

*Script.java*
```java
import software.uncharted.splog.*;
class Foo {
  public void doStuffWithContext (JavaSparkContext sc, JavaRDD<Integer> data) {
    Logger logger = new SparkContextLogger(sc.sc()).getLogger("test");
    logger.info("Hello world!"); // we can log outside!
    data.foreach(n ->
      // we can log inside!
      logger.info(String.format("We got number %d", n))
    );
  }
  public void doStuffWithSession (SparkSession session, Dataset<Integer> data) {
    Logger logger = new SparkSessionLogger(session).getLogger("test");
    logger.info("Hello world!"); // we can log outside!
    data.foreach((ForeachFunction<Integer>) n ->
      // we can log inside!
      logger.info(String.format("We got number %d", n))
    );
  }
}
```
## Configuration

Add the following to your `resources/application.properties`:

```
splog.port=12345 # Pick an available port
splog.threads=4 # Number of "printing" threads. Increase if you're sending lots of messages per second.
```

Log output is governed by the same Apache logging used throughout Spark. Log output is therefore configured in the standard way (for example, using log4j.properties) that Apache logging is always configured.

## Shut up Spark

*log4j.properties*

```
# This silences Spark output during tests/operation
log4j.rootCategory=ERROR, console
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.target=System.err
log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=%d{yy/MM/dd HH:mm:ss} %p %c{1}: %m%n

# Settings to quiet third party logs that are too verbose
log4j.logger.org.spark-project.jetty=ERROR
log4j.logger.org.spark-project.jetty.util.component.AbstractLifeCycle=ERROR
log4j.logger.org.apache.spark.repl.SparkIMain$exprTyper=ERROR
log4j.logger.org.apache.spark.repl.SparkILoop$SparkILoopInterpreter=ERROR
```

*spark-submit*

Turn off those ridiculous stdout progress bars with:

`spark-submit --conf spark.ui.showConsoleProgress=false`
