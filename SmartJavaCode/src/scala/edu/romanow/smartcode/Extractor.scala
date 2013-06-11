package edu.romanow.smartcode

import contexts.Variable
import trees.BaseTree
import edu.romanow.antlr.JavaParser._
import net.liftweb.common.Box

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/16/12
 * Time: 8:44 AM
 * To change this template use File | Settings | File Templates.
 */
object Extractor {
  def extractName(tree: BaseTree): String =
    tree.all(IDENTIFIER) match {
      case Nil => tree.allChildren.filter(t => TokenUtil.isPrimitive(t.getType)).headOption.map(_.getText).getOrElse("")
      case list => list.map(_.getText).mkString(".")
    }


  def extractExtendName(tree: BaseTree): Box[String] = {
    (tree \ EXTENDS).map(extractType _)
  }

  def extractInterfaces(tree: BaseTree): List[String] = {
    tree.all(IMPLEMENTS).flatMap {
      tree =>
        tree.all(TYPE_NAME_T).map {
          extractName(_)
        }
    }
  }

  def extractVariable(tree: BaseTree): Variable = {
    val name = tree \ VAR_NAME_T map (extractName) getOrElse ""
    val vType = extractType(tree)
    Variable(name, vType)
  }

  def extractFunctionParam(tree: BaseTree): Variable = {
    val name = extractName(tree)
    val vType = extractType(tree)
    Variable(name, vType)
  }

  def extractType(tree: BaseTree): String = {
    tree \ TYPE_NAME_T map (extractName _) getOrElse ("")
  }


  def extractFunctionRetVal(tree: BaseTree): String = {

    (tree \ METHOD_RET_VAL_T).map {
      head =>
        head \ VOID map (_ => "void") getOrElse {
          head \ TYPE_NAME_T map (extractName _) getOrElse ""
        }
    } getOrElse ""
  }
}
