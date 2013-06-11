package edu.romanow.smartcode.trees

import org.antlr.runtime.Token
import edu.romanow.smartcode.{HTMLGenerator, TokenUtil, Util}
import org.antlr.runtime.tree.CommonTree
import edu.romanow.smartcode.contexts.{GodContext, Context}
import net.liftweb.common.Box
import xml.{Text, NodeSeq}

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 05.05.12
 * Time: 13:48
 * To change this template use File | Settings | File Templates.
 */

class BaseTree(t: Token) extends CommonTree(t) {


  var context: Context = GodContext

  def begLine: Int = t.getLine

  def endLine: Int = basicChildren.lastOption.getOrElse(return begLine).getLine

  def before(t: CommonTree) =
    (getLine < t.getLine) || (getLine == t.getLine && getCharPositionInLine < t.getCharPositionInLine)

  def asXML: NodeSeq = tokenXml ++ renderChildren


  def renderChildren = basicChildren.map(_.asXML).foldLeft(NodeSeq.Empty)(_ ++ _)


  def tokenXml: NodeSeq =
    <span class={TokenUtil.tokenClass(t.getType) + " " + HTMLGenerator.classForType(getType)} style={style}>
      {tokenInnerXml}
    </span>


  def style = ""

  //HTMLGenerator.itemStyle(t.getLine.toInt, t.getCharPositionInLine.toInt)

  def tokenInnerXml = Text(t.getText)


  def basicChildren = (for (i <- 0 to (getChildCount - 1)) yield getChild(i).asInstanceOf[BaseTree]).toList

  def sortedBaseChildren = basicChildren.sortWith(_.before(_))


  lazy val allChildren = {
    (basicChildren :+ this).sortWith(_.before(_))
  }


  def \(tokenType: Int): Box[BaseTree] =
    Box(all(tokenType).headOption)

  def all(tokenType: Int): List[BaseTree] =
    allChildren.filter(_.getType == tokenType)


}