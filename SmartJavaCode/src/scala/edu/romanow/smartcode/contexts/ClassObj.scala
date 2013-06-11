package edu.romanow.smartcode.contexts

import net.liftweb.common.Box

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/15/12
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
case class ClassObj(name: String, packageName: String, outerClass: Box[ClassObj], parentClass: Box[String], interfaces: List[String]) {
  def fullName = "%s.%s".format(packageName, name)

  def shortName: String = outerClass.map(_.shortName + ".").getOrElse("") + name

  def simpleName = name

}
