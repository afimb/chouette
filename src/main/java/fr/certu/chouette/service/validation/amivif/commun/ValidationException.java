package fr.certu.chouette.service.validation.amivif.commun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

public class ValidationException extends ServiceException {
	
	private Map<TypeInvalidite, Set<String>> tridentIdParTypeInvalidite;
	
	public ValidationException(/*String message*/) { 
		super(CodeIncident.DONNEE_INVALIDE,CodeDetailIncident.DEFAULT);
		tridentIdParTypeInvalidite = new Hashtable<TypeInvalidite, Set<String>>();
	}
	
	public ValidationException(/*String message,*/ Throwable exception) {
		super(CodeIncident.DONNEE_INVALIDE, CodeDetailIncident.DEFAULT, exception);
		tridentIdParTypeInvalidite = new Hashtable<TypeInvalidite, Set<String>>();
	}
	
	public void add(TypeInvalidite typeInvalidite, String tridentId) {
		Set<String> tridentIds = tridentIdParTypeInvalidite.get(typeInvalidite);
		if (tridentIds == null) {
			tridentIds = new HashSet<String>();
			tridentIdParTypeInvalidite.put(typeInvalidite, tridentIds);
		}
		if (tridentId != null)
			tridentIds.add(tridentId);
	}
	
	public List<TypeInvalidite> getCategories() {
		return new ArrayList<TypeInvalidite>(tridentIdParTypeInvalidite.keySet());
	}
	
	public Set<String> getTridentIds(TypeInvalidite typeInvalidite) {
		return tridentIdParTypeInvalidite.get(typeInvalidite);
	}
}
