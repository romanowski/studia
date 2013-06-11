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


	exception  AccessDenied{
	};


    sequence<string> strSeq;

	interface Listener{

		void listen(string msg);

	};

	interface Labolatory{

		strSeq describe();

		string connect(string login, string pass) throws AccessDenied;

		void disconnect(string accessToken);
	};



	interface Device{
		void listen(Listener* list, string accessToken)  throws AccessDenied;
		void abandon(Listener* list, string accessToken) throws AccessDenied;

		bool use(string accessToken) throws AccessDenied;
		void free(string accessToken) throws AccessDenied;

	};

	interface Monitor extends Device{
		void move(int x, int y, string accessToken) throws AccessDenied;
	};


	interface Camera extends Device{
		void move(int x, int y, string accessToken) throws AccessDenied;
		void zoom(int level, string accessToken) throws AccessDenied;
	};
};

#endif
