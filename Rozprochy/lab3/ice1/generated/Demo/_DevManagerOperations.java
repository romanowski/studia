// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************
//
// Ice version 3.4.2
//
// <auto-generated>
//
// Generated from file `_DevManagerOperations.java'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Demo;

public interface _DevManagerOperations
{
    Dev[] getDevsInfo(Ice.Current __current);

    Dev viewDev(String ID, Ice.Current __current);

    String connect(Ice.Current __current)
        throws ApplicationException;

    DevPrx reserveDev(String ID, String accessToken, Ice.Current __current)
        throws ApplicationException;

    void relaseDev(String ID, String accessToken, Ice.Current __current)
        throws ApplicationException;

    void disconnect(String accessToken, Ice.Current __current)
        throws ApplicationException;
}
