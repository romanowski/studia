package edu.romanow.dev

import Laboratory._CameraDisp
import Ice.Current
import edu.romanow.Server

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 22.05.12
 * Time: 06:54
 * To change this template use File | Settings | File Templates.
 */

class MegaCamera extends _CameraDisp with DevT {


  def move(x: Int, y: Int, access_token:String,  __current: Current) {
    Server.state.validateOwnership(access_token, this)
    val msg = "MEGA MOVED to %d:%d".format(x, y);
    notify(msg);
    println(msg);
  }

  def zoom(level: Int, access_token:String, __current: Current) {
    Server.state.validateOwnership(access_token, this)
    val msg = "ZOOMED to level %d".format(level);
    notify(msg);
    println(msg);
  }
}
