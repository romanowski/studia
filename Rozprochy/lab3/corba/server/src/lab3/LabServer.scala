package lab3

import org.omg.CORBA.ORB
import org.omg.CosNaming.NamingContextExtHelper
import io.Source
import java.io.File
import org.omg.PortableServer.{POA, POAHelper}
;

/**
 * Created by IntelliJ IDEA.
 * User: jar
 * Date: 22.04.12
 * Time: 17:46
 * To change this template use File | Settings | File Templates.
 */

object LabServer {

  val address = try {
    Source.fromFile(new File("corba.server")).mkString
  }
  catch {
    case e => e.printStackTrace()
    ""
  }
  println(address)

  // get reference to rootpoa & activate the POAManager
  var rootpoa: POA = null;


  def run(args: Array[String]) = {

    val orb = ORB.init(args, null);

    rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

    rootpoa.the_POAManager().activate();

    // register it with the POA and get the object reference
    rootpoa.activate_object(DevManagerI);

    val ref = rootpoa.servant_to_reference(DevManagerI);

    val nsRef: org.omg.CORBA.Object = if (address == "")
      orb.resolve_initial_references("NameService")
    else
      orb.string_to_object(address);

    val namingService = NamingContextExtHelper.narrow(nsRef);

    val path = namingService.to_name("DevManager")

    namingService.rebind(path, ref)
    orb.run()
  };
}

object Server {


  def main(args: Array[String]): Unit = {

    LabServer.run(args)
  }
}