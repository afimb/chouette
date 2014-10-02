/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.plugin.exchange;

@SuppressWarnings("serial")
public class ExchangeException extends Exception
{

   public ExchangeException(String arg0, Throwable arg1)
   {
      super(arg0, arg1);
   }

   public ExchangeException(String arg0)
   {
      super(arg0);
   }

}
