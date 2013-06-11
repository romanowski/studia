package edu.romanow.smartcode.contexts

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/15/12
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
object ClassFileMapper {

  def map(className: String): String = {
    className.mkString("/", "/", ".html")
  }

}
