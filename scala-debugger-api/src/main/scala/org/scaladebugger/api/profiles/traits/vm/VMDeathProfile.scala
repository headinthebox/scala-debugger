package org.scaladebugger.api.profiles.traits.vm
import acyclic.file

import com.sun.jdi.event.VMDeathEvent
import org.scaladebugger.api.lowlevel.JDIArgument
import org.scaladebugger.api.lowlevel.events.data.JDIEventDataResult
import org.scaladebugger.api.lowlevel.vm.VMDeathRequestInfo
import org.scaladebugger.api.pipelines.Pipeline
import org.scaladebugger.api.pipelines.Pipeline.IdentityPipeline

import scala.util.Try

/**
 * Represents the interface that needs to be implemented to provide
 * vm death functionality for a specific debug profile.
 */
trait VMDeathProfile {
  /** Represents a vm death event and any associated data. */
  type VMDeathEventAndData = (VMDeathEvent, Seq[JDIEventDataResult])

  /**
   * Retrieves the collection of active and pending vm death requests.
   *
   * @return The collection of information on vm death requests
   */
  def vmDeathRequests: Seq[VMDeathRequestInfo]

  /**
   * Constructs a stream of vm death events.
   *
   * @param extraArguments The additional JDI arguments to provide
   *
   * @return The stream of vm death events
   */
  def tryGetOrCreateVMDeathRequest(
    extraArguments: JDIArgument*
  ): Try[IdentityPipeline[VMDeathEvent]] = {
    tryGetOrCreateVMDeathRequestWithData(extraArguments: _*).map(_.map(_._1).noop())
  }

  /**
   * Constructs a stream of vm death events.
   *
   * @param extraArguments The additional JDI arguments to provide
   *
   * @return The stream of vm death events and any retrieved data based on
   *         requests from extra arguments
   */
  def tryGetOrCreateVMDeathRequestWithData(
    extraArguments: JDIArgument*
  ): Try[IdentityPipeline[VMDeathEventAndData]]

  /**
   * Constructs a stream of vm death events.
   *
   * @param extraArguments The additional JDI arguments to provide
   *
   * @return The stream of vm death events
   */
  def getOrCreateVMDeathRequest(
    extraArguments: JDIArgument*
  ): IdentityPipeline[VMDeathEvent] = {
    tryGetOrCreateVMDeathRequest(extraArguments: _*).get
  }

  /**
   * Constructs a stream of vm death events.
   *
   * @param extraArguments The additional JDI arguments to provide
   *
   * @return The stream of vm death events and any retrieved data based on
   *         requests from extra arguments
   */
  def getOrCreateVMDeathRequestWithData(
    extraArguments: JDIArgument*
  ): IdentityPipeline[VMDeathEventAndData] = {
    tryGetOrCreateVMDeathRequestWithData(extraArguments: _*).get
  }
}
