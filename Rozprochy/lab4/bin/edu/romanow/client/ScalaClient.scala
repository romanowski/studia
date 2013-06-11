package edu.romanow.client

import edu.romanow.SimpleLister
import Ice.Identity
import Laboratory._
import java.util.concurrent.TimeUnit
import java.util.{Scanner, UUID}
import scala._


/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 22.05.12
 * Time: 06:04
 * To change this template use File | Settings | File Templates.
 */

object ScalaClient extends Ice.Application {

  lazy val ice = Ice.Application.communicator()


  lazy val endpoints = ice.getProperties.getProperty("server.endpoints")

  def objProps(name: String, category: String = ""): String = {
    (if (category == "")
      ""
    else
      category + """/""") + name + ":" + endpoints
  }


  def run(args: Array[String]): Int = {

    val login = "myID"


    val pass = System.nanoTime().toString

    val state = new ClientState(objProps("dev"), login, pass, ice)

    if (state.base == null) {
      throw new RuntimeException("bad proxy")
    }

    var line = " /help"

    val scaner = new Scanner(System.in)

    var work= true

    try {
      while (work) {
        line = scaner.nextLine()
        try {
          line.split("\\s+").toList match {
            case "\\q" :: _ => work = false
            case "listen" :: name :: cat :: _ => state.listen(objProps(name, cat))
            case "unlisten" :: name :: cat :: _ => state.unlisten(objProps(name, cat))
            case "desc" :: _ => state.state()
            case "zoom" :: name :: "Camera" :: x :: _ => state.zoomCamera(objProps(name, "Camera"), x)
            case "move" :: name :: "Camera" :: x :: y :: _ => state.moveCamera(objProps(name, "Camera"), x, y)
            case "move" :: name :: "Monitor" :: x :: y :: _ => state.moveMonitor(objProps(name, "Monitor"), x, y)
            case "take" :: name :: cat :: _ => state.take(objProps(name, cat))
            case "free" :: name :: cat :: _ => state.free(objProps(name, cat))
            case _ => println("help: ... TODO")
          }
        }
        catch {
          case e => e.printStackTrace()
        }

      }
      println("ending by by!")
    }
    finally {
      state.base.disconnect(state.access_token)
    }


    ice.shutdown()

    0;
  }

  def main(args: Array[String]) {

    main("Server", args, "config.client")

  }


}
