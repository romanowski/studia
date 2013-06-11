package edu.romanow.smartcode.trees

import org.antlr.runtime.Token
import tools.nsc.interpreter.Line
import edu.romanow.antlr.JavaParser

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/20/12
 * Time: 11:04 PM
 * To change this template use File | Settings | File Templates.
 */
class ImportTree(t: Token) extends LineTree(t) {


  lazy val importName = all(JavaParser.IDENTIFIER).map(_.getText) mkString (".")

  lazy val className = all(JavaParser.IDENTIFIER).lastOption.map(_.getText).getOrElse("")

}
