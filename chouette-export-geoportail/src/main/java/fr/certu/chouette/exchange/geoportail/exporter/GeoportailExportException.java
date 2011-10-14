/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.geoportail.exporter;

import fr.certu.chouette.common.ChouetteException;

/**
 *
 */
@SuppressWarnings("serial")
public class GeoportailExportException extends ChouetteException
{
   private static final String PREFIX = "COR";
   private GeoportailExportExceptionCode code;

   /**
    * 
    */
   public GeoportailExportException(GeoportailExportExceptionCode code, String... args) 
   {
      super( args);
      this.code = code;
   }
   
   public GeoportailExportException(GeoportailExportExceptionCode code, Throwable cause, String... args) 
   {
      super( cause, args);
      this.code = code;
   }

   public GeoportailExportExceptionCode getExceptionCode() 
   {
      return code;
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.common.ChouetteException#getPrefix()
    */
   @Override
   public String getPrefix()
   {
      // TODO Auto-generated method stub
      return PREFIX;
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.common.ChouetteException#getCode()
    */
   @Override
   public String getCode()
   {
      // TODO Auto-generated method stub
      return code.name();
   }

}
