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
import java.io.{ObjectInputStream, PrintStream}

import scala.io.BufferedSource
import scala.util.Try
import java.util.concurrent.{ExecutorService, Executors}

private[splog] class Receiver(
  port: Int,
  threads: Int = 4
) extends Runnable {
  // scalastyle:ignore

  @volatile private var shouldRun = true
  private val server = new ServerSocket(port.toInt)
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
          val in = new ObjectInputStream(s.getInputStream).readObject().asInstanceOf[LogMessage]
          s.close

          pool.execute(new Runnable {
            override def run(): Unit = {
              in.output
            }
          })
        })
      }
    } finally {
      pool.shutdown
    }
  }
}
