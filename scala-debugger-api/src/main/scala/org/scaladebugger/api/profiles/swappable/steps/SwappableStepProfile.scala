package org.scaladebugger.api.profiles.swappable.steps

import com.sun.jdi.ThreadReference
import com.sun.jdi.event.StepEvent
import org.scaladebugger.api.lowlevel.JDIArgument
import org.scaladebugger.api.lowlevel.events.data.JDIEventDataResult
import org.scaladebugger.api.pipelines.Pipeline.IdentityPipeline
import org.scaladebugger.api.profiles.swappable.SwappableDebugProfile
import org.scaladebugger.api.profiles.traits.steps.StepProfile

import scala.concurrent.Future
import scala.util.Try

/**
 * Represents a swappable profile for step events that redirects the
 * invocation to another profile.
 */
trait SwappableStepProfile extends StepProfile {
  this: SwappableDebugProfile =>

  override def stepIntoLineWithData(
    threadReference: ThreadReference,
    extraArguments: JDIArgument*
  ): Future[StepEventAndData] = {
    withCurrentProfile.stepIntoLineWithData(threadReference, extraArguments: _*)
  }

  override def stepOverLineWithData(
    threadReference: ThreadReference,
    extraArguments: JDIArgument*
  ): Future[StepEventAndData] = {
    withCurrentProfile.stepOverLineWithData(threadReference, extraArguments: _*)
  }

  override def stepOutLineWithData(
    threadReference: ThreadReference,
    extraArguments: JDIArgument*
  ): Future[StepEventAndData] = {
    withCurrentProfile.stepOutLineWithData(threadReference, extraArguments: _*)
  }

  override def stepIntoMinWithData(
    threadReference: ThreadReference,
    extraArguments: JDIArgument*
  ): Future[StepEventAndData] = {
    withCurrentProfile.stepIntoMinWithData(threadReference, extraArguments: _*)
  }

  override def stepOverMinWithData(
    threadReference: ThreadReference,
    extraArguments: JDIArgument*
  ): Future[StepEventAndData] = {
    withCurrentProfile.stepOverMinWithData(threadReference, extraArguments: _*)
  }

  override def stepOutMinWithData(
    threadReference: ThreadReference,
    extraArguments: JDIArgument*
  ): Future[StepEventAndData] = {
    withCurrentProfile.stepOutMinWithData(threadReference, extraArguments: _*)
  }

  override def onStepWithData(
    threadReference: ThreadReference,
    extraArguments: JDIArgument*
  ): Try[IdentityPipeline[(StepEvent, Seq[JDIEventDataResult])]] = {
    withCurrentProfile.onStepWithData(threadReference, extraArguments: _*)
  }
}