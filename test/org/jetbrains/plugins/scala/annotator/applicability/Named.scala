package org.jetbrains.plugins.scala
package annotator.applicability

import lang.psi.types.{UnresolvedParameter, ParameterSpecifiedMultipleTimes, PositionalAfterNamedArgument}

/**
 * Pavel.Fatin, 18.05.2010
 */

abstract class Named extends Applicability {
  def testFine {
    assertProblems("(a: A)", "(a = A)") {
      case Nil =>
    }
    assertProblems("(a: A, b: B)", "(a = A, b = B)") {
      case Nil =>
    }
  }

  def testReversed {
    assertProblems("(a: A, b: B)", "(b = B, a = A)") {
      case Nil =>
    }
  }

  def testPositionalWithNamed {
    assertProblems("(a: A, b: B)", "(A, b = B)") {
      case Nil =>
    }
    //TODO compiler allows such calls, they seem to be OK 
    //    assertProblems("(a: A, b: B)", "(a = A, b)") {
    //      case Nil =>
    //    }
  }

  def testPositionalAfterNamed {
    assertProblems("(a: A, b: B)", "(b = B, A)") {
      case PositionalAfterNamedArgument(Expression("A")) :: Nil =>
    }
    assertProblems("(a: A, b: B, c: C)", "(c = C, A, B)") {
      case PositionalAfterNamedArgument(Expression("A")) ::
              PositionalAfterNamedArgument(Expression("B")) :: Nil =>
    }
    assertProblems("(a: A, b: B, c: C)", "(c = C, A, B)") {
      case PositionalAfterNamedArgument(Expression("A")) ::
              PositionalAfterNamedArgument(Expression("B")) :: Nil =>
    }
    assertProblems("(a: A, b: B, c: C)", "(A, c = C, B)") {
      case PositionalAfterNamedArgument(Expression("B")) :: Nil =>
    }
  }

  def testNamedDuplicates {
    assertProblems("(a: A)", "(a = A, a = null)") {
      case ParameterSpecifiedMultipleTimes(Assignment("a = A")) ::
              ParameterSpecifiedMultipleTimes(Assignment("a = null")) :: Nil =>
    }
    assertProblems("(a: A)", "(a = A, a = A, a = A)") {
      case ParameterSpecifiedMultipleTimes(Assignment("a = A")) ::
              ParameterSpecifiedMultipleTimes(Assignment("a = A")) ::
              ParameterSpecifiedMultipleTimes(Assignment("a = A")) :: Nil =>
    }
    assertProblems("(a: A, b: B)", "(a = A, a = null, b = B, b = null)") {
      case ParameterSpecifiedMultipleTimes(Assignment("a = A")) ::
              ParameterSpecifiedMultipleTimes(Assignment("a = null")) ::
              ParameterSpecifiedMultipleTimes(Assignment("b = B")) ::
              ParameterSpecifiedMultipleTimes(Assignment("b = null")) :: Nil =>
    }
    assertProblems("(a: A, b: B)", "(A, b = B, b = null)") {
      case ParameterSpecifiedMultipleTimes(Assignment("b = B")) ::
              ParameterSpecifiedMultipleTimes(Assignment("b = null")) :: Nil =>
    }
  }
  
  def testUnresolvedParameter {
    assertProblems("()", "(a = A)") {
      case UnresolvedParameter(Assignment("a = A")) :: Nil =>
    }
    assertProblems("()", "(a = A, b = B)") {
      case UnresolvedParameter(Assignment("a = A")) :: 
              UnresolvedParameter(Assignment("b = B")) :: Nil =>
    }
    assertProblems("(a: A)", "(b = A)") {
      case UnresolvedParameter(Assignment("b = A")) :: Nil =>
    }
    assertProblems("(a: A)", "(a = A, b = B)") {
      case UnresolvedParameter(Assignment("b = B")) :: Nil =>
    }
  }
  
  def testNamedUnresolvedDuplicates {
    assertProblems("(a: A)", "(b = A, b = null)") {
      case UnresolvedParameter(Assignment("b = A")) :: 
              UnresolvedParameter(Assignment("b = null")) ::
              ParameterSpecifiedMultipleTimes(Assignment("b = A")) ::
              ParameterSpecifiedMultipleTimes(Assignment("b = null")) :: Nil =>
    }
  }

  // named with defaults
  // type mismatch
  // missed parameter
  // too many args
}