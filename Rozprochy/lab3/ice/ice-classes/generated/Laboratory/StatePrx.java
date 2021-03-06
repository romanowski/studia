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
// Generated from file `StatePrx.java'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Laboratory;

public interface StatePrx extends Ice.ObjectPrx
{
    public String[] states();

    public String[] states(java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_states();

    public Ice.AsyncResult begin_states(java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_states(Ice.Callback __cb);

    public Ice.AsyncResult begin_states(java.util.Map<String, String> __ctx, Ice.Callback __cb);

    public Ice.AsyncResult begin_states(Callback_State_states __cb);

    public Ice.AsyncResult begin_states(java.util.Map<String, String> __ctx, Callback_State_states __cb);

    public String[] end_states(Ice.AsyncResult __result);

    public void setState(String newState);

    public void setState(String newState, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_setState(String newState);

    public Ice.AsyncResult begin_setState(String newState, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_setState(String newState, Ice.Callback __cb);

    public Ice.AsyncResult begin_setState(String newState, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    public Ice.AsyncResult begin_setState(String newState, Callback_State_setState __cb);

    public Ice.AsyncResult begin_setState(String newState, java.util.Map<String, String> __ctx, Callback_State_setState __cb);

    public void end_setState(Ice.AsyncResult __result);
}
