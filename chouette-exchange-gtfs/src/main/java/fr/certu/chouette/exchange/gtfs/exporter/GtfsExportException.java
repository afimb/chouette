/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter;

import fr.certu.chouette.common.ChouetteException;

/**
 *
 */
@SuppressWarnings("serial")
public class GtfsExportException extends ChouetteException
{
   private static final String PREFIX = "COR";
   private GtfsExportExceptionCode code;

   /**
    * 
    */
   public GtfsExportException(GtfsExportExceptionCode code, String... args) 
   {
      super( args);
      this.code = code;
   }
   
   public GtfsExportException(GtfsExportExceptionCode code, Throwable cause, String... args) 
   {
      super( cause, args);
      this.code = code;
   }

   public GtfsExportExceptionCode getExceptionCode() 
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
