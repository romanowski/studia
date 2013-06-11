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

module Laboratory
{
	
	exception ApplicationException{
		string message;
	};
	
	exception  BadOperation{
	};



    sequence<string> strSeq;

	interface State{
		strSeq states();
		void setState(string newState);
	};


	struct DevS
	{
		string ID;
		string devType;
	};

	struct Operation{
	         string ID;
             strSeq paramsTypes;
	};


    sequence<Operation> OperationSeq;

	interface Dev{
	    DevS info();
        void doOperation(string name, strSeq params) throws BadOperation;
        OperationSeq operations();
	};

	
	sequence<DevS> DevSeq;
	
	interface DevManager
	{
	    idempotent DevSeq  getDevsInfo();
	
		idempotent DevS viewDev(string ID);
		
		string connect() throws ApplicationException;
		
		Dev* reserveDev(string ID, string accessToken) throws ApplicationException;
		
		void relaseDev(string ID, string accessToken) throws ApplicationException;
		
		void disconnect(string accessToken) throws ApplicationException;

		State *devState(string ID);
	};


};

#endif
