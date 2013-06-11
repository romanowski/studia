package edu.romanow.smartcode.trees

import org.antlr.runtime.Token
import edu.romanow.smartcode.{Extractor, TokenUtil, Util}
import com.sun.swing.internal.plaf.basic.resources.basic_es

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 08.05.12
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */

class VariableTree(t: Token) extends ImaginaryTree(t) {

  override def asXML() = {
    val head = basicChildren.head.asXML


    <div class={"line " + TokenUtil.tokenClass(t.getType)}
         id={context.varLoc(variable.name).map(_.id).getOrElse("")}>
      {super.asXML}
    </div>
  }


  lazy val variable = {
    Extractor.extractVariable(this)
  }

}

class FunctionParamTree(t: Token) extends ImaginaryTree(t) {

  override def asXML() = <div class={TokenUtil.tokenClass(t.getType)}
                              id={context.varLoc(variable.name).map(_.id).getOrElse("")}>
    {super.asXML}
  </div>


  lazy val variable = {
    Extractor.extractFunctionParam(this)
  }

}
