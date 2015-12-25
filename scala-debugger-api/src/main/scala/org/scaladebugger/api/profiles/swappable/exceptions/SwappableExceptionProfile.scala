package org.scaladebugger.api.profiles.swappable.exceptions

import org.scaladebugger.api.lowlevel.JDIArgument
import org.scaladebugger.api.pipelines.Pipeline.IdentityPipeline
import org.scaladebugger.api.profiles.swappable.SwappableDebugProfile
import org.scaladebugger.api.profiles.traits.exceptions.ExceptionProfile

import scala.util.Try

/**
 * Represents a swappable profile for exception events that redirects the
 * invocation to another profile.
 */
trait SwappableExceptionProfile extends ExceptionProfile {
  this: SwappableDebugProfile =>

  override def onExceptionWithData(
    exceptionName: String,
    notifyCaught: Boolean,
    notifyUncaught: Boolean,
    extraArguments: JDIArgument*
  ): Try[IdentityPipeline[ExceptionEventAndData]] = {
    withCurrentProfile.onExceptionWithData(
      exceptionName,
      notifyCaught,
      notifyUncaught,
      extraArguments: _*
    )
  }

  override def onAllExceptionsWithData(
    notifyCaught: Boolean,
    notifyUncaught: Boolean,
    extraArguments: JDIArgument*
  ): Try[IdentityPipeline[ExceptionEventAndData]] = {
    withCurrentProfile.onAllExceptionsWithData(
      notifyCaught,
      notifyUncaught,
      extraArguments: _*
    )
  }
}