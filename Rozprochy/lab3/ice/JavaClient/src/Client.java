// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

import Displays.MonitorPrxHelper;
import Laboratory.*;
import Recorder.CameraPrx;
import Recorder.CameraPrxHelper;

import java.util.*;

public class Client extends Ice.Application {

    String accessToken;
    DevManagerPrx prx = null;

    class ShutdownHook extends Thread {
        public void
        run() {
            try {

                if (prx != null)
                    prx.disconnect(accessToken);

                communicator().destroy();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public int run(String[] args) {


        if (args.length > 0) {
            System.err.println(appName() + ": too many arguments");
            return 1;
        }

        //
        // Since this is an interactive demo we want to clear the
        // Application installed interrupt callback and install our
        // own shutdown hook.
        //
        setInterruptHook(new ShutdownHook());


        try {
            Ice.ObjectPrx prxx = communicator().propertyToProxy("LapServer.Proxy");


            prx = DevManagerPrxHelper.checkedCast(prxx.ice_twoway());
            if (prx == null) {
                System.err.println("invalid proxy");
                return 1;
            }
            accessToken = prx.connect();
            
            System.out.println("Connected!");

            mainLoop();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        Client app = new Client();
        int status = app.main("Client", args, "config.client");
        System.exit(status);
    }

    void mainLoop() {
        Scanner s = new Scanner(System.in);

        String line;

        Map<String, DevPrx> reserved = new HashMap<String, DevPrx>();


        System.out.print("#> ");
        while (!(line = s.nextLine()).equals("\\q")) {

            System.out.print('\n');


            try {
                String[] args = line.split(" ");

                if (args[0].equals("list")) {
                    System.out.println("List all devices");
                    for (DevS d : prx.getDevsInfo()) {
                        System.out.println("id: " + d.ID + " type " + d.devType);
                    }
                }
                if (args[0].equals("state")) {
                    System.out.println("Device " + args[1] + " States: ");
                    for (String state : prx.devState(args[1]).states()) {
                        System.out.println(state);
                    }
                }

                if (args[0].equals("zoom")) {
                    CameraPrxHelper.checkedCast(reserved.get(args[1])).zoom(new Integer(args[2]));

                    System.out.println("zooming camera on: " + args[1]);
                }


                if (args[0].equals("rotateCamera")) {
                    CameraPrxHelper.checkedCast(reserved.get(args[1])).rotate(new Integer(args[2]), new Integer(args[3]));

                    System.out.println("rotate camera on: " + args[2] + " and" + args[3]);
                }

                if (args[0].equals("rotateMonitor")) {
                    MonitorPrxHelper.checkedCast(reserved.get(args[1])).rotate(new Integer(args[2]));

                    System.out.println("rotate monitor on: " + args[2]);
                }

                if (args[0].equals("reserve")) {
                    reserved.put(args[1], prx.reserveDev(args[1], accessToken));
                    System.out.println("Dev " + args[1] + " reserved");
                }

                if (args[0].equals("release")) {
                    prx.relaseDev(args[1], accessToken);
                    reserved.remove(args[1]);
                    System.out.println("Dev " + args[1] + " realase");
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

                System.out.print("#> ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

