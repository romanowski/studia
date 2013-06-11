package lab3.devices

import Laboratory.{DevS, DevOperations, DevPOA, Dev}
import lab3.{LabServer, ApplicationExceptionO}

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 22.04.12
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */

import lab3.DevManagerI.DevType

object DeviceFactory {

  def createDevice(info: DevS): DevType = {
    val dev: DevType = info.devType.split(":").toList match {

      case "monitor" :: "mega" :: _ => new MegaMonitor(info)
      case "monitor" :: "simple" :: _ => new SimpleMonitor(info)
      case "camera" :: "mega" :: _ => new MegaCamera(info)
      case "camera" :: "simple" :: _ => new SimpleCamera(info)
      case list => throw ApplicationExceptionO("bad type:" + info.devType)
    }
    dev
  }


}
