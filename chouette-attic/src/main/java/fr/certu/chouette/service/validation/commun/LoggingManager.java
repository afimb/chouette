package fr.certu.chouette.service.validation.commun;

import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class LoggingManager {
    
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

    public static void log(Logger logger, String message, String[] params, Priority priority) {
        if ((params == null) || (params.length == 0)) {
            while (message.indexOf(" ()") >= 0)
                message = replaceFirst(message, " ()", "");
            log(logger, message, priority);
            return;
        }
        for (int i = 0; i < nombreMaxLogEquiv; i++)
            if (messages.add(message+i)) {
                for (int j = 0; j < params.length; j++)
                    if (message.indexOf("()") >= 0)
                        if ((params[j] != null) && (params[j].trim().length() > 0))
                            message = replaceFirst(message, "()", "("+params[j]+")");
                        else
                            message = replaceFirst(message, " ()", "");
                    else
                        break;// ERROR
                if ((i+1) == nombreMaxLogEquiv)
                    message = message + "...etc.";
                logger.log(priority, message);
                return;
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

    public static String[] getParams(String str) {
        if ((str == null) || (str.trim().length() == 0))
            return null;
        String[] params = new String[1];
        params[0] = str.trim();
        return params;
    }
    
    public static String[] getParams(String str1, String str2) {
        if ((str1 == null) || (str1.trim().length() == 0))
            str1 = "";
        if ((str2 == null) || (str2.trim().length() == 0))
            str2 = "";
        if (str1.equals("") && str2.equals(""))
            return null;
        String[] params = new String[2];
        params[0] = str1.trim();
        params[1] = str2.trim();
        return params;
    }
    
    public static String[] getParams(String str1, String str2, String str3) {
        if ((str1 == null) || (str1.trim().length() == 0))
            str1 = "";
        if ((str2 == null) || (str2.trim().length() == 0))
            str2 = "";
        if ((str3 == null) || (str3.trim().length() == 0))
            str3 = "";
        if (str1.equals("") && str2.equals("") && str3.equals(""))
            return null;
        String[] params = new String[3];
        params[0] = str1.trim();
        params[1] = str2.trim();
        params[2] = str3.trim();
        return params;
    }

    public static String[] getParams(String str1, String str2, String str3, String str4) {
        if ((str1 == null) || (str1.trim().length() == 0))
            str1 = "";
        if ((str2 == null) || (str2.trim().length() == 0))
            str2 = "";
        if ((str3 == null) || (str3.trim().length() == 0))
            str3 = "";
        if ((str4 == null) || (str4.trim().length() == 0))
            str4 = "";
        if (str1.equals("") && str2.equals("") && str3.equals("")&& str4.equals(""))
            return null;
        String[] params = new String[3];
        params[0] = str1.trim();
        params[1] = str2.trim();
        params[2] = str3.trim();
        params[3] = str4.trim();
        return params;
    }

    private static String replaceFirst(String str, String sub1, String sub2) {
        return str.substring(0, str.indexOf(sub1)) + sub2 + str.substring(str.indexOf(sub1)+sub1.length());
    }
}
