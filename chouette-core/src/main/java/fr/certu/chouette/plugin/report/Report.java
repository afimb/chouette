/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.plugin.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;

/**
 * @author michel
 *
 */

public abstract class Report 
{
	public enum STATE {UNCHECK, OK, WARNING, ERROR, FATAL} ;
	@Getter @Setter private String originKey;
	@Getter @Setter private STATE status;
	@Getter @Setter private List<ReportItem> items;

	public void addItem(ReportItem item)
	{
		if (items == null) items= new ArrayList<ReportItem>();
                String messageKey = item.getMessageKey();
                for (ReportItem it : items) {
                    if (it.getMessageKey() != null && it.getMessageKey().equals(messageKey)) {
                        if (item.getItems() != null) {
                            for (ReportItem sub : item.getItems()) {
                                it.addItem(sub);
                            }
                        }
                        return;
                    }
                }
		items.add(item);
	}	
	
	public void addAll(List<ReportItem> itemsToAdd) 
	{
		if (itemsToAdd == null) return;
		if (items == null) items= new ArrayList<ReportItem>();
		//items.addAll(itemsToAdd);
                for (ReportItem it : itemsToAdd)
                    addItem(it);
	} 



	public void updateStatus(STATE statusToApply) 
	{
		if (status.ordinal() < statusToApply.ordinal())
		{
			status = statusToApply;
		}
		
	}
	
	public final String getLocalizedMessage() 
	{
		return getLocalizedMessage(Locale.getDefault());
	}
	/**
	 * @param locale
	 * @return
	 */
	public String getLocalizedMessage(Locale locale)
	{
		String message = "";
		try
		{
			ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName(),locale);
			message = bundle.getString(getOriginKey());
		}
		catch (MissingResourceException e1)
		{
			try
			{
				ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName());
				message = bundle.getString(getOriginKey());
			}
			catch (MissingResourceException e2)
			{
				message = getOriginKey();
			}
		}

		return message;
	}

}
