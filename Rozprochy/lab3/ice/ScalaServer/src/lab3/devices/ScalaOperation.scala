package lab3.devices

import Laboratory.Operation

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 24.04.12
 * Time: 07:31
 * To change this template use File | Settings | File Templates.
 */

case class ScalaOperation(name: String, func: Array[String] => Unit, pramsType: String*) {

  val operation = new Operation(name, pramsType.toArray)

}
