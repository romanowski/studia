package lab3.devices

import Displays.MonitorPrxHelper
import lab3.{StateI, LabServer}
import lab3.StateI._
import Laboratory.DevS
import Recorder.{_CameraDisp, CameraPrxHelper, Camera}

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 22.04.12
 * Time: 23:52
 * To change this template use File | Settings | File Templates.
 */

class MegaCamera(val dev: DevS) extends _CameraDisp with DevT {


  val myID = Ice.Util.stringToIdentity(java.util.UUID.randomUUID().toString())


  LabServer.appAdapter.add(this, myID)

  val prx = CameraPrxHelper.uncheckedCast(LabServer.appAdapter.createProxy(myID))


  def rotate(xy: Int, z: Int, __current: Ice.Current) {

    val msg = "Rotate on %d degrees on xy and %d on z".format(xy, z);
    devState.setState(msg)
    println(msg);

  }

  def zoom(zoomLevel: Int, __current: Ice.Current) {
    val msg = "Zooming: %d".format(zoomLevel);
    devState.setState(msg)
    println(msg);
  }



  _operations += "zoom" -> ScalaOperation("zoom", arr => zoom(arr(0).toInt), "int")
  _operations += "rotate" -> ScalaOperation("rotate", arr => rotate(arr(0).toInt, arr(1).toInt), "int", "int")
}


class SimpleCamera(val dev: DevS) extends _CameraDisp with DevT {


  val myID = Ice.Util.stringToIdentity(java.util.UUID.randomUUID().toString())


  LabServer.appAdapter.add(this, myID)

  val prx = CameraPrxHelper.uncheckedCast(LabServer.appAdapter.createProxy(myID))


  def rotate(xy: Int, z: Int, __current: Ice.Current) {

    val msg = "Rotate on %d degrees on xy and %d on z".format(xy, z);
    devState.setState(msg)
    println(msg);

  }

  def zoom(zoomLevel: Int, __current: Ice.Current) {
    val msg = "I cannot zoom! ;("
    devState.setState(msg)
    println(msg);
  }



  _operations += "zoom" -> ScalaOperation("zoom", arr => zoom(arr(0).toInt), "int")
  _operations += "rotate" -> ScalaOperation("rotate", arr => rotate(arr(0).toInt, arr(1).toInt), "int", "int")
}