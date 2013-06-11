package edu.romanow.smartcode.trees


import edu.romanow.smartcode.TokenUtil
import org.antlr.runtime.Token
import edu.romanow.smartcode.contexts.{ClassObj, GodContext}
import net.liftweb.common.{Full, Empty}
import edu.romanow.smartcode.Extractor._
import net.liftweb.common.Full
import edu.romanow.antlr.JavaParser._
import edu.romanow.smartcode.html.HTMLUtil

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 08.05.12
 * Time: 18:40
 * To change this template use File | Settings | File Templates.
 */

class EnumClassTree(t: Token) extends ImaginaryTree(t) {

  //TODO id to class
  override def asXML = <div class={"intent " + TokenUtil.tokenClass(t.getType)} id={context.thisLoc.id}>
    {super.asXML}
  </div>

  lazy val className = this \ IDENTIFIER mkString


  lazy val classObj = {
    val packageName = context.fileContext.packageName

    val outer = this.context match {
      case GodContext => Empty
      case ctx => Full(ctx.thisClass.clazz)
    }

    val parentClass = extractExtendName(this)


    val interfaces = extractInterfaces(this)

    ClassObj(className, packageName, outer, parentClass, interfaces)
  }

}
