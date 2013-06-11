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
// Generated from file `DeviceException.java'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Demo;

public class DeviceException extends Ice.UserException
{
    public DeviceException()
    {
    }

    public DeviceException(Throwable cause)
    {
        super(cause);
    }

    public DeviceException(String message)
    {
        this.message = message;
    }

    public DeviceException(String message, Throwable cause)
    {
        super(cause);
        this.message = message;
    }

    public String
    ice_name()
    {
        return "Demo::DeviceException";
    }

    public String message;

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeString("::Demo::DeviceException");
        __os.startWriteSlice();
        __os.writeString(message);
        __os.endWriteSlice();
    }

    public void
    __read(IceInternal.BasicStream __is, boolean __rid)
    {
        if(__rid)
        {
            __is.readString();
        }
        __is.startReadSlice();
        message = __is.readString();
        __is.endReadSlice();
    }

    public void
    __write(Ice.OutputStream __outS)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "exception Demo::DeviceException was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "exception Demo::DeviceException was not generated with stream support";
        throw ex;
    }
}