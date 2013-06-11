// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import Displays.MonitorHelper;
import Laboratory.*;
import Recorder.CameraHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class Client {

    String accessToken;
    DevManager mgr = null;
    ORB orb;

    class ShutdownHook extends Thread {
        public void
        run() {
            try {

                if (mgr != null)
                    mgr.disconnect(accessToken);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public int run(String[] args) {


        //
        // Since this is an interactive demo we want to clear the
        // Application installed interrupt callback and install our
        // own shutdown hook.
        //
        ///setInterruptHook(new ShutdownHook());


        try {
            orb = ORB.init(args, null);

            org.omg.CORBA.Object nsRef;

            try {
                BufferedReader reader = new BufferedReader(new FileReader("corba.client"));
                nsRef = orb.string_to_object(reader.readLine());
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
                nsRef = orb.resolve_initial_references("NameService");
            }


            NamingContextExt namingService = NamingContextExtHelper.narrow(nsRef);

            org.omg.CORBA.Object obj = namingService.resolve_str("DevManager");

            mgr = DevManagerHelper.narrow(obj);

            accessToken = mgr.connect();

            mainLoop();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        new Client().run(args);
    }

    void mainLoop() {
        Scanner s = new Scanner(System.in);

        String line;

        Map<String, Dev> reserved = new HashMap<String, Dev>();


        System.out.print("#> ");
        while (!(line = s.nextLine()).equals("\\q")) {

            System.out.print('\n');


            try {
                String[] args = line.split(" ");

                if (args[0].equals("list")) {
                    System.out.println("List all devices");
                    for (DevS d : mgr.getDevsInfo()) {
                        System.out.println("id: " + d.ID + " type " + d.devType);
                    }
                }
                if (args[0].equals("state")) {
                    System.out.println("Device " + args[1] + " States: ");

                    State state = mgr.devState(args[1]);

                    for (String st : state.states()) {
                        System.out.println(st);
                    }
                }

                if (args[0].equals("zoom")) {
                    CameraHelper.narrow(reserved.get(args[1]))
                            .zoom(new Integer(args[2]));

                    System.out.println("zooming camera on: " + args[1]);
                }


                if (args[0].equals("rotateCamera")) {
                    CameraHelper.narrow(reserved.get(args[1]))
                            .rotate(new Integer(args[2]), new Integer(args[3]));

                    System.out.println("rotate camera on: " + args[2] + " and" + args[3]);
                }

                if (args[0].equals("rotateMonitor")) {
                    MonitorHelper.narrow(reserved.get(args[1])).rotate(new Integer(args[2]));

                    System.out.println("rotate monitor on: " + args[2]);
                }


                if (args[0].equals("operations")) {
                    for (Operation o : reserved.get(args[1]).operations()) {
                        System.out.print(o.ID + " params: ");
                        for (String t : o.paramsTypes) {
                            System.out.print(t + " ");
                        }
                        System.out.println("");
                    }
                }

                if (args[0].equals("do")) {
                    reserved.get(args[1]).doOperation(args[2], Arrays.copyOfRange(args, 3, args.length));
                    System.out.println("operation " + args[2] + " done!");
                }

                if (args[0].equals("reserve")) {
                    reserved.put(args[1], mgr.reserveDev(args[1], accessToken));
                    System.out.println("Dev " + args[1] + " reserved");
                }

                if (args[0].equals("release")) {
                    mgr.relaseDev(args[1], accessToken);
                    reserved.remove(args[1]);
                    System.out.println("Dev " + args[1] + " realase");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.print("#> ");
        }

    }
}

