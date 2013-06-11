package edu.romanow

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 22.05.12
 * Time: 06:04
 * To change this template use File | Settings | File Templates.
 */

object Server extends Ice.Application {

  import Ice.Application.communicator

  lazy val ice = communicator()

  lazy val state = new ServerState

  def run(args: Array[String]): Int = {


    communicator().getProperties.getCommandLineOptions.foreach(println(_))

    val adapter = communicator().createObjectAdapter("server")

    try {

      val mgr = new LabI
      adapter.add(mgr, ice.stringToIdentity("dev"))


      val devManager = new BaseManager(state)


      adapter.addServantLocator(devManager, "Monitor")

      adapter.addServantLocator(devManager, "Camera")

      adapter.addServantLocator(devManager, "Dev")


      adapter.activate();

      println("Started at endpoints:")
      adapter.getEndpoints.foreach(println(_))

      ice.waitForShutdown()
    }
    catch {
      case e => e.printStackTrace()
    }
    finally {
      ice.shutdown()
    }
    0
  }


}

object ScalaServer {

  def main(args: Array[String]) {

    Server.main("MyLabServer", args, "config.server")

  }

}