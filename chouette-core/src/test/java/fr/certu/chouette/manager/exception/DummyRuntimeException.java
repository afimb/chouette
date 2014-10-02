package fr.certu.chouette.manager.exception;

import fr.certu.chouette.common.ChouetteRuntimeException;

@SuppressWarnings("serial")
public class DummyRuntimeException extends ChouetteRuntimeException
{

   @Override
   public String getPrefix()
   {
      return "DUM";
   }

   @Override
   public String getCode()
   {
      return "DUMMY_RUNTIME";
   }

   /**
	 * 
	 */
   public DummyRuntimeException()
   {
      super();
   }

   /**
    * @param message
    * @param cause
    */
   public DummyRuntimeException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * @param args
    */
   public DummyRuntimeException(String... args)
   {
      super(args);
   }

   /**
    * @param message
    */
   public DummyRuntimeException(String message)
   {
      super(message);
   }

   /**
    * @param cause
    * @param args
    */
   public DummyRuntimeException(Throwable cause, String... args)
   {
      super(cause, args);
   }

   /**
    * @param cause
    */
   public DummyRuntimeException(Throwable cause)
   {
      super(cause);
   }

}
