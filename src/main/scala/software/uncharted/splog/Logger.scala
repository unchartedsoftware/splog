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
import java.net.{InetAddress, Socket}
import java.io.ObjectOutputStream

class Logger(name: String,
             port: Int,
             driverHost: String
) extends Serializable {
  def log(level: Level, msg: Any, err: Option[Throwable] = None): Unit = {
    val s = new Socket(InetAddress.getByName(driverHost), port)
    val out = new ObjectOutputStream(s.getOutputStream())
    out.writeObject(LogMessage(name, level, msg.toString, err))
    out.flush
    s.close
  }

  def trace(msg: Any): Unit = {
    this.log(Level.TRACE, msg, None)
  }

  def trace(msg: Any, err: Throwable): Unit = {
    this.log(Level.TRACE, msg, Some(err))
  }

  def debug(msg: Any): Unit = {
    this.log(Level.DEBUG, msg, None)
  }

  def debug(msg: Any, err: Throwable): Unit = {
    this.log(Level.DEBUG, msg, Some(err))
  }

  def info(msg: Any): Unit = {
    this.log(Level.INFO, msg, None)
  }

  def info(msg: Any, err: Throwable): Unit = {
    this.log(Level.INFO, msg, Some(err))
  }

  def warn(msg: Any): Unit = {
    this.log(Level.WARN, msg, None)
  }

  def warn(msg: Any, err: Throwable): Unit = {
    this.log(Level.WARN, msg, Some(err))
  }

  def error(msg: Any): Unit = {
    this.log(Level.ERROR, msg, None)
  }

  def error(msg: Any, err: Throwable): Unit = {
    this.log(Level.ERROR, msg, Some(err))
  }

  def fatal(msg: Any): Unit = {
    this.log(Level.FATAL, msg, None)
  }

  def fatal(msg: String, err: Throwable): Unit = {
    this.log(Level.FATAL, msg, Some(err))
  }
}
