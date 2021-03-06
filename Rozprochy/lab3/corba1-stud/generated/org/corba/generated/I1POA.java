package org.corba.generated;


/**
* sr/I1POA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from sr.idl
* niedziela, 22 kwiecień 2012 15:20:10 CEST
*/

public abstract class I1POA extends org.omg.PortableServer.Servant
 implements org.corba.generated.I1Operations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("op1", new java.lang.Integer (0));
    _methods.put ("op2", new java.lang.Integer (1));
    _methods.put ("getI2", new java.lang.Integer (2));
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
       case 0:  // sr/I1/op1
       {
         int abc = in.read_long ();
         short $result = (short)0;
         $result = this.op1 (abc);
         out = $rh.createReply();
         out.write_short ($result);
         break;
       }

       case 1:  // sr/I1/op2
       {
         String text = in.read_string ();
         org.omg.CORBA.StringHolder text2 = new org.omg.CORBA.StringHolder ();
         text2.value = in.read_string ();
         org.omg.CORBA.StringHolder text3 = new org.omg.CORBA.StringHolder ();
         org.corba.generated.S1 struct1 = org.corba.generated.S1Helper.read (in);
         String $result = null;
         $result = this.op2 (text, text2, text3, struct1);
         out = $rh.createReply();
         out.write_string ($result);
         out.write_string (text2.value);
         out.write_string (text3.value);
         break;
       }

       case 2:  // sr/I1/getI2
       {
         org.corba.generated.I2 $result = null;
         $result = this.getI2 ();
         out = $rh.createReply();
         org.corba.generated.I2Helper.write (out, $result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:sr/I1:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public I1 _this() 
  {
    return I1Helper.narrow(
    super._this_object());
  }

  public I1 _this(org.omg.CORBA.ORB orb) 
  {
    return I1Helper.narrow(
    super._this_object(orb));
  }


} // class I1POA
