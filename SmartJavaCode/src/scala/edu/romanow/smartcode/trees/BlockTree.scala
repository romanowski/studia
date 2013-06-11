package edu.romanow.smartcode.trees

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 08.05.12
 * Time: 09:59
 * To change this template use File | Settings | File Templates.
 */


import org.antlr.runtime.Token
import edu.romanow.smartcode.{TokenUtil, Util}
import edu.romanow.antlr.JavaParser
import javax.swing.text.html.HTML
import edu.romanow.smartcode.html.HTMLUtil
import xml.NodeSeq


class BlockTree(t: Token) extends ImaginaryTree(t) {


  override def asXML = <div class={"intent " + TokenUtil.tokenClass(t.getType)}>
    {if (canCollapse) {
      HTMLUtil.collapsable(renderChildren)
    }
    else {
      renderChildren
    }}
  </div>


  def canCollapse = {
    !all(JavaParser.RBRACE).isEmpty
  }

  def part = {
    var got = false;

    basicChildren.partition(
      tree =>
        !got && tree.getType == JavaParser.LBRACE && {
          got = true;
          false
        });
  }
}
