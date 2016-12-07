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

import org.scalatest._
import org.scalatest.Assertions._

class LoggerSpec extends FunSpec with BeforeAndAfter with BeforeAndAfterEach with Matchers {

  val rdd = Spark.sc.parallelize(Seq(
    (0, "first"),
    (1, "second"),
    (2, "third"),
    (3, "fourth")
  ))

  var baos = new java.io.ByteArrayOutputStream

  before {
  }

  override def beforeEach {
    baos = new java.io.ByteArrayOutputStream
    LoggerFactory.start(new java.io.PrintStream(baos))
    LoggerFactory.setLevel(Level.TRACE)
    Thread.sleep(100)
    baos.reset
  }

  override def afterEach {
    LoggerFactory.shutdown()
    Thread.sleep(50)
    baos.close
  }

  after {
  }

  describe("splog.Logger") {
    it("Should support logging outside transformations") {
      val logger = LoggerFactory.getLogger("test")
      logger.info("Hello world!")
      Thread.sleep(100)
      val log = baos.toString
      log should include ("[INFO] test: Hello world!")
    }

    it("Should support logging inside transformations") {
      val logger = LoggerFactory.getLogger("test")
      rdd.map(r => {
        logger.info(r._2)
        r
      }).collect
      Thread.sleep(100)
      val log = baos.toString
      log should include ("[INFO] test: first")
      log should include ("[INFO] test: second")
      log should include ("[INFO] test: third")
      log should include ("[INFO] test: fourth")
    }

    it("Should print nothing when log level is set to OFF") {
      val logger = LoggerFactory.getLogger("test")
      LoggerFactory.setLevel(Level.OFF)
      logger.trace("Hello world!")
      Thread.sleep(100)
      val log = baos.toString
      log should not include ("[TRACE] test: Hello world!")
    }

    it("Should throw an Exception if the client tries to retrieve a logger inside a Spark TaskContext") {
      val errors = rdd.flatMap(r => {
        try {
          val logger = LoggerFactory.getLogger("test")
          Seq()
        } catch {
          case e: Exception => Seq(e.toString)
        }
      }).collect
      assert(errors.length == 4)
    }

    it("Double starts shouldn't break things") {
      LoggerFactory.start()
      val logger = LoggerFactory.getLogger("test")
      logger.info("Hello world!")
      Thread.sleep(100)
      val log = baos.toString
      log should include ("[INFO] test: Hello world!")
    }

    it("Double shutdowns shouldn't break things") {
      LoggerFactory.shutdown()
      LoggerFactory.shutdown()
    }

    describe("#trace") {
      it("Should allow logging of messages with level TRACE") {
        val logger = LoggerFactory.getLogger("test")
        logger.trace("Hello world!")
        Thread.sleep(100)
        val log = baos.toString
        log should include ("[TRACE] test: Hello world!")
      }

      it("Should allow logging of messages and errors with level TRACE") {
        val logger = LoggerFactory.getLogger("test")
        logger.trace("Hello world!", new Exception("whoops!"))
        Thread.sleep(100)
        val log = baos.toString
        log should include ("[TRACE] test: Hello world!")
        log should include ("whoops!")
      }
    }

    describe("#debug") {
      it("Should allow logging of messages with level DEBUG") {
        val logger = LoggerFactory.getLogger("test")
        logger.debug("Hello world!")
        Thread.sleep(100)
        val log = baos.toString
        log should include ("[DEBUG] test: Hello world!")
      }

      it("Should allow logging of messages and errors with level DEBUG") {
        val logger = LoggerFactory.getLogger("test")
        logger.debug("Hello world!", new Exception("whoops!"))
        Thread.sleep(100)
        val log = baos.toString
        log should include ("[DEBUG] test: Hello world!")
        log should include ("whoops!")
      }
    }

    describe("#info") {
      it("Should allow logging of messages with level INFO") {
        val logger = LoggerFactory.getLogger("test")
        logger.info("Hello world!")
        Thread.sleep(100)
        val log = baos.toString
        log should include ("[INFO] test: Hello world!")
      }

      it("Should allow logging of messages and errors with level INFO") {
        val logger = LoggerFactory.getLogger("test")
        logger.info("Hello world!", new Exception("whoops!"))
        Thread.sleep(100)
        val log = baos.toString
        log should include ("[INFO] test: Hello world!")
        log should include ("whoops!")
      }
    }

    describe("#warn") {
      it("Should allow logging of messages with level WARN") {
        val logger = LoggerFactory.getLogger("test")
        logger.warn("Hello world!")
        Thread.sleep(100)
        val log = baos.toString
        log should include ("[WARN] test: Hello world!")
      }

      it("Should allow logging of messages and errors with level WARN") {
        val logger = LoggerFactory.getLogger("test")
        logger.warn("Hello world!", new Exception("whoops!"))
        Thread.sleep(100)
        val log = baos.toString
        log should include ("[WARN] test: Hello world!")
        log should include ("whoops!")
      }
    }

    describe("#error") {
      it("Should allow logging of messages with level ERROR") {
        val logger = LoggerFactory.getLogger("test")
        logger.error("Hello world!")
        Thread.sleep(100)
        val log = baos.toString
        log should include ("[ERROR] test: Hello world!")
      }

      it("Should allow logging of messages and errors with level ERROR") {
        val logger = LoggerFactory.getLogger("test")
        logger.error("Hello world!", new Exception("whoops!"))
        Thread.sleep(100)
        val log = baos.toString
        log should include ("[ERROR] test: Hello world!")
        log should include ("whoops!")
      }
    }

    describe("#fatal") {
      it("Should allow logging of messages with level FATAL") {
        val logger = LoggerFactory.getLogger("test")
        logger.fatal("Hello world!")
        Thread.sleep(100)
        val log = baos.toString
        log should include ("[FATAL] test: Hello world!")
      }

      it("Should allow logging of messages and errors with level FATAL") {
        val logger = LoggerFactory.getLogger("test")
        logger.fatal("Hello world!", new Exception("whoops!"))
        Thread.sleep(100)
        val log = baos.toString
        log should include ("[FATAL] test: Hello world!")
        log should include ("whoops!")
      }
    }
  }
}
