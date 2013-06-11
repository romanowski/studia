package lab3

import Laboratory.{StatePOA, State}


/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 23.04.12
 * Time: 19:16
 * To change this template use File | Settings | File Templates.
 */

case class StateI(begState: String) extends StatePOA {

  var _states = begState :: Nil

  def states: Array[String] = _states.toArray

  def setState(newState: String): Unit = {
    _states = newState :: _states;
  }

  LabServer.rootpoa.activate_object(this)

}
