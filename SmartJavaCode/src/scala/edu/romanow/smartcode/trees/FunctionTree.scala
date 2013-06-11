package edu.romanow.smartcode.trees

import org.antlr.runtime.Token
import edu.romanow.smartcode.{TokenUtil, Util}
import edu.romanow.smartcode.contexts.{FuncLocation, EmptyLocation, Function, Context}
import edu.romanow.smartcode.Extractor._
import edu.romanow.antlr.JavaParser._
import edu.romanow.smartcode.html.HTMLUtil
import net.liftweb.common.{Empty, Box}

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 08.05.12
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */

class FunctionTree(t: Token) extends ImaginaryTree(t) {

  override def asXML() = <div class={"intent " + TokenUtil.tokenClass(t.getType)} id={loc.map(_.id).getOrElse("")}>
    {super.asXML}
  </div>


  var loc: Box[FuncLocation] = Empty

  lazy val function = {
    val name = extractName(this)
    val retVal = extractFunctionRetVal(this)
    val params = this all (FUN_ARG_T) map (extractFunctionParam _)
    Function(name, retVal, params)
  }

}
