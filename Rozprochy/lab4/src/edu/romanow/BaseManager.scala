package edu.romanow

import dev.DevT
import Laboratory.{MonitorPrx, Monitor}
import Ice._


/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 22.05.12
 * Time: 06:22
 * To change this template use File | Settings | File Templates.
 */


class BaseManager(sevantsMap: ServerState) extends Ice.ServantLocator {


  def locate(curr: Current, cookie: LocalObjectHolder): Ice.Object with DevT = {
    sevantsMap.getOrCreate(curr)
  }


  def finished(curr: Current, servant: Object, cookie: Any) {
    sevantsMap.evict()
  }

  def deactivate(category: String) {
  }
}
