package edu.romanow.smartcode

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 08.05.12
 * Time: 19:37
 * To change this template use File | Settings | File Templates.
 */

object Helpers {


  def tryo[A](f: => A): Option[A] = {
    try {
      Some(f)
    }
    catch {
      case e => e.printStackTrace()
      None
    }
  }

}
