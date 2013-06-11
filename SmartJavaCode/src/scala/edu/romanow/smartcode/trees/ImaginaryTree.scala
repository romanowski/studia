package edu.romanow.smartcode.trees

import org.antlr.runtime.Token
import xml.NodeSeq

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 08.05.12
 * Time: 12:34
 * To change this template use File | Settings | File Templates.
 */


/**
 * tree use for deleting tokens added to identified tree type
 * @param t
 */
class ImaginaryTree(t: Token) extends BaseTree(t) {


  override def asXML: NodeSeq = <span style={style}>
    {renderChildren}
  </span>

  override def style = basicChildren.headOption.map(_.style).getOrElse("")


}

