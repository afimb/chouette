package fr.certu.chouette.exchange;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class LoggingManager 
{
	
	private static final int         nombreMaxLogEquiv = 5;
	private static       Set<String> messages          = new HashSet<String>();
	
	public static void log(Logger logger, String message, Priority priority) {
		for (int i = 0; i < nombreMaxLogEquiv; i++)
			if (messages.add(message+i)) {
				if ((i+1) == nombreMaxLogEquiv)
					message = message + "...etc.";
				logger.log(priority, message);
				return;
			}
	}
	
	public static void log(Logger logger, String messageTemplate, Priority priority,Object... params ) 
	{
		if ((params == null) || (params.length == 0)) 
		{
			log(logger,messageTemplate,priority);
			return;
		}
		for (int i = 0; i < nombreMaxLogEquiv; i++)
		{
			if (messages.add(messageTemplate+i)) 
			{
				
				String message = MessageFormat.format(messageTemplate,params);
				if ((i+1) == nombreMaxLogEquiv)
					message = message + "...etc.";
				logger.log(priority, message);
				return;
			}
		}
	}
	
	public static int getNombreMaxLogEquiv() {
		return nombreMaxLogEquiv;
	}
	
	public static void setMessages(Set<String> messages) {
		LoggingManager.messages = messages;
	}
	
	public static Set<String> getMessages() {
		return messages;
	}
	
}
