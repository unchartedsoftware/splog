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

import java.io.ByteArrayOutputStream

import org.apache.log4j.spi.LoggingEvent
import org.apache.log4j.{Appender, Layout, WriterAppender}

/**
  * An appender that can be used to collect output for testing
  * @param out
  */
class TestAppender (out: ByteArrayOutputStream) extends WriterAppender(TestAppenderLayout, out) {
  def this () {
    this(new ByteArrayOutputStream())
  }

  /**
    * Get the current total output to this appender
    * @return
    */
  def getCurrentOutput: String = {
    Thread.sleep(200)
    out.flush
    out.toString
  }
}


object TestAppenderLayout extends Layout {
  override def format(event: LoggingEvent): String = {
    if (null != event.getThrowableInformation && null != event.getThrowableInformation.getThrowable) {
      val t = event.getThrowableInformation.getThrowable
      s"""[${event.getLevel}] ${event.getLoggerName}: ${event.getMessage}\n${t.getMessage}"""
    } else {
      s"""[${event.getLevel}] ${event.getLoggerName}: ${event.getMessage}"""
    }
  }


  override def ignoresThrowable(): Boolean = false

  override def activateOptions(): Unit = {}
}
