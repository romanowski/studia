package lab3

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 23.04.12
 * Time: 18:59
 * To change this template use File | Settings | File Templates.
 */

import devices.{DevT, DeviceFactory}
import java.io.File
import collection.mutable.{SynchronizedMap, HashMap, SynchronizedSet, HashSet, Set, Map}
import io.Source
import org.omg.CORBA.Object
import Laboratory._

object DevManagerI extends DevManagerPOA {

  type DevType = org.omg.PortableServer.Servant with DevT

  val connectedUsers: Set[String] = new HashSet[String]() with SynchronizedSet[String]

  val createdDev: Map[String, DevType] = new HashMap[String, DevType]() with SynchronizedMap[String, DevType]

  val reservedDev: Map[String, (DevType, String)] = new HashMap[String, (DevType, String)]() with SynchronizedMap[String, (DevType, String)]

  def extract_info(line: String): DevS = {
    val id :: devType :: _ = line.split(" ").toList
    new DevS(id, devType)
  }


  lazy val infos = (try {
    Source.fromFile(new File("dev.txt")).mkString
  }
  catch {
    case e => e.printStackTrace();
    "1 monitor:mega\n2 camera:simple"
  })
    .split("\n").map(extract_info _).toArray

  override def getDevsInfo(): Array[DevS] = {
    println("getDevsInfo" + infos)
    infos
  }


  override def viewDev(ID: String): DevS =
    infos.filter(_.ID == ID).headOption.getOrElse(throw ApplicationExceptionO("no such device!: + ID"))

  override def connect(): String = {
    val token = System.nanoTime().toString;
    connectedUsers += token
    token
  }

  override def reserveDev(ID: String, accessToken: String): Dev = {

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


    DevHelper.narrow(LabServer.rootpoa.servant_to_reference(dev))

  }


  override def disconnect(accessToken: String): Unit = {
    if (!connectedUsers.contains(accessToken)) {
      throw ApplicationExceptionO("not connected. Connetct first")
    }

    reservedDev.filter(el => el._2._1 == accessToken).map(_._1).foreach(reservedDev.remove(_))

    connectedUsers -= accessToken
  }

  override def devState(ID: String): State = {
    val dev = createdDev.get(ID).getOrElse {
      val tmp = createDev(viewDev(ID))
      createdDev.put(ID, tmp)
      tmp
    }
    StateHelper.narrow(LabServer.rootpoa.servant_to_reference(dev.devState))
  }

  private def createDev(info: DevS): DevType = DeviceFactory.createDevice(info)

  def relaseDev(ID: String, accessToken: String): Unit = {
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

}
