package edu.romanow

import Laboratory._ListenerDisp
import Ice.Current

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 22.05.12
 * Time: 09:05
 * To change this template use File | Settings | File Templates.
 */

case class SimpleLister(my_id: String) extends _ListenerDisp {

  def listen(msg: String, __current: Current) {
    println("Mam message: " + msg)
  }

}
