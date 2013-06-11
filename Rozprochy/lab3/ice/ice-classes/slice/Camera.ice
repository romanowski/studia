// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

#ifndef CAMERA_ICE
#define CAMERA_ICE
#include "Dev.ice"

module Recorder
{



	interface Camera extends Laboratory::Dev {

	    void rotate(int xy, int z);

	    void zoom(int zoomLevel);
	};


};

#endif
