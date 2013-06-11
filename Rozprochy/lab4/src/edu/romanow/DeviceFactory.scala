package edu.romanow

import dev._
import io.Source
import Ice.Identity

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 22.05.12
 * Time: 07:15
 * To change this template use File | Settings | File Templates.
 */

object DeviceFactory {

  lazy val content = Source.fromFile("dev.txt").getLines().toArray

  lazy val mappedContent = content.map(
    line => line.split(":").toList match {
      case base :: dClass :: _ => base.split("-").toList match {
        case dType :: id :: _ => Some((dType, id), dClass)
        case _ => None
      }
      case _ => None
    }
  ).filterNot(_.isEmpty).map(_.get).toList

  /**
   * creates only instance of class and do nthing more!
   * @param id
   * @return
   */
  def crateDev(id: Identity): Option[Ice.Object with DevT] = {
    mappedContent.filter(_._1 ==(id.category, id.name)) match {
      case dev :: _ =>
        dev._2 match {
          case "SimpleMonitor" => Some(new SimpleMonitor)
          case "MegaMonitor" => Some(new MegaMonitor)
          case "SimpleCamera" => Some(new SimpleCamera)
          case "MegaCamera" => Some(new MegaCamera)
        }
      case _ => None
    }

  }


}
