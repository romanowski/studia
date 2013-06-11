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
// Generated from file `RotatablePrx.java'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Demo;

public interface RotatablePrx extends Ice.ObjectPrx
{
    public void rotate(int degree);

    public void rotate(int degree, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_rotate(int degree);

    public Ice.AsyncResult begin_rotate(int degree, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_rotate(int degree, Ice.Callback __cb);

    public Ice.AsyncResult begin_rotate(int degree, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    public Ice.AsyncResult begin_rotate(int degree, Callback_Rotatable_rotate __cb);

    public Ice.AsyncResult begin_rotate(int degree, java.util.Map<String, String> __ctx, Callback_Rotatable_rotate __cb);

    public void end_rotate(Ice.AsyncResult __result);
}