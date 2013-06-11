package lab3.devices

import Recorder.{CameraPOA, Camera}
import Laboratory.DevS
import lab3.{LabServer, StateI}

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 22.04.12
 * Time: 23:52
 * To change this template use File | Settings | File Templates.
 */

class MegaCamera(override val info: DevS) extends CameraPOA with DevT {


  LabServer.rootpoa.activate_object(this)

  def rotate(xy: Int, z: Int) {

    val msg = "Rotate on %d degrees on xy and %d on z".format(xy, z);
    devState.setState(msg)
    println(msg);

  }

  def zoom(zoomLevel: Int) {
    val msg = "Zooming: %d".format(zoomLevel);
    devState.setState(msg)
    println(msg);
  }


  _operations += "zoom" -> ScalaOperation("zoom", arr => zoom(arr(0).toInt), "int")
  _operations += "rotate" -> ScalaOperation("rotate", arr => rotate(arr(0).toInt, arr(1).toInt), "int", "int")
}


class SimpleCamera(override val info: DevS) extends CameraPOA with DevT {

  LabServer.rootpoa.activate_object(this)


  def rotate(xy: Int, z: Int) {

    val msg = "Rotate on %d degrees on xy and %d on z".format(xy, z);
    devState.setState(msg)
    println(msg);

  }

  def zoom(zoomLevel: Int) {
    val msg = "I cannot zoom! ;("
    devState.setState(msg)
    println(msg);
  }

  _operations += "zoom" -> ScalaOperation("zoom", arr => zoom(arr(0).toInt), "int")
  _operations += "rotate" -> ScalaOperation("rotate", arr => rotate(arr(0).toInt, arr(1).toInt), "int", "int")
}