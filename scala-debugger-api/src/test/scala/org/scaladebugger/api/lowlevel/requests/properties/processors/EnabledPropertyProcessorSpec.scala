package org.scaladebugger.api.lowlevel.requests.properties.processors

import com.sun.jdi.request._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, Matchers, ParallelTestExecution}
import org.scaladebugger.api.lowlevel.requests.properties.EnabledProperty
import org.scaladebugger.test.helpers.ParallelMockFunSpec

class EnabledPropertyProcessorSpec extends ParallelMockFunSpec
{
  private val testValue = false
  private val enabledProperty = EnabledProperty(value = testValue)
  private val enabledProcessor = new EnabledPropertyProcessor(enabledProperty)

  describe("EnabledPropertyProcessor") {
    describe("#process") {
      it("should set the enabled status of the event request") {
        val mockEventRequest = mock[EventRequest]

        (mockEventRequest.setEnabled _).expects(testValue).once()

        enabledProcessor.process(mockEventRequest)
      }
    }
  }
}
