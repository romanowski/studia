package Recorder;

/**
* Recorder/CameraHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Camera.idl
* wtorek, 24 kwiecień 2012 11:40:26 CEST
*/

public final class CameraHolder implements org.omg.CORBA.portable.Streamable
{
  public Recorder.Camera value = null;

  public CameraHolder ()
  {
  }

  public CameraHolder (Recorder.Camera initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = Recorder.CameraHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    Recorder.CameraHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return Recorder.CameraHelper.type ();
  }

}