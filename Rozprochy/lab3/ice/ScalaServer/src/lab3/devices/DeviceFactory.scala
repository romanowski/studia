package lab3.devices

import lab3.ApplicationExceptionO
import Laboratory.{DevS, Dev}

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 22.04.12
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */

import lab3.DevManagerI.DevP

object DeviceFactory {

  def createDevice(info: DevS): DevP = {
    val dev: DevT = info.devType.split(":").toList match {

      case "monitor" :: "mega" :: _ => new MegaMonitor(info)
      case "monitor" :: "simple" :: _ => new SimpleMonitor(info)
      case "camera" :: "mega" :: _ => new MegaCamera(info)
      case "camera" :: "simple" :: _ => new SimpleCamera(info)
      case list => throw ApplicationExceptionO("bad type:" + info.devType)
    }
    dev
  }


}
