package org.jetbrains.plugins.scala
package lang
package refactoring
package rename

import _root_.scala.collection.mutable.ArrayBuffer
import com.intellij.psi.PsiElement
import com.intellij.refactoring.util.TextOccurrencesUtil
import com.intellij.usageView.UsageInfo
import java.util.ArrayList
import psi.api.base.patterns.ScCaseClause
import psi.api.statements.{ScFunction, ScValue, ScVariable}
import psi.api.toplevel.templates.ScTemplateBody
import psi.api.toplevel.{ScEarlyDefinitions, ScNamedElement}
import psi.impl.statements.params.ScParameterImpl
import psi.ScalaPsiUtil
import com.intellij.psi.util.PsiTreeUtil
import psi.api.expr.ScFunctionExpr
import psi.api.statements.params.{ScParameters, ScClassParameter, ScParameter}

/**
 * User: Alexander Podkhalyuzin
 * Date: 23.11.2008
 */

object ScalaInplaceVariableRenamer {
  def myRenameInPlace(element: PsiElement, context: PsiElement): Boolean = {
    element match {
      case name: ScNamedElement => {
        def isOk(elem: PsiElement): Boolean = {
          elem match {
            case v@(_: ScValue | _: ScVariable | _: ScFunction | _: ScCaseClause) if !v.getParent.isInstanceOf[ScTemplateBody] &&
                    !v.isInstanceOf[ScClassParameter] && !v.getParent.isInstanceOf[ScEarlyDefinitions] => true
            case p: ScParameter if PsiTreeUtil.getParentOfType(p, classOf[ScParameters]).getParent.isInstanceOf[ScFunctionExpr] => true
            case _ => false
          }
        }

        ScalaPsiUtil.nameContext(name) match {
          case elem if isOk(elem) => {
            val stringToSearch = name.name
            val usages = new ArrayList[UsageInfo]
            if (stringToSearch != null) {
              TextOccurrencesUtil.addUsagesInStringsAndComments(element, stringToSearch, usages, new TextOccurrencesUtil.UsageInfoFactory {
                def createUsageInfo(usage: PsiElement, startOffset: Int, endOffset: Int): UsageInfo = new UsageInfo(usage)
              });
            }
            usages.isEmpty
          }
          case _ => false
        }
      }
      case _ => false
    }
  }
}