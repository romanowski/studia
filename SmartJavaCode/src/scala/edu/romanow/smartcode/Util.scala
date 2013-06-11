package edu.romanow.smartcode

import scala.collection.mutable.Set
import org.antlr.runtime.tree.Tree
import edu.romanow.antlr.JavaParser
import trees.BaseTree
import xml.PrettyPrinter
import scala.Predef._
;

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 05.05.12
 * Time: 12:51
 * To change this template use File | Settings | File Templates.
 */

object Util {

  def printTree(tree: Tree, int: String = "\t"): Unit = {

    println(int + tree.getText)
    println("mam:" + tree.getCharPositionInLine + " " + tree.getLine)
    for (i <- 0 to (tree.getChildCount)) {
      val child = tree.getChild(i)
      if (child != null) {
        printTree(child, "\t" + int)
      }
    }

  }


  def reprint_file(tree: Tree) = {


    val tokens: Set[(String, Int, Int)] = Set()
    val bb = new StringBuilder(" " * tree.getTokenStopIndex);

    def walk(tree: Tree) {
      if (tree.getText != null) {
        tokens += ((tree.getText, tree.getLine, tree.getCharPositionInLine))
        if (tree.getText != null) bb.insert(tree.getTokenStartIndex, tree.getText)
      }
      for (i <- 0 to (tree.getChildCount)) {
        val child = tree.getChild(i)
        if (child != null) {
          walk(child)
        }
      }
    }
    println("!!!!!!!!!!!!!!!!!!" + bb.toString())
    walk(tree)

    var last = ("", 0, 0)

    val list = tokens.toList.sortWith((t1, t2) => {
      (t1._2 - t2._2 < 0) || (t1._2 == t2._2 && t1._3 - t2._3 < 0)
    })

    list.foreach(println _)

    val builder = list.foldLeft(new StringBuilder) {
      (builder, el) =>
        el._2 - last._2 match {
          case x if x > 0 =>
            builder.append("\n" * x)
            builder.append(" " * el._3)
            builder.append(el._1)
          case 0 =>
            builder.append(" " * (el._3 - last._3 - last._1.length))
            builder.append(el._1)
          case _ =>
            throw new RuntimeException(el.toString() + last.toString())
        }
        last = el
        builder
    }

    println(builder.toString())
  }


  def createHtml(all: JavaParser.compilationUnit_return): String = {

    val tree = all.getTree.asInstanceOf[BaseTree]



    new PrettyPrinter(80, 4).format(<html>
      <head>
        <LINK href="css/style.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" id="mainscript" src="js/code.js"></script>
      </head> <body>
        {tree.asXML}
      </body>
    </html>).replaceAll("\t", "").replaceAll(" ", "").replaceAll("\n", "")
  }


  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try {
      op(p)
    } finally {
      p.close()
    }
  }

}
