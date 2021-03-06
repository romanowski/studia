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
// Generated from file `LabolatoryPrx.java'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Laboratory;

public interface LabolatoryPrx extends Ice.ObjectPrx
{
    public String[] describe();

    public String[] describe(java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_describe();

    public Ice.AsyncResult begin_describe(java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_describe(Ice.Callback __cb);

    public Ice.AsyncResult begin_describe(java.util.Map<String, String> __ctx, Ice.Callback __cb);

    public Ice.AsyncResult begin_describe(Callback_Labolatory_describe __cb);

    public Ice.AsyncResult begin_describe(java.util.Map<String, String> __ctx, Callback_Labolatory_describe __cb);

    public String[] end_describe(Ice.AsyncResult __result);

    public String connect(String login, String pass)
        throws AccessDenied;

    public String connect(String login, String pass, java.util.Map<String, String> __ctx)
        throws AccessDenied;

    public Ice.AsyncResult begin_connect(String login, String pass);

    public Ice.AsyncResult begin_connect(String login, String pass, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_connect(String login, String pass, Ice.Callback __cb);

    public Ice.AsyncResult begin_connect(String login, String pass, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    public Ice.AsyncResult begin_connect(String login, String pass, Callback_Labolatory_connect __cb);

    public Ice.AsyncResult begin_connect(String login, String pass, java.util.Map<String, String> __ctx, Callback_Labolatory_connect __cb);

    public String end_connect(Ice.AsyncResult __result)
        throws AccessDenied;

    public void disconnect(String accessToken);

    public void disconnect(String accessToken, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_disconnect(String accessToken);

    public Ice.AsyncResult begin_disconnect(String accessToken, java.util.Map<String, String> __ctx);

    public Ice.AsyncResult begin_disconnect(String accessToken, Ice.Callback __cb);

    public Ice.AsyncResult begin_disconnect(String accessToken, java.util.Map<String, String> __ctx, Ice.Callback __cb);

    public Ice.AsyncResult begin_disconnect(String accessToken, Callback_Labolatory_disconnect __cb);

    public Ice.AsyncResult begin_disconnect(String accessToken, java.util.Map<String, String> __ctx, Callback_Labolatory_disconnect __cb);

    public void end_disconnect(Ice.AsyncResult __result);
}
