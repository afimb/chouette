/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.exchange.report;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;

import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author michel
 *
 */
public class ExchangeReport extends Report 
{
	// declare message report
    public enum KEY {IMPORT,EXPORT};
    
    @Getter @Setter private String format;
    
    /**
     * build a exchange report
     * @param key exchange mode (IMPORT/EXPORT)
     * @param format exchange format (Neptune, GTFS, NeTEx, ...)
     * 
     */
    public ExchangeReport(KEY key,String format)
    {
    	setOriginKey(key.name());
    	setFormat(format);
    }
    
    /**
     * add but don't merge item in list
     * 
     * @param item
     *           to add/merge
     */
    public void addItem(ReportItem item)
    {
       if (getItems() == null)
          setItems(new ArrayList<ReportItem>());
       updateStatus(item.getStatus());
       getItems().add(item);
    }

    /**
     * get report message for a specified Locale
     * <p>
     * if no message available for locale, default locale is assumed
     * 
     * @param locale
     *           asked locale
     * @return report message
     */
    @Override
    public String getLocalizedMessage(Locale locale)
    {
       String messageFormat = "";
       try
       {
          ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName(), locale);
          messageFormat = bundle.getString(getOriginKey());
       }
       catch (MissingResourceException e1)
       {
          try
          {
             ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName());
             messageFormat = bundle.getString(getOriginKey());
          }
          catch (MissingResourceException e2)
          {
        	  messageFormat = getOriginKey() + " : {0}";
          }
       }
       return MessageFormat.format(messageFormat, format);
    }


}
