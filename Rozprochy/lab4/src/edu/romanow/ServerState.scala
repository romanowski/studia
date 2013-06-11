package edu.romanow

import dev.DevT
import actors.Actor
import java.util.concurrent.TimeUnit
import Ice.{Connection, Current, Identity}
import Laboratory.AccessDenied

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 22.05.12
 * Time: 12:27
 * To change this template use File | Settings | File Templates.
 */

class ServerState {

  object Ping;

  object StateActor extends Actor {
    def act() {
      while (true) {
        receive {
          case Ping =>
            keepConnection()
        }
      }
    }
  }


  private var servants = Map[Identity, Ice.Object with DevT]()

  private var connectedClient = List[(String, Ice.ObjectPrx)]()

  def evict() {
    servants = servants.filterNot {
      s =>
        if (s._2.unused_?) {
          println("evicitng: " + s._1)
          true
        }
        else false
    };
  }

  def addNewListener(access_token: String, obj: Ice.ObjectPrx) {
    connectedClient = (access_token, obj) :: connectedClient

  }

  def getOrCreate(curr: Current): Ice.Object with DevT = {
    servants.get(curr.id) match {
      case Some(dev) =>
        println("reuse device: " + dev.ice_id())
        dev
      case _ => {
        val dev = DeviceFactory.crateDev(curr.id).getOrElse(return null)
        println("create monitor: " + curr.id.category + "/" + curr.id.name)
        servants = servants + ((curr.id, dev))
        dev
      }
    }
  }

  def keepConnection() {


    connectedClient.foreach {
      cl => try {
        try {
          cl._2.ice_ping()
          //  println("client " + cl._1 + " pinged! ")
        }
        catch {
          case e =>
            e.printStackTrace()
            connectedClient = connectedClient.filter(_._1 != cl._1)
          //TODO remove client
        }
      }
    }
    //TODO some other time
    TimeUnit.SECONDS.sleep(1)
    StateActor ! Ping

  }

  keepConnection()
  StateActor.start()


  def vaidateToken(access_token: String) = {
    //to jakies sprawdzanie czy dodane itp...
  }

  def validateOwnership(access_token: String, dev: DevT) {
    dev.owner match {
      case Some(token) if token == access_token => ""
      case _ => throw new AccessDenied
    }
  }
}
