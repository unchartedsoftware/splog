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

import org.apache.log4j.Level

/**
  * A simple serializable case class to use to transmit log messages from workers to the logging machine
  *
  * @param loggerName The name of the logger that should log this message
  * @param level The log level of this message
  * @param message The text message to be logged
  * @param exception The exception to be logged
  */
case class LogMessage (loggerName: String, level: Level, message: String, exception: Option[Throwable]) {
  /** Log this message */
  def output: Unit = {
    exception match {
      case Some(e) =>
        org.apache.log4j.Logger.getLogger(loggerName).log(level, message, e)
      case _ =>
        org.apache.log4j.Logger.getLogger(loggerName).log(level, message)
    }
  }
}
