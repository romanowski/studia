package edu.romanow.smartcode.contexts

import net.liftweb.common.Box
import edu.romanow.smartcode.html.HTMLUtil


/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/15/12
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */

trait Location {
  def file: String

  def id: String = innerId.replace(".", "_")

  def innerId: String


  def fullLink(file: String) = HTMLUtil.onClick(
    this, file)

  def onHover(file: String) = HTMLUtil.onMouseOver(
    this, file)
}

case class SelectLocation(options: Seq[(String, String => String)], override val file: String) extends Location {
  def innerId = "no-grate-inner-id"

  override def fullLink(file: String) =
    HTMLUtil.showChooseWindow(options.map(el => el._1 -> el._2(file)): _*)

  override def onHover(file: String) = ""
}

case class FunctionSelectLocation(f: String, funcs: FuncLocation*) extends SelectLocation(
  funcs.map {
    funcLoc =>
      (funcLoc.niceName, funcLoc.fullLink _)
  }, f)


case class FuncLocation(func: Function, classLoc: ClassLocation) extends Location {
  def file = classLoc.file

  def innerId = "%s_%s-%s-".format(classLoc.id, func.name, func.params.map(_.varType).mkString("-"))

  def fullName = classLoc.fullName + "." + func.name

  def niceName = "%s(%s)".format(fullName, func.params.map(_.varType).mkString(", "))
}

case class NotKnownLocation(name: String) extends Location {
  def file = ""

  def innerId = "not-know"

  override def fullLink(file: String) = ""

  override def onHover(file: String) = HTMLUtil.showBriefInfo(name)
}


case class VarLoc(v: Variable, contextId: String, classLoc: ClassLocation) extends Location {
  def file = classLoc.file

  def innerId = "%s-%s".format(contextId, v.name)
}


case class ClassLocation(clazz: ClassObj, file: String) extends Location {

  def shortId: String = clazz.simpleName


  def fullName = clazz.fullName

  def innerId = clazz.fullName
}


object EmptyLocation extends Location {
  def file = ""

  def innerId = ""

  override def fullLink(file: String) = ""
}

