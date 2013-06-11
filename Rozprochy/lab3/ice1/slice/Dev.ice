// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

#ifndef DEV_ICE
#define DEV_ICE

module Demo
{
	
	exception ApplicationException{
		string message;
	};
	
	exception DeviceException{
		string message;	
	};

	interface State{
		string asString();
	};

	interface Dev
	{
	    idempotent State getState();
		idempotent string ID();
		idempotent string type();
	};
	
	interface Rotatable{
		void rotate(int degree);
	};
	
	interface Movable{
		void move(int x, int y);	
	};
	
	interface Zoomable{
		void zoom(int zoomLevel);
	};
	
	interface MobileCamera extends Dev, Movable, Zoomable, Rotatable{
	};
	
	sequence<Dev> DevSeq;
	
	interface DevManager
	{
	    idempotent DevSeq  getDevsInfo();
	
		idempotent Dev viewDev(string ID);
		
		string connect() throws ApplicationException;
		
		Dev* reserveDev(string ID, string accessToken) throws ApplicationException;
		
		void relaseDev(string ID, string accessToken) throws ApplicationException;
		
		void disconnect(string accessToken) throws ApplicationException;

		
	};


};

#endif
