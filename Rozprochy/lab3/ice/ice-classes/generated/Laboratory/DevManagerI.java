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
// Generated from file `DevManagerI.java'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Laboratory;

public final class DevManagerI extends _DevManagerDisp
{
    public
    DevManagerI()
    {
    }

    public String
    connect(Ice.Current __current)
        throws ApplicationException
    {
        return null;
    }

    public StatePrx
    devState(String ID, Ice.Current __current)
    {
        return null;
    }

    public void
    disconnect(String accessToken, Ice.Current __current)
        throws ApplicationException
    {
    }

    public DevS[]
    getDevsInfo(Ice.Current __current)
    {
        return null;
    }

    public void
    relaseDev(String ID, String accessToken, Ice.Current __current)
        throws ApplicationException
    {
    }

    public DevPrx
    reserveDev(String ID, String accessToken, Ice.Current __current)
        throws ApplicationException
    {
        return null;
    }

    public DevS
    viewDev(String ID, Ice.Current __current)
    {
        return null;
    }
}
