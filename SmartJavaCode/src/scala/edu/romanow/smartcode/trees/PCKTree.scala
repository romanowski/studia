package edu.romanow.smartcode.trees

import org.antlr.runtime.Token
import edu.romanow.antlr.JavaParser

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/20/12
 * Time: 10:28 PM
 * To change this template use File | Settings | File Templates.
 */
class PCKTree(t: Token) extends ImaginaryTree(t) {


  lazy val packageName =
    basicChildren.filter(_.getType == JavaParser.IDENTIFIER).map(_.getText).mkString(".")


}
