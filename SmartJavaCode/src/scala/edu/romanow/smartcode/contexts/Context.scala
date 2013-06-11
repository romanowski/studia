package edu.romanow.smartcode.contexts

import net.liftweb.common.{Empty, Full, Box}


/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/15/12
 * Time: 11:11 AM
 * To change this template use File | Settings | File Templates.
 */
case class FileContext(fileName: String) {
  var imports = Map[String, String]()

  def classContext(className: String) = {
    Box(imports.get(className)) or Full(packageName + "." + className) flatMap (ContextManager.get _)
  }

  def fullName(className: String) =
    Box(imports.get(className))

  def classLocation(className: String): Box[ClassLocation] = {
    classContext(className) map (_.thisLoc)
  }


  def putImport(name: String, fullName: String) {
    imports = imports + (name -> fullName)
  }

  var packageName: String = fileName.split("/").dropRight(1).mkString(".")

}

abstract class Context {

  def thisLoc: ClassLocation = thisClass.thisLoc

  def id: String

  def thisClass: ClassContext = parentContext.open_!.thisClass

  def nextInnerId = thisClass.nextInnerID


  def parentContext: Box[Context]

  var functions = Map[String, List[FuncLocation]]()
  var vars = Map[String, VarLoc]()
  var innerClasses = Map[String, ClassLocation]();


  def putInnerClass(clazz: ClassObj): ClassLocation = {
    thisClass.fileContext.putImport(clazz.simpleName, clazz.fullName)
    val clazzLoc = ClassLocation(clazz, thisClass.fileContext.fileName)
    innerClasses = innerClasses + (clazz.fullName -> clazzLoc)
    innerClasses = innerClasses + (clazz.simpleName -> clazzLoc)
    innerClasses = innerClasses + (clazz.shortName -> clazzLoc)

    thisClass.handleInnerClass(clazzLoc)
    clazzLoc
  }

  def putFunction(func: Function): FuncLocation = {
    val loc = FuncLocation(func, thisClass.thisLoc)
    functions = functions +
      (func.name ->
        (loc :: functions.get(func.name).getOrElse(Nil)))
    loc
  }

  def putVar(v: Variable): VarLoc = {
    val loc = VarLoc(v, id, thisLoc)
    vars = vars + (v.name -> loc)
    loc
  }

  def createInnerClazzContext(clazz: ClassObj) =
    ClassContext(clazz, thisClass.fileContext, Full(this))

  def createBlockContext = new BlockContext(nextInnerId, parentContext)

  def createFuncContext(func: Function) = FunctionContext(func, nextInnerId, Full(this))


  def functionLoc(name: String, argCount: Int): Box[Location] =
    functions.get(name) match {
      case None =>
        parentContext.flatMap(_.functionLoc(name, argCount))
      case Some(h :: Nil) =>
        Full(h)
      case Some(list) =>
        Full(FunctionSelectLocation(file, list: _*))
    }

  def classLoc(name: String): Box[ClassLocation] = {
    thisClass.fileContext.classLocation(name)
  }

  def varLoc(name: String): Box[VarLoc] = {
    Box(vars.get(name)).or(parentContext.flatMap(_.varLoc(name)))
  }

  def file = thisClass.fileContext.fileName

  def fileContext: FileContext = thisClass.fileContext

}

case class ClassContext(clazz: ClassObj, override val fileContext: FileContext, override val parentContext: Box[Context]) extends Context {
  override val thisLoc = ClassLocation(clazz, fileContext.fileName)

  var i = 0;

  def nextInnerID: String = {
    i = i + 1;
    "%s-%s".format(id, i)
  }


  override def thisClass = this

  override def id = thisLoc.id

  var nextId = 0

  def handleInnerClass(clazz: ClassLocation) {
    innerClasses = innerClasses + (clazz.clazz.fullName -> clazz)
    innerClasses = innerClasses + (clazz.clazz.simpleName -> clazz)
  }

  putVar(Variable("this", clazz.fullName))

  clazz.parentClass match {
    case Full(name) => classLoc(name) match {
      case Full(loc) => putVar(Variable("super", name))
      case _ =>
    }
    case _ =>
  }


  lazy val parentClassContext = clazz.parentClass.flatMap(ContextManager.get _)
}

case class BlockContext(override val id: String, override val parentContext: Box[Context]) extends Context {

}

case class FunctionContext(func: Function, override val id: String,
                           override val parentContext: Box[Context]) extends Context {

  func.params.foreach(v => putVar(v))


}

object EmptyClass extends ClassObj("#UnUse", "#packageUNUSe", Empty, Empty, Nil)

case class StartContext(fc: FileContext) extends ClassContext(EmptyClass, fc, Empty) {
  override def createInnerClazzContext(clazz: ClassObj) = {
    val ret = ClassContext(clazz, thisClass.fileContext, Full(this))

    ContextManager.putClass(clazz.fullName, ret)

    ret
  }
}


object GodContext extends Context {
  def id = "GOD"

  def createForFile(name: String) = StartContext(FileContext(name))

  def parentContext = null
}