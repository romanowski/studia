package Laboratory;

/**
* Laboratory/BadOperationHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Laboratory.idl
* wtorek, 24 kwiecień 2012 11:40:18 CEST
*/

public final class BadOperationHolder implements org.omg.CORBA.portable.Streamable
{
  public Laboratory.BadOperation value = null;

  public BadOperationHolder ()
  {
  }

  public BadOperationHolder (Laboratory.BadOperation initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = Laboratory.BadOperationHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    Laboratory.BadOperationHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return Laboratory.BadOperationHelper.type ();
  }

}
