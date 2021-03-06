package org.scaladebugger.api.profiles.traits.info

/**
 * Represents a profile that provides common methods to convert info profiles
 * to their low-level Java equivalents.
 */
trait JavaInfo {
  /**
   * Returns whether or not this info profile represents the low-level Java
   * implementation.
   *
   * @return If true, this profile represents the low-level Java information,
   *         otherwise this profile represents something higher-level like
   *         Scala, Jython, or JRuby
   */
  def isJavaInfo: Boolean

  /**
   * Converts the current profile instance to a representation of
   * low-level Java instead of a higher-level abstraction.
   *
   * @return The profile instance providing an implementation corresponding
   *         to Java
   */
  def toJavaInfo: AnyRef
}
