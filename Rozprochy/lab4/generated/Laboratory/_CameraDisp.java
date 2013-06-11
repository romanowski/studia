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
// Generated from file `_CameraDisp.java'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Laboratory;

public abstract class _CameraDisp extends Ice.ObjectImpl implements Camera
{
    protected void
    ice_copyStateFrom(Ice.Object __obj)
        throws java.lang.CloneNotSupportedException
    {
        throw new java.lang.CloneNotSupportedException();
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::Laboratory::Camera",
        "::Laboratory::Device"
    };

    public boolean
    ice_isA(String s)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean
    ice_isA(String s, Ice.Current __current)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[]
    ice_ids()
    {
        return __ids;
    }

    public String[]
    ice_ids(Ice.Current __current)
    {
        return __ids;
    }

    public String
    ice_id()
    {
        return __ids[1];
    }

    public String
    ice_id(Ice.Current __current)
    {
        return __ids[1];
    }

    public static String
    ice_staticId()
    {
        return __ids[1];
    }

    public final void
    move(int x, int y, String accessToken)
        throws AccessDenied
    {
        move(x, y, accessToken, null);
    }

    public final void
    zoom(int level, String accessToken)
        throws AccessDenied
    {
        zoom(level, accessToken, null);
    }

    public final void
    abandon(ListenerPrx list, String accessToken)
        throws AccessDenied
    {
        abandon(list, accessToken, null);
    }

    public final void
    free(String accessToken)
        throws AccessDenied
    {
        free(accessToken, null);
    }

    public final void
    listen(ListenerPrx list, String accessToken)
        throws AccessDenied
    {
        listen(list, accessToken, null);
    }

    public final boolean
    use(String accessToken)
        throws AccessDenied
    {
        return use(accessToken, null);
    }

    public static Ice.DispatchStatus
    ___move(Camera __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int x;
        x = __is.readInt();
        int y;
        y = __is.readInt();
        String accessToken;
        accessToken = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        try
        {
            __obj.move(x, y, accessToken, __current);
            return Ice.DispatchStatus.DispatchOK;
        }
        catch(AccessDenied ex)
        {
            __os.writeUserException(ex);
            return Ice.DispatchStatus.DispatchUserException;
        }
    }

    public static Ice.DispatchStatus
    ___zoom(Camera __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int level;
        level = __is.readInt();
        String accessToken;
        accessToken = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        try
        {
            __obj.zoom(level, accessToken, __current);
            return Ice.DispatchStatus.DispatchOK;
        }
        catch(AccessDenied ex)
        {
            __os.writeUserException(ex);
            return Ice.DispatchStatus.DispatchUserException;
        }
    }

    private final static String[] __all =
    {
        "abandon",
        "free",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "listen",
        "move",
        "use",
        "zoom"
    };

    public Ice.DispatchStatus
    __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
        }

        switch(pos)
        {
            case 0:
            {
                return _DeviceDisp.___abandon(this, in, __current);
            }
            case 1:
            {
                return _DeviceDisp.___free(this, in, __current);
            }
            case 2:
            {
                return ___ice_id(this, in, __current);
            }
            case 3:
            {
                return ___ice_ids(this, in, __current);
            }
            case 4:
            {
                return ___ice_isA(this, in, __current);
            }
            case 5:
            {
                return ___ice_ping(this, in, __current);
            }
            case 6:
            {
                return _DeviceDisp.___listen(this, in, __current);
            }
            case 7:
            {
                return ___move(this, in, __current);
            }
            case 8:
            {
                return _DeviceDisp.___use(this, in, __current);
            }
            case 9:
            {
                return ___zoom(this, in, __current);
            }
        }

        assert(false);
        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeTypeId(ice_staticId());
        __os.startWriteSlice();
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void
    __read(IceInternal.BasicStream __is, boolean __rid)
    {
        if(__rid)
        {
            __is.readTypeId();
        }
        __is.startReadSlice();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void
    __write(Ice.OutputStream __outS)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type Laboratory::Camera was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type Laboratory::Camera was not generated with stream support";
        throw ex;
    }
}