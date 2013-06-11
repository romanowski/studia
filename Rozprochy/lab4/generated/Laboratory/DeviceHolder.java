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
// Generated from file `DeviceHolder.java'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Laboratory;

public final class DeviceHolder extends Ice.ObjectHolderBase<Device>
{
    public
    DeviceHolder()
    {
    }

    public
    DeviceHolder(Device value)
    {
        this.value = value;
    }

    public void
    patch(Ice.Object v)
    {
        try
        {
            value = (Device)v;
        }
        catch(ClassCastException ex)
        {
            IceInternal.Ex.throwUOE(type(), v.ice_id());
        }
    }

    public String
    type()
    {
        return _DeviceDisp.ice_staticId();
    }
}