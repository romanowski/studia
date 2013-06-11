package org.corba.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.corba.generated.I1;
import org.corba.generated.I1Helper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class Server {


	public Server()
	{
	}
	
	public void base1(String[] args) 
		throws InvalidName, AdapterInactive, ServantAlreadyActive, WrongPolicy, ServantNotActive, IOException
	{
		// create and initialize the ORB
		ORB orb = ORB.init( args, null );
		
		// get reference to rootpoa & activate the POAManager
		POA rootpoa = POAHelper.narrow(orb.resolve_initial_references( "RootPOA" ));

		rootpoa.the_POAManager().activate();
		
		// create servant
		I1Impl i1Impl = new I1Impl(rootpoa);

		// register it with the POA and get the object reference
		
		rootpoa.activate_object(i1Impl);
		
		org.omg.CORBA.Object ref = rootpoa.servant_to_reference(i1Impl);

		// export object reference to a file
		PrintWriter pw = new PrintWriter(new FileWriter("Test.IOR"));
		pw.print(orb.object_to_string(ref));
		pw.close();
		
		// wait for invocations from clients
		orb.run();
	}

	
	public void base2(String[] args) throws InvalidName, AdapterInactive, ServantNotActive, WrongPolicy, IOException, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound, CannotProceed, ServantAlreadyActive, ObjectAlreadyActive
	{
		// create and initialize the ORB
		ORB orb = ORB.init( args, null );
		
		// get reference to rootpoa & activate the POAManager
		POA rootpoa = POAHelper.narrow(orb.resolve_initial_references( "RootPOA" ));

		rootpoa.the_POAManager().activate();
		
		// create servant
		I1Impl i1Impl = new I1Impl(rootpoa);
	
		// register it with the POA and get the object reference
		//rootpoa.activate_object_with_id("moj_pierwszy_serwant".getBytes(), i1Impl);
		rootpoa.activate_object(i1Impl);
		
		org.omg.CORBA.Object ref = rootpoa.servant_to_reference(i1Impl);

		org.omg.CORBA.Object nsRef = orb.resolve_initial_references("NameService");

		//narrow NS reference
		NamingContextExt ncRef = NamingContextExtHelper.narrow( nsRef );

		// bind the Object Reference in Naming
		//args.NameComponent path[] = ncRef.to_name(

		//ncRef.rebind(
		
		// wait for invocations from clients
		orb.run();

	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws WrongPolicy 
	 * @throws ServantNotActive 
	 * @throws AdapterInactive 
	 * @throws InvalidName 
	 * @throws org.omg.CosNaming.NamingContextPackage.InvalidName 
	 * @throws CannotProceed 
	 * @throws NotFound 
	 * @throws ObjectAlreadyActive 
	 * @throws ServantAlreadyActive 
	 */
	public static void main(String[] args) throws InvalidName, AdapterInactive, ServantNotActive, WrongPolicy, IOException, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound, CannotProceed, ServantAlreadyActive, ObjectAlreadyActive 
	{
		Server s = new Server();
		s.base1(args);
	}

}
