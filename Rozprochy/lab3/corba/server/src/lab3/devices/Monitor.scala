package lab3.devices

import Displays.{MonitorPOA, Monitor}
import Laboratory.DevS
import lab3.{LabServer, StateI}


/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 22.04.12
 * Time: 23:19
 * To change this template use File | Settings | File Templates.
 */

class SimpleMonitor(override val info: DevS) extends MonitorPOA with DevT {


  LabServer.rootpoa.activate_object(this)


  override def rotate(degree: Int) {

    val msg = "Rotate on %d degrees".format(degree);
    devState.setState(msg)
    println(msg);
  }

  _operations += "rotate" ->  ScalaOperation("rotate", arr => rotate(arr(0).toInt), "int")
}


class MegaMonitor(override val info: DevS) extends MonitorPOA with DevT {


  LabServer.rootpoa.activate_object(this)

  override def rotate(degree: Int) {

    val msg = "Rotate on %d degrees %s".format(degree, (if (degree.abs > 45) " and change me" else ""));
    devState.setState(msg)
    println(msg);
  }

  _operations += "rotate" -> ScalaOperation("rotate", arr => rotate(arr(0).toInt), "int")

}