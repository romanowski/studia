package lab3.devices

import com.sun.corba.se.spi.orbutil.fsm.StateImpl
import lab3.{LabServer, StateI}
import Laboratory.{DevS, DevPrxHelper}
import Displays.{_MonitorDisp, MonitorPrxHelper, Monitor}

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 22.04.12
 * Time: 23:19
 * To change this template use File | Settings | File Templates.
 */

class SimpleMonitor(val dev: DevS) extends _MonitorDisp with DevT {


  val myID = Ice.Util.stringToIdentity(java.util.UUID.randomUUID().toString())


  LabServer.appAdapter.add(this, myID)

  val prx = MonitorPrxHelper.uncheckedCast(LabServer.appAdapter.createProxy(myID))



  override def rotate(degree: Int, __current: Ice.Current) {

    val msg = "Rotate on %d degrees".format(degree);
    devState.setState(msg)
    println(msg);
  }

  _operations += "rotate" ->  ScalaOperation("rotate", arr => rotate(arr(0).toInt), "int")

}


class MegaMonitor(val dev: DevS) extends _MonitorDisp with DevT {


  val myID = Ice.Util.stringToIdentity(java.util.UUID.randomUUID().toString())


  LabServer.appAdapter.add(this, myID)

  val prx = MonitorPrxHelper.uncheckedCast(LabServer.appAdapter.createProxy(myID))

  var lastState: StateI = StateI("sleeping");


  override def rotate(degree: Int, __current: Ice.Current) {

    val msg = "Rotate on %d degrees %s".format(degree, (if (degree.abs > 45) " and change me" else ""));
    devState.setState(msg)
    println(msg);
  }


  _operations += "rotate" ->  ScalaOperation("rotate", arr => rotate(arr(0).toInt), "int")
}