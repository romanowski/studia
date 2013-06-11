package edu.romanow.dev

import Laboratory._MonitorDisp
import Ice.Current
import edu.romanow.Server

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 22.05.12
 * Time: 06:53
 * To change this template use File | Settings | File Templates.
 */

class SimpleMonitor extends _MonitorDisp with DevT {


  def move(x: Int, y: Int, access_token: String, __current: Current) {
    Server.state.validateOwnership(access_token, this)
    val msg = "i was Simply MOVED to %d:%d".format(x, y);
    notify(msg);
    println(msg);
  }
}

