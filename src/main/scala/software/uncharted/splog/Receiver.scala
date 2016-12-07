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

import java.net.ServerSocket
import java.io.PrintStream
import scala.io.BufferedSource
import scala.util.Try
import java.util.concurrent.{Executors, ExecutorService}

private[splog] class Receiver(
  port: Int,
  dateFormat: String = "yy/MM/dd HH:mm:ss z",
  out: PrintStream = Console.out,
  threads: Int = 4
) extends Runnable {
  import Level.{Level, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF} // scalastyle:ignore

  @volatile private var shouldRun = true
  private val server = new ServerSocket(port.toInt)
  private val format = new java.text.SimpleDateFormat(dateFormat)
  private val pool: ExecutorService = Executors.newFixedThreadPool(threads)

  def stop(): Unit = {
    shouldRun = false
    Try(server.close()) // this stops the blocking call to accept()
  }

  def run() {
    try {
      while (shouldRun) {
        Try({
          val s = server.accept() // blocks until a message comes in
          val in = new BufferedSource(s.getInputStream()).getLines()
          // spin off worker to handle message
          pool.execute(new Printer(in.next, out, format))
          s.close
        })
      }
    } finally {
      pool.shutdown
    }
  }
}
