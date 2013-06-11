package JavaGeniueCode

import io.Source
import java.io.File
import edu.romanow.antlr.JavaParser
import edu.romanow.antlr.JavaLexer
import org.antlr.runtime.{ANTLRFileStream, ANTLRStringStream, CommonTokenStream}
import org.antlr.runtime.tree.{BaseTree, CommonTree}

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 30.04.12
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */

object Test1 {

  def parser(file: String) = {
    val in = new ANTLRFileStream(file)
    new JavaParser(new CommonTokenStream(new JavaLexer(in)));
  }

  def printTree(obj: AnyRef): String = {
    obj match {
      case tree: CommonTree => printTree(tree, " ").mkString("\n")
      "OK done"
      case _ => " no tree!" + obj.getClass
    }
  }


  def printTree(tree: CommonTree, int: String = "a"): List[String] = {
    if (tree == null) return Nil
    val childs = tree.getChildCount

    (0 to childs).foldLeft(List[String]()) {
      (list, i) =>
        val child = tree.getChild(i)

        if (child != null) {
          println(int + child + " " + child.getClass)
          (int + tree.getChild(i).toString) :: (printTree(child.asInstanceOf[CommonTree], int + "a") ::: list)
        }
        else
          list
    }


  }


  def manageChild(child: BaseTree) = {
    child match {
      case ct: CommonTree =>
        println("common: " + ct + " " + ct.getType + ct.getToken)
    }
  }

  def main(args:Array[String]){

    val parser = Test1.parser("samples/_DevManagerDisp.java")

   // parser.setTreeAdaptor(new )


  }

}