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
// Generated from file `StatePrxHelper.java'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Demo;

public final class StatePrxHelper extends Ice.ObjectPrxHelperBase implements StatePrx
{
    public String
    asString()
    {
        return asString(null, false);
    }

    public String
    asString(java.util.Map<String, String> __ctx)
    {
        return asString(__ctx, true);
    }

    private String
    asString(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("asString");
                __delBase = __getDelegate(false);
                _StateDel __del = (_StateDel)__delBase;
                return __del.asString(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    private static final String __asString_name = "asString";

    public Ice.AsyncResult begin_asString()
    {
        return begin_asString(null, false, null);
    }

    public Ice.AsyncResult begin_asString(java.util.Map<String, String> __ctx)
    {
        return begin_asString(__ctx, true, null);
    }

    public Ice.AsyncResult begin_asString(Ice.Callback __cb)
    {
        return begin_asString(null, false, __cb);
    }

    public Ice.AsyncResult begin_asString(java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_asString(__ctx, true, __cb);
    }

    public Ice.AsyncResult begin_asString(Callback_State_asString __cb)
    {
        return begin_asString(null, false, __cb);
    }

    public Ice.AsyncResult begin_asString(java.util.Map<String, String> __ctx, Callback_State_asString __cb)
    {
        return begin_asString(__ctx, true, __cb);
    }

    private Ice.AsyncResult begin_asString(java.util.Map<String, String> __ctx, boolean __explicitCtx, IceInternal.CallbackBase __cb)
    {
        __checkAsyncTwowayOnly(__asString_name);
        IceInternal.OutgoingAsync __result = new IceInternal.OutgoingAsync(this, __asString_name, __cb);
        try
        {
            __result.__prepare(__asString_name, Ice.OperationMode.Normal, __ctx, __explicitCtx);
            IceInternal.BasicStream __os = __result.__os();
            __os.endWriteEncaps();
            __result.__send(true);
        }
        catch(Ice.LocalException __ex)
        {
            __result.__exceptionAsync(__ex);
        }
        return __result;
    }

    public String end_asString(Ice.AsyncResult __result)
    {
        Ice.AsyncResult.__check(__result, this, __asString_name);
        if(!__result.__wait())
        {
            try
            {
                __result.__throwUserException();
            }
            catch(Ice.UserException __ex)
            {
                throw new Ice.UnknownUserException(__ex.ice_name(), __ex);
            }
        }
        String __ret;
        IceInternal.BasicStream __is = __result.__is();
        __is.startReadEncaps();
        __ret = __is.readString();
        __is.endReadEncaps();
        return __ret;
    }

    public static StatePrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        StatePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (StatePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA(ice_staticId()))
                {
                    StatePrxHelper __h = new StatePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static StatePrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        StatePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (StatePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA(ice_staticId(), __ctx))
                {
                    StatePrxHelper __h = new StatePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static StatePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        StatePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA(ice_staticId()))
                {
                    StatePrxHelper __h = new StatePrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static StatePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        StatePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA(ice_staticId(), __ctx))
                {
                    StatePrxHelper __h = new StatePrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static StatePrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        StatePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (StatePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                StatePrxHelper __h = new StatePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static StatePrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        StatePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            StatePrxHelper __h = new StatePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    public static final String[] __ids =
    {
        "::Demo::State",
        "::Ice::Object"
    };

    public static String
    ice_staticId()
    {
        return __ids[0];
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _StateDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _StateDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, StatePrx v)
    {
        __os.writeProxy(v);
    }

    public static StatePrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            StatePrxHelper result = new StatePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
