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
// Generated from file `_CameraOperations.java'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Laboratory;

public interface _CameraOperations extends _DeviceOperations
{
    void move(int x, int y, String accessToken, Ice.Current __current)
        throws AccessDenied;

    void zoom(int level, String accessToken, Ice.Current __current)
        throws AccessDenied;
}