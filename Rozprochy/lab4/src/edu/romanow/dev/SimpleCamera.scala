package edu.romanow.dev

import Laboratory._CameraDisp
import Ice.Current
import edu.romanow.Server

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 22.05.12
 * Time: 06:55
 * To change this template use File | Settings | File Templates.
 */

class SimpleCamera extends _CameraDisp with DevT {


  def move(x: Int, y: Int, access_token:String, __current: Current) {
    Server.state.validateOwnership(access_token, this)
    val msg = "Simple MOVED to %d:%d".format(x, y);
    notify(msg);
    println(msg);
  }

  def zoom(level: Int, access_token:String,  __current: Current) {
    Server.state.validateOwnership(access_token, this)
    val msg = "i cant ZOOM"
    notify(msg);
    println(msg);
  }
}
