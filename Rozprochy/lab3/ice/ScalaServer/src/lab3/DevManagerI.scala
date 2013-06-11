package lab3

import devices.{DevT, DevProxyTrait, DeviceFactory}
import Ice.Current
import io.Source
import java.io.File
import Laboratory._
import collection.mutable._


/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 22.04.12
 * Time: 17:47
 * To change this template use File | Settings | File Templates.
 */

object DevManagerI extends _DevManagerDisp {

  type DevP = DevT


  val connectedUsers: Set[String] = new HashSet[String]() with SynchronizedSet[String]

  val createdDev: Map[String, DevP] = new HashMap[String, DevP]() with SynchronizedMap[String, DevP]

  val reservedDev: Map[String, (DevP, String)] = new HashMap[String, (DevP, String)]() with SynchronizedMap[String, (DevP, String)]

  def extract_info(line: String): DevS = {
    val id :: devType :: _ = line.split(" ").toList
    DevInfo(id, devType)
  }


  lazy val infos = (try {
    Source.fromFile(new File("dev.txt")).mkString
  }
  catch {
    case e => e.printStackTrace();
    "1 monitor:mega\n2 camera:simple"
  })
    .split("\n").map(extract_info _).toArray

  override def getDevsInfo(current: Ice.Current): Array[DevS] = {
    println("getDevsInfo" + infos)
    infos
  }


  override def viewDev(ID: String, current: Ice.Current): DevS =
    infos.filter(_.ID == ID).headOption.getOrElse(throw ApplicationExceptionO("no such device!: + ID"))

  override def connect(current: Ice.Current): String = {
    val token = System.nanoTime().toString;
    connectedUsers += token
    token
  }

  override def reserveDev(ID: String, accessToken: String, __current: Current): DevPrx = {

    if (!connectedUsers.contains(accessToken)) {
      throw ApplicationExceptionO("not connected. Connetct first")
    }


    val dev = createdDev.get(ID).getOrElse {
      val tmp = createDev(viewDev(ID))
      createdDev.put(ID, tmp)
      tmp
    }


    reservedDev.get(ID) match {
      case Some(_) => throw ApplicationExceptionO("Dev is reserved!")
      case _ => reservedDev.put(ID, (dev, accessToken))
    }
    dev.prx
  }


  override def disconnect(accessToken: String, current: Ice.Current): Unit = {
    if (!connectedUsers.contains(accessToken)) {
      throw ApplicationExceptionO("not connected. Connetct first")
    }

    reservedDev.filter(el => el._2._1 == accessToken).map(_._1).foreach(reservedDev.remove(_))

    connectedUsers -= accessToken
  }

  private def createDev(info: DevS): DevP = {

    val nd = DeviceFactory.createDevice(info)
    nd
  }

  def relaseDev(ID: String, accessToken: String, current: Ice.Current): Unit = {
    if (!connectedUsers.contains(accessToken)) {
      throw ApplicationExceptionO("not connected. Connetct first")
    }
    reservedDev.get(ID) match {
      case Some((_, `accessToken`)) => reservedDev -= ID
      case Some(_) => throw ApplicationExceptionO("Dev is reserved not by You!")
      case _ => throw ApplicationExceptionO("Dev is not reserved")
    }
  }


  val startState = StateI("not started")

  case class DevInfo(val _ID: String, val _devType: String) extends DevS(_ID, _devType)

  override def devState(ID: String, current: Ice.Current) = {
    val dev = createdDev.get(ID).getOrElse {
      val tmp = createDev(viewDev(ID))
      createdDev.put(ID, tmp)
      tmp
    }
    dev.devState.prx
  }

}