package edu.romanow.smartcode.html

import edu.romanow.smartcode.contexts.Location
import net.liftweb.util.JsonCmd
import xml.NodeSeq
import edu.romanow.smartcode.FileUtils

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/17/12
 * Time: 7:53 AM
 * To change this template use File | Settings | File Templates.
 */
object HTMLUtil {

  def onClick(loc: Location, file: String): String = {
    if (loc.file == file) {
      scrollTo(loc.id)
    } else {
      redirectTo("%s?id=%s".format(FileUtils.relPath(file, loc.file), loc.id))
    }
  }

  def onMouseOver(loc: Location, file: String) = {
    if (loc.file == file) {
      myFunc("showInfo", "this", str(loc.id))
    } else {
      myFunc("showInfo", "this", str(loc.id), str(FileUtils.relPath(file, loc.file)))
    }
  }

  def onMouseOut = myFunc("hideInfo")


  def redirectTo(loc: String): String = {
    myFunc("redirect", str(loc))
  }

  def scrollTo(id: String): String = {
    myFunc("scrollTo", str(id))
  }

  def showBriefInfo(what: String): String = {
    myFunc("showBriefInfo", str(what), "this")
  }


  def myFunc(name: String, args: String*): String = {
    "SmartCode.%s(%s);".format(name, args.mkString(", "))
  }


  def str(v: String): String = "'%s'".format(v)


  def concat(col: Seq[NodeSeq]): NodeSeq =
    col.foldLeft(NodeSeq.Empty)(_ ++ _)


  def toggleCollapse(id: String): String = {
    myFunc("toggleCollapse", str(id))
  }

  def showChooseWindow(options: (String, String)*): String = {

    options.map(el => "%s: %s".format(el._1, el._2))
    myFunc("showOptions", options.map(el => "'%s': \"%s\"".format(el._1, el._2)).mkString("{", ",", "}"), "this")
  }


  def collapsable(innerHtml: NodeSeq) = {
    val id = System.nanoTime().toString
    <span id={id}>
      <span class="blockHide hide">
        <a href="javascript://" onclick={toggleCollapse(id)}>...</a>{"}"}
      </span>
      <span class="blockShow">
        <span onclick={toggleCollapse(id)} class="collapseButton">-</span>
        <span>
          {innerHtml}
        </span>
      </span>
    </span>
  }
}

