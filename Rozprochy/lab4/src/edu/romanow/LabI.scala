package edu.romanow

import Ice.Current
import io.Source
import Laboratory.{ListenerPrxHelper, ListenerPrx, _LabolatoryDisp}

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 22.05.12
 * Time: 06:10
 * To change this template use File | Settings | File Templates.
 */

class LabI extends _LabolatoryDisp {
  def describe(__current: Current): Array[String] = DeviceFactory.content

  //TODO valid accessTokens
  def connect(login: String, pass: String, __current: Current) =
    (pass + login).hashCode.toString

  //TODO valid accessTokens
  def disconnect(accessToken: String, __current: Current) {
    Server.state.vaidateToken(accessToken);

  }
}
