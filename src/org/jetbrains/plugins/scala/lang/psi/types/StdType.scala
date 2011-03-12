package org.jetbrains.plugins.scala
package lang
package psi
package types

import impl.toplevel.synthetic.{SyntheticClasses, ScSyntheticClass}
import com.intellij.openapi.project.Project


abstract case class StdType(name: String, tSuper: Option[StdType]) extends ValueType {
  /**
   * Return wrapped to option appropriate synthetic class.
   * In dumb mode returns None (or before it ends to register classes).
   * @param project in which project to find this class
   * @return If possible class to represent this type.
   */
  def asClass(project: Project): Option[ScSyntheticClass] = {
    if (SyntheticClasses.get(project).isClassesRegistered)
      Some(SyntheticClasses.get(project).byName(name).get)
    else None
  }

  override def equivInner(r: ScType, subst: ScUndefinedSubstitutor, falseUndef: Boolean): (Boolean, ScUndefinedSubstitutor) = {
    (this, r) match {
      case (l: StdType, r: StdType) => (l == r, subst)
      case (AnyRef, r) => {
        ScType.extractClass(r) match {
          case Some(clazz) if clazz.getQualifiedName == "java.lang.Object" => (true, subst)
          case _ => (false, subst)
        }
      }
      case _ => (false, subst)
    }
  }
}

trait ValueType extends ScType {
  def isValue = true

  def inferValueType: ValueType = this
}

case object Any extends StdType("Any", None)

case object Null extends StdType("Null", Some(AnyRef))

case object AnyRef extends StdType("AnyRef", Some(Any))

case object Nothing extends StdType("Nothing", None)

case object Singleton extends StdType("Singleton", Some(AnyRef))

case object AnyVal extends StdType("AnyVal", Some(Any))

abstract case class ValType(override val name: String) extends StdType(name, Some(AnyVal))

object Unit extends ValType("Unit")

object Boolean extends ValType("Boolean")

object Char extends ValType("Char")

object Int extends ValType("Int")

object Long extends ValType("Long")

object Float extends ValType("Float")

object Double extends ValType("Double")

object Byte extends ValType("Byte")

object Short extends ValType("Short")