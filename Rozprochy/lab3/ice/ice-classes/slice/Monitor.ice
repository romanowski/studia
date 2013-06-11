// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

#ifndef Displays_ICE
#define Displays_ICE
#include "Dev.ice"

module Displays
{

	interface Monitor extends Laboratory::Dev{

	    void rotate(int degree);

    };


};

#endif
