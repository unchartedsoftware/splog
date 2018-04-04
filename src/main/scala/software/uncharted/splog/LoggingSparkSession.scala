/*
 * Copyright 2016 Uncharted Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.uncharted.splog

import org.apache.spark.sql.SparkSession
import software.uncharted.splog.LoggerFactory.{inDriver, port}

/**
  * Pimp SparkSession to allow the creation of worker-aware loggers
  */
trait LoggingSparkSession {
  implicit def sessionToSessionLogger (session: SparkSession): SparkSessionLogger =
    new SparkSessionLogger(session)
}

sealed class SparkSessionLogger (session: SparkSession) {
  def getLogger (source: String = "root"): Logger = {
    if (!inDriver) {
      // In reality, there's no way to get a SparkSession on a worker, so this test and branch is probably
      // unnecessary.  Still, it seems good to have here in case we're wrong, or that changes, or even
      // just to document the condition.
      // $COVERAGE-OFF$
      throw new Exception("Cannot use getLogger() inside a Spark task (such as inside a map() closure)."
        + " Please instantiate your logger outside the closure and let Spark serialize it in.")
      // $COVERAGE-ON$
    } else {
      LoggerFactory.start()
      new Logger(source, port, session.conf.get("spark.driver.host"))
    }
  }
}
