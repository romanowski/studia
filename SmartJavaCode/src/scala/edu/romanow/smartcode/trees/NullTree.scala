package edu.romanow.smartcode.trees

import org.antlr.runtime.Token
import org.antlr.runtime.tree.CommonTree

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 08.05.12
 * Time: 10:11
 * To change this template use File | Settings | File Templates.
 */

class NullTree extends ImaginaryTree(null) {
  override def before(t: CommonTree) =
    true

  override def asXML = <div class={"null"}>
    {super.asXML}
  </div>


}
