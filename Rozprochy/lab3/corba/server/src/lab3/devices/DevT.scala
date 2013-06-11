package lab3.devices

import lab3.StateI._
import lab3.StateI
import scala.collection.mutable.Map
import Laboratory.{DevOperations, Operation, BadOperation}

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 24.04.12
 * Time: 06:28
 * To change this template use File | Settings | File Templates.
 */

trait DevT extends DevOperations {
  val devState = StateI("Dev ON")

  val _operations: Map[String, ScalaOperation] = Map();

  override def doOperation(name: String, params: Array[String]): Unit = {

    println("try to do %s with params %s".format(name, params.mkString(", ")))

    _operations.get(name) match {
      case Some(op) if op.pramsType.size == params.size => {
        try {
          op.func(params)
        }
        catch {
          case e => e.printStackTrace()
          throw new BadOperation("Error in operation")
        }
      }
      case Some(_) =>  print("bad args ")
        throw new BadOperation("bad args ")
      case _ => println("No operation")
        throw new BadOperation("No operation")
    }
  }

  override def operations: Array[Operation] = _operations.map(_._2.operation).toArray

}
