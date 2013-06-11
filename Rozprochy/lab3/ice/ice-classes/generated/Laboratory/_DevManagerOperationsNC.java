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
// Generated from file `_DevManagerOperationsNC.java'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Laboratory;

public interface _DevManagerOperationsNC
{
    DevS[] getDevsInfo();

    DevS viewDev(String ID);

    String connect()
        throws ApplicationException;

    DevPrx reserveDev(String ID, String accessToken)
        throws ApplicationException;

    void relaseDev(String ID, String accessToken)
        throws ApplicationException;

    void disconnect(String accessToken)
        throws ApplicationException;

    StatePrx devState(String ID);
}
