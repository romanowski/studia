package lab3.devices

import lab3.StateI
import scala.collection.mutable.Map
import Laboratory._

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 24.04.12
 * Time: 06:28
 * To change this template use File | Settings | File Templates.
 */

trait DevT extends _DevOperations {
  val devState = StateI("Dev ON")

  val _operations: Map[String, ScalaOperation] = Map();

  override def doOperation(name: String, params: Array[String], __current: Ice.Current): Unit = {

    println("try to do %s with params %s".format(name, params.mkString(", ")))

    _operations.get(name) match {
      case Some(op) if op.pramsType.size == params.size => {
        try {
          op.func(params)
        }
        catch {
          case e => e.printStackTrace()
          throw new BadOperation()
        }
      }
      case Some(_) => print("bad args ")
      throw new BadOperation()
      case _ => println("No operation")
      throw new BadOperation()
    }
  }

  override def operations(__current: Ice.Current): Array[Operation] = _operations.map(_._2.operation).toArray

  val prx: DevPrx;

  val dev: DevS

  override def info(__current: Ice.Current) = dev
}
