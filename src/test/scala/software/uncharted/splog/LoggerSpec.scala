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

class LoggerSpec extends FunSpec with BeforeAndAfter {

  val rdd = Spark.sc.parallelize(Seq(
    (0, "BMO"),
    (1, "RBC"),
    (2, "CIBC"),
    (3, "ABC"),
    (4, "Royal Bank Canada"),
    (5, "Royal Bank of Canada"),
    (6, "The Royal Bank Of Canada"),
    (7, "The Royal Bank Of Canada and the World"),
    (8, "Fake Bank")
  ))

  before {
    LoggerFactory.start()
  }

  after {
    LoggerFactory.shutdown()
  }

  describe("splog.Logger") {
    it("Should do something") {
      LoggerFactory.start()
      val logger = LoggerFactory.getLogger("test")
      logger.info("Hello world!")
      logger.error("Oh snap!", new Exception("Oh snap!"))
      rdd.map(r => {
        logger.info(r._2)
        r
      }).collect() // scalastyle:ignore
    }
  }
}
