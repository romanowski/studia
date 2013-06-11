package edu.romanow.smartcode.trees

import org.antlr.runtime.Token

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 08.05.12
 * Time: 09:48
 * To change this template use File | Settings | File Templates.
 */

class LineTree(t: Token) extends ImaginaryTree(t) {

  override def asXML() = <div class={"line"} number={t.getLine.toString}>
    {super.asXML}
  </div>

}
