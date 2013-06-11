package lab3

import Ice.Current
import Laboratory.{StatePrxHelper, DevPrxHelper, _StateDisp}

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 22.04.12
 * Time: 18:00
 * To change this template use File | Settings | File Templates.
 */

case class StateI(val startState: String) extends _StateDisp {

  var _states = startState :: Nil

  val myID = Ice.Util.stringToIdentity(java.util.UUID.randomUUID().toString())


  LabServer.appAdapter.add(this, myID)

  val prx = StatePrxHelper.uncheckedCast(LabServer.appAdapter.createProxy(myID))

  def states(__current: Current): Array[String] = {
    println(_states)
    _states.toArray
  }

  def setState(newState: String, __current: Current): Unit = {

    println("setting state: " + newState)

    _states = newState :: _states;

    println(_states)
  }
}
