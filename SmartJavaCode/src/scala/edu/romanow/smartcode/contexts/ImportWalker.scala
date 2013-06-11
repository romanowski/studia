package edu.romanow.smartcode.contexts

import edu.romanow.smartcode.trees._


/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/21/12
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
object ImportWalker {
  def walk(compilationUnit: BaseTree): BaseTree = {

    compilationUnit.basicChildren.foreach {
      tree => tree match {
        case clazz: EnumClassTree =>
          clazz.context.putInnerClass(clazz.classObj)
          val newCtx = clazz.context.createInnerClazzContext(clazz.classObj)
          clazz.context = newCtx
        case pck: PCKTree =>
          tree.context.fileContext.packageName = pck.packageName
        case importTree: ImportTree =>
          tree.context.fileContext.putImport(importTree.className, importTree.importName)
        case _ =>
      }
    }
    compilationUnit
  }
}
