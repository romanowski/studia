package lab3

import Ice.ObjectAdapter

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 22.04.12
 * Time: 17:46
 * To change this template use File | Settings | File Templates.
 */

object LabServer extends Ice.Application {


  var appAdapter: Ice.ObjectAdapter = null;

  import Ice.Application.communicator

  def run(args: Array[String]): Int = {

    appAdapter = communicator().createObjectAdapter("LapServer");
    appAdapter.add(DevManagerI, communicator().stringToIdentity("DevManager"));
    appAdapter.getPublishedEndpoints.foreach( println(_))

    println(communicator().stringToIdentity("DevManager").toString)
    println(communicator().stringToIdentity("DevManager").name +" "+ communicator().stringToIdentity("DevManager").category)
    appAdapter.activate()

    Ice.Application.communicator().waitForShutdown
    return 0
  }


}

object Server {
  def main(args: Array[String]): Unit = {
    var app = LabServer
    var status: Int = app.main("Server", args, "config.server")
    System.exit(status)
  }
}