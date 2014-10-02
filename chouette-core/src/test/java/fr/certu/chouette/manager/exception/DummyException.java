/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager.exception;

import fr.certu.chouette.common.ChouetteException;

/**
 * @author michel
 * 
 */
@SuppressWarnings("serial")
public class DummyException extends ChouetteException
{

   /**
	 * 
	 */
   public DummyException()
   {
      super();
   }

   /**
    * @param message
    * @param cause
    */
   public DummyException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * @param args
    */
   public DummyException(String... args)
   {
      super(args);
   }

   /**
    * @param message
    */
   public DummyException(String message)
   {
      super(message);
   }

   /**
    * @param cause
    * @param args
    */
   public DummyException(Throwable cause, String... args)
   {
      super(cause, args);
   }

   /**
    * @param cause
    */
   public DummyException(Throwable cause)
   {
      super(cause);
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.common.ChouetteException#getPrefix()
    */
   @Override
   public String getPrefix()
   {
      return "DUM";
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.common.ChouetteException#getCode()
    */
   @Override
   public String getCode()
   {
      return "DUMMY";
   }

}
