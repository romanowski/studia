package org.corba.generated;

/**
* sr/I2ExtHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from sr.idl
* niedziela, 22 kwiecień 2012 15:20:10 CEST
*/

public final class I2ExtHolder implements org.omg.CORBA.portable.Streamable
{
  public org.corba.generated.I2Ext value = null;

  public I2ExtHolder ()
  {
  }

  public I2ExtHolder (org.corba.generated.I2Ext initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.corba.generated.I2ExtHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.corba.generated.I2ExtHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.corba.generated.I2ExtHelper.type ();
  }

}
