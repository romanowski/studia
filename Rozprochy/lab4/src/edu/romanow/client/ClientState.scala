package edu.romanow.client

import edu.romanow.SimpleLister
import Ice.Identity
import java.util.UUID
import actors.Actor
import java.util.concurrent.TimeUnit
import Laboratory._

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 22.05.12
 * Time: 13:32
 * To change this template use File | Settings | File Templates.
 */

class ClientState(proxyId: String, login: String, pass: String, ice: Ice.Communicator) {

  object Ping;

  object StateActor extends Actor {
    def act() {
      while (true) {
        receive {
          case Ping => keepConnection()
        }
      }
    }
  }

  def connect = LabolatoryPrxHelper.checkedCast(ice.stringToProxy(proxyId));

  var base: LabolatoryPrx = connect;

  var listenOn: List[DevicePrx] = Nil
  var controling: List[DevicePrx] = Nil

  val access_token = base.connect(login, pass)

  private val _listener = SimpleLister(access_token)

  val clientAdapter = ice.createObjectAdapter("")

  val listenerPrx = ListenerPrxHelper.uncheckedCast(clientAdapter.add(_listener, new Identity(UUID.randomUUID().toString, "")))

  clientAdapter.activate()


  def keepConnection() {
    try {
      base.ice_ping();
      // println("Pinged!")
    }
    catch {
      case e => e.printStackTrace();
      println("waiting a while and reconnect")
      TimeUnit.SECONDS.sleep(10)

      base = connect
      if (base == null) {
        throw new RuntimeException("can't connect", e)
      }
    }

    TimeUnit.SECONDS.sleep(2)
    StateActor ! Ping
  }

  StateActor.start()
  StateActor ! Ping


  def getDev(what: String): DevicePrx =
    DevicePrxHelper.checkedCast(ice.stringToProxy(what))


  def listen(what: String) {
    val dev = getDev(what)
    dev.ice_getConnection().setAdapter(clientAdapter)
    dev.listen(listenerPrx, access_token)
    println("Start listening")
    listenOn = dev :: listenOn
  }

  def unlisten(what: String) {
    val dev = getDev(what)
    dev.ice_getConnection().setAdapter(clientAdapter)
    dev.abandon(listenerPrx, access_token)
    println("Stop listening")
    listenOn = listenOn.filter(_.ice_id() != dev.ice_id())
  }

  def state() {
    println("Lab state:")
    base.describe().foreach(println(_))
    print("^^^^^^^^^^^^^^^^^^^^^^")
  }

  def take(what: String) {

    val dev = getDev(what)

    dev.use(access_token)
    controling = dev :: controling

    println("Dev: " + what + " taken")
  }

  def free(what: String) {
    val dev = getDev(what)

    dev.free(access_token)
    controling = controling.filterNot(_.ice_id() != dev.ice_id())
    println("Dev: " + what + " free")

  }

  def moveMonitor(what: String, x: String, y: String) {
    val dev = getDev(what)
    MonitorPrxHelper.uncheckedCast(dev).move(x.toInt, y.toInt, access_token)

    println("Dev: " + what + " move")
  }

  def moveCamera(what: String, x: String, y: String) {
    val dev = getDev(what)
    CameraPrxHelper.uncheckedCast(dev).move(x.toInt, y.toInt, access_token)
    println("Dev: " + what + " move")
  }

  def zoomCamera(what: String, level: String) {
    val dev = getDev(what)
    CameraPrxHelper.uncheckedCast(dev).zoom(level.toInt, access_token)
    println("Dev: " + what + " move")
  }

  def end() {
    base.disconnect(access_token)
  }

}
