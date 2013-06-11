package edu.romanow.smartcode

import trees.BaseTree
import xml._
import transform.{RuleTransformer, RewriteRule}
import edu.romanow.antlr.JavaParser._
import scala.Some

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/18/12
 * Time: 11:15 PM
 * To change this template use File | Settings | File Templates.
 */
object HTMLGenerator {

  def generate(tree: BaseTree, basePath: String): Node = {
    val xml =
      <html>
        <head>
          <LINK href={basePath + "/css/style.css"} rel="stylesheet" type="text/css"/>
          <script type={"text/javascript"} id="mainscript" src={basePath + "/js/jquery.js"}></script>
          <script type={"text/javascript"} id="mainscript" src={basePath + "/js/code.js"}></script>
        </head>
        <body>
          {tree.asXML}<span style={tree.style.replace("fixed", "relative")}></span>
          <div id="__info_div" class="floatingWindow"></div>
          <div id="__choose_div" class="floatingWindow"></div>
          <div style="display:none">
            <a href="javascript://" id="__choose__option"></a>
          </div>
        </body>
      </html>

    xml

  }

  def compact(xml: Node): String = {
    //  Utility.trim(xml).toString()
    xml.toString()
  }


  object PosiitoningRule extends RewriteRule {
    override def transform(n: Node) = n match {
      case elm: Elem =>
        (elm.attribute("line"), elm.attribute("pos")) match {
          case (Some(line), Some(pos)) =>
            elm % Attribute("style", Text(itemStyle(line.toString.toInt, pos.toString().toInt)), Null)
          case _ => elm
        }
      case other => other
    }
  }

  object transformer extends RuleTransformer(PosiitoningRule)


  def itemStyle(line: Int, pos: Int): String = {
    "position: fixed; top:%dpx; left:%dpx".format(line * 18, pos * 10)
  }


  def time[A](func: => A, what: String): A = {
    val t = System.nanoTime()

    val out = func

    println("%s took %d millis.".format(what, (System.nanoTime() - t) / 1000000))
    out
  }


  val operators = NEW :: FOR :: WHILE :: IF :: CLASS :: DO :: PUBLIC :: PRIVATE :: STATIC :: Nil

  def classForType(tokenType: Int): String = {
    if (operators contains tokenType) {
      "operator"
    }
    else ""
  }

}
