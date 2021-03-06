package Recorder;


/**
* Recorder/CameraPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Camera.idl
* wtorek, 24 kwiecień 2012 11:40:26 CEST
*/

public abstract class CameraPOA extends org.omg.PortableServer.Servant
 implements Recorder.CameraOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("rotate", new java.lang.Integer (0));
    _methods.put ("zoom", new java.lang.Integer (1));
    _methods.put ("info", new java.lang.Integer (2));
    _methods.put ("doOperation", new java.lang.Integer (3));
    _methods.put ("operations", new java.lang.Integer (4));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // Recorder/Camera/rotate
       {
         int xy = in.read_long ();
         int z = in.read_long ();
         this.rotate (xy, z);
         out = $rh.createReply();
         break;
       }

       case 1:  // Recorder/Camera/zoom
       {
         int zoomLevel = in.read_long ();
         this.zoom (zoomLevel);
         out = $rh.createReply();
         break;
       }

       case 2:  // Laboratory/Dev/info
       {
         Laboratory.DevS $result = null;
         $result = this.info ();
         out = $rh.createReply();
         Laboratory.DevSHelper.write (out, $result);
         break;
       }

       case 3:  // Laboratory/Dev/doOperation
       {
         try {
           String name = in.read_string ();
           String params[] = Laboratory.StrSeqHelper.read (in);
           this.doOperation (name, params);
           out = $rh.createReply();
         } catch (Laboratory.BadOperation $ex) {
           out = $rh.createExceptionReply ();
           Laboratory.BadOperationHelper.write (out, $ex);
         }
         break;
       }

       case 4:  // Laboratory/Dev/operations
       {
         Laboratory.Operation $result[] = null;
         $result = this.operations ();
         out = $rh.createReply();
         Laboratory.OperationSeqHelper.write (out, $result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:Recorder/Camera:1.0", 
    "IDL:Laboratory/Dev:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public Camera _this() 
  {
    return CameraHelper.narrow(
    super._this_object());
  }

  public Camera _this(org.omg.CORBA.ORB orb) 
  {
    return CameraHelper.narrow(
    super._this_object(orb));
  }


} // class CameraPOA
