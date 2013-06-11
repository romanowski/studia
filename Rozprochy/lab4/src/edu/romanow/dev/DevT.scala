package edu.romanow.dev

import edu.romanow.Server
import Laboratory.{ListenerPrxHelper, ListenerPrx, _DeviceOperations, Listener}
import Ice.{Identity, Current}

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 22.05.12
 * Time: 06:39
 * To change this template use File | Settings | File Templates.
 */

trait DevT extends _DeviceOperations {

  protected var listeners = Map[String, ListenerPrx]()

  var owner: Option[String] = None


  //TODO valid accessTokens
  override def listen(list_p: ListenerPrx, accessToken: String, __current: Current) {

    Server.state.vaidateToken(accessToken);

    val list = ListenerPrxHelper.uncheckedCast(
      __current.con.createProxy(list_p.ice_getIdentity()).ice_oneway())

    list.listen("connected to dev: " + __current.id.name)

    println("listing done!")

    Server.state.addNewListener(accessToken, list)

    listeners.get(accessToken) match {
      case None => listeners = listeners + ((accessToken, list))
      case _ => ""
    }
  }


  override def abandon(list_p: ListenerPrx, accessToken: String, __current: Current) {

    Server.state.vaidateToken(accessToken);


    val list = ListenerPrxHelper.uncheckedCast(
      __current.con.createProxy(list_p.ice_getIdentity()).ice_oneway())

    list.listen("disconnected from dev " + __current.id.name)

    listeners = listeners.filter(_._1 != list)
  }


  def notify(what: String) {
    listeners.foreach(list => try {
      println(list._2.ice_getIdentity().name)
      list._2.listen(what)
    }
    catch {
      case e => e.printStackTrace()
      listeners = listeners.filter(_._1 != list._1)
      owner match {
        case Some(s) if s == list._1 => owner = None
        case _ => ""
      }
    })
  }

  def unused_? : Boolean = listeners.isEmpty && owner.isEmpty

  def owned_? : Boolean = !owner.isEmpty


  override def use(accessToken: String, __current: Current): Boolean = {

    Server.state.vaidateToken(accessToken);


    owner match {
      case Some(_) =>
        false
      case _ =>
        owner = Some(accessToken)
        true
    }
  };

  override def free(access_token: String, __current: Current) {
    Server.state.vaidateToken(access_token);
    owner match {
      case Some(oId) if oId == access_token =>
        owner = None
      case _ => ""
    }
  }

}
