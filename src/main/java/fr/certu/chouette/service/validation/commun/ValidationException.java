package fr.certu.chouette.service.validation.commun;

import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public class ValidationException extends ServiceException {
    
    private static final int                              nombreMaxLogEquiv          = 5;
    private static       Set<String>                      typesInvalidite            = new HashSet<String>();
    private              Map<TypeInvalidite, Set<String>> tridentIdParTypeInvalidite;

    public ValidationException(/*String message*/) {
        super(CodeIncident.DONNEE_INVALIDE, CodeDetailIncident.DEFAULT);
        tridentIdParTypeInvalidite = new Hashtable<TypeInvalidite, Set<String>>();
    }

    public ValidationException(/*String message,*/ Throwable exception) {
        super(CodeIncident.DONNEE_INVALIDE, CodeDetailIncident.DEFAULT, exception);
        tridentIdParTypeInvalidite = new Hashtable<TypeInvalidite, Set<String>>();
    }

    public void add(TypeInvalidite typeInvalidite, String tridentId) {
        for (int j = 0; j < nombreMaxLogEquiv; j++)
            if (typesInvalidite.add(typeInvalidite.toString()+j)) {
                Set<String> tridentIds = tridentIdParTypeInvalidite.get(typeInvalidite);
                if (tridentIds == null)
                    tridentIds = new HashSet<String>();
                if (tridentId != null) {
                    tridentIds.add(tridentId);
                    tridentIdParTypeInvalidite.put(typeInvalidite, tridentIds);
                }
                return;
            }
    }
    
    public List<TypeInvalidite> getCategories() {
        return new ArrayList<TypeInvalidite>(tridentIdParTypeInvalidite.keySet());
    }

    public Set<String> getTridentIds(TypeInvalidite typeInvalidite) {
        return tridentIdParTypeInvalidite.get(typeInvalidite);
    }
    
    public void add(TypeInvalidite typeInvalidite, String message, String[] params) {
        if ((params == null) || (params.length == 0))
            while (message.indexOf(" ()") >= 0)
                message = replaceFirst(message, " ()", "");
        else
            for (int i = 0; i < params.length; i++)
                if (message.indexOf("()") >= 0) {
                    if ((params[i] != null) && (params[i].trim().length() > 0))
                        message = replaceFirst(message, "()", "("+params[i]+")");
                    else
                        message = replaceFirst(message, " ()", "");
                }
                else
                    break;
        add(typeInvalidite, message);
    }
    
    private String replaceFirst(String str, String sub1, String sub2) {
        return str.substring(0, str.indexOf(sub1)) + sub2 + str.substring(str.indexOf(sub1)+sub1.length());
    }

    public static int getNombreMaxLogEquiv() {
        return nombreMaxLogEquiv;
    }
    
    public static void setMessages(Set<String> messages) {
        ValidationException.typesInvalidite = messages;
    }

    public static Set<String> getMessages() {
        return typesInvalidite;
    }
}
