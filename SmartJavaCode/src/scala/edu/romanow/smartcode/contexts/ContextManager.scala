package edu.romanow.smartcode.contexts

import net.liftweb.common.Box

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/17/12
 * Time: 7:46 AM
 * To change this template use File | Settings | File Templates.
 */
object ContextManager {

  var classMap = Map[String, ClassContext]()


  def putClass(fullName: String, context: ClassContext) = {
    classMap = classMap + (fullName -> context)
  }

  def get(className: String): Box[ClassContext] = Box(classMap.get(className))
}
