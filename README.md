# splog [![Build Status](https://travis-ci.org/unchartedsoftware/splog.svg?branch=master)](https://travis-ci.org/unchartedsoftware/splog) [![Coverage Status](https://coveralls.io/repos/github/unchartedsoftware/splog/badge.svg?branch=master)](https://coveralls.io/github/unchartedsoftware/splog?branch=master)

> Because splogging doesn't sound wrong at all.

`splog` is a simple logging framework for Apache Spark which spits all log content, from any node in the cluster, to the driver's `stdout` immediately upon receipt. Log messages can be generated both inside and outside serialized closures, and are logged whenever that closure is executed (usually when you perform a terminating action like `collect` or `take`).

## Getting started

*build.gradle*

```groovy
compile  "software.uncharted:splog:splog:0.0.1"
```

*Script.scala*

```scala
import software.uncharted.splog.LoggerFactory

val logger = LoggerFactory.getLogger("test")
logger.info("Hello world!") // we can log outside!
rdd.foreach(r => {
  logger.info(r._2) // we can log inside!
})
// we can log everywhere!!!
```

## Configuration

Add the following to your `resources/application.properties`:

```
splog.port=12345 # Pick an available port
splog.level=TRACE # TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF
splog.threads=4 # Number of "printing" threads. Increase if you're sending lots of messages per second.
splog.date.format="yy/MM/dd HH:mm:ss z" # Anything that can be passed to SimpleDateFormat
```

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
