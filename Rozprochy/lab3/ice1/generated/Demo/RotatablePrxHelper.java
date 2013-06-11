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
// Generated from file `RotatablePrxHelper.java'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Demo;

public final class RotatablePrxHelper extends Ice.ObjectPrxHelperBase implements RotatablePrx
{
    public void
    rotate(int degree)
    {
        rotate(degree, null, false);
    }

    public void
    rotate(int degree, java.util.Map<String, String> __ctx)
    {
        rotate(degree, __ctx, true);
    }

    private void
    rotate(int degree, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __delBase = __getDelegate(false);
                _RotatableDel __del = (_RotatableDel)__delBase;
                __del.rotate(degree, __ctx);
                return;
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

    private static final String __rotate_name = "rotate";

    public Ice.AsyncResult begin_rotate(int degree)
    {
        return begin_rotate(degree, null, false, null);
    }

    public Ice.AsyncResult begin_rotate(int degree, java.util.Map<String, String> __ctx)
    {
        return begin_rotate(degree, __ctx, true, null);
    }

    public Ice.AsyncResult begin_rotate(int degree, Ice.Callback __cb)
    {
        return begin_rotate(degree, null, false, __cb);
    }

    public Ice.AsyncResult begin_rotate(int degree, java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_rotate(degree, __ctx, true, __cb);
    }

    public Ice.AsyncResult begin_rotate(int degree, Callback_Rotatable_rotate __cb)
    {
        return begin_rotate(degree, null, false, __cb);
    }

    public Ice.AsyncResult begin_rotate(int degree, java.util.Map<String, String> __ctx, Callback_Rotatable_rotate __cb)
    {
        return begin_rotate(degree, __ctx, true, __cb);
    }

    private Ice.AsyncResult begin_rotate(int degree, java.util.Map<String, String> __ctx, boolean __explicitCtx, IceInternal.CallbackBase __cb)
    {
        IceInternal.OutgoingAsync __result = new IceInternal.OutgoingAsync(this, __rotate_name, __cb);
        try
        {
            __result.__prepare(__rotate_name, Ice.OperationMode.Normal, __ctx, __explicitCtx);
            IceInternal.BasicStream __os = __result.__os();
            __os.writeInt(degree);
            __os.endWriteEncaps();
            __result.__send(true);
        }
        catch(Ice.LocalException __ex)
        {
            __result.__exceptionAsync(__ex);
        }
        return __result;
    }

    public void end_rotate(Ice.AsyncResult __result)
    {
        __end(__result, __rotate_name);
    }

    public static RotatablePrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        RotatablePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (RotatablePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA(ice_staticId()))
                {
                    RotatablePrxHelper __h = new RotatablePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RotatablePrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        RotatablePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (RotatablePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA(ice_staticId(), __ctx))
                {
                    RotatablePrxHelper __h = new RotatablePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static RotatablePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        RotatablePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA(ice_staticId()))
                {
                    RotatablePrxHelper __h = new RotatablePrxHelper();
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

    public static RotatablePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        RotatablePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA(ice_staticId(), __ctx))
                {
                    RotatablePrxHelper __h = new RotatablePrxHelper();
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

    public static RotatablePrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        RotatablePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (RotatablePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                RotatablePrxHelper __h = new RotatablePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static RotatablePrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        RotatablePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            RotatablePrxHelper __h = new RotatablePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    public static final String[] __ids =
    {
        "::Demo::Rotatable",
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
        return new _RotatableDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _RotatableDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, RotatablePrx v)
    {
        __os.writeProxy(v);
    }

    public static RotatablePrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            RotatablePrxHelper result = new RotatablePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}