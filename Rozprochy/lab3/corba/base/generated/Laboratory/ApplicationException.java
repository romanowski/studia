package Laboratory;


/**
* Laboratory/ApplicationException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Laboratory.idl
* wtorek, 24 kwiecień 2012 11:40:18 CEST
*/

public final class ApplicationException extends org.omg.CORBA.UserException
{
  public String message = null;

  public ApplicationException ()
  {
    super(ApplicationExceptionHelper.id());
  } // ctor

  public ApplicationException (String _message)
  {
    super(ApplicationExceptionHelper.id());
    message = _message;
  } // ctor


  public ApplicationException (String $reason, String _message)
  {
    super(ApplicationExceptionHelper.id() + "  " + $reason);
    message = _message;
  } // ctor

} // class ApplicationException
