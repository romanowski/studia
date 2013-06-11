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
// Generated from file `Callback_Device_use.java'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Laboratory;

public abstract class Callback_Device_use extends Ice.TwowayCallback
{
    public abstract void response(boolean __ret);
    public abstract void exception(Ice.UserException __ex);

    public final void __completed(Ice.AsyncResult __result)
    {
        DevicePrx __proxy = (DevicePrx)__result.getProxy();
        boolean __ret = false;
        try
        {
            __ret = __proxy.end_use(__result);
        }
        catch(Ice.UserException __ex)
        {
            exception(__ex);
            return;
        }
        catch(Ice.LocalException __ex)
        {
            exception(__ex);
            return;
        }
        response(__ret);
    }
}
