package edu.romanow.smartcode.contexts

import edu.romanow.smartcode.trees._
import net.liftweb.common.Full


/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/15/12
 * Time: 3:08 PM
 * To change this template use File | Settings | File Templates.
 */
object ContextTreeWalker {


  def walk(tree: BaseTree): BaseTree = {
    tree.basicChildren.foreach(base =>
      base.basicChildren.foreach {
        child =>
          child.context = base.context
          walkInner(child)
      })
    tree
  }

  def walkInner(tree: BaseTree): BaseTree = {
    val context = tree match {
      case clazz: EnumClassTree => handleClass(clazz)
      case block: BlockContext => tree.context.createBlockContext
      case func: FunctionTree => handleFunc(func)
      case v: VariableTree => handleVariable(v)

      case _ => tree.context
    }
    tree.basicChildren.foreach {
      tree =>
        tree.context = context
        walkInner(tree)
    }
    tree
  }


  def handleClass(tree: EnumClassTree): ClassContext = {
    tree.context.putInnerClass(tree.classObj)
    val newCtx = tree.context.createInnerClazzContext(tree.classObj)
    tree.context = newCtx
    newCtx
  }

  def handleFunc(tree: FunctionTree): FunctionContext = {
    val loc = tree.context.putFunction(tree.function)
    val newCtx = tree.context.createFuncContext(tree.function)
    tree.context = newCtx
    tree.loc = Full(loc)
    newCtx
  }

  def handleVariable(tree: VariableTree): Context = {
    tree.context.putVar(tree.variable)
    tree.context
  }
}
