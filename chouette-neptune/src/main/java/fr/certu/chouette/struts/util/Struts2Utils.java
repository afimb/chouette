package fr.certu.chouette.struts.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opensymphony.xwork2.config.entities.ActionConfig;

public final class Struts2Utils {
	
	private static final String CHOUETTE_struts_PACKAGE_NAME = "fr.certu.chouette.struts.";
	
	// LES URLS SUPPORTEES PAR LE PATTERN MATCHER 
	// SONT POUR L'INSTANT DE DEUX FORMES / 
    // # 1 / http:// .. liste_NomClasseAction.action .. 
    // # 2 / http:// .. crud_NomClasseAction!edit.action ..
	
	// (?:chaine)?? SIGNIFIE QUE chaine DOIT ETRE CONSIDERE COMME UN GROUPE NON CAPTURABLE POUVANT APARAITRE DANS L'URL  
	// (LE GROUPE NE POURRA DONC PAS ETRE EXTRAIT EN FAISANT / matcher.group(n))
	private static final Pattern actionAndActionMethodExtractionPattern = Pattern.compile("\\/(\\w*)_(\\w*)(?:\\!edit)??.action");
	
	public static String getModelClassFullNameFromConfig (final ActionConfig actionProxyConfig) throws Exception {
		
		StringBuffer className = new StringBuffer();
		
		className.append(actionProxyConfig.getPackageName());
		className.append(".");
		className.append(actionProxyConfig.getClassName().toLowerCase());
		className.append(".");

		if (actionProxyConfig.getMethodName() != null) {
			
			// ON ASSUME QUE C'EST DU LIST
			className.append("List");
			
		} else {
		
			// ON ASSUME QUE C'EST DU CRUD
			// CAR STRUTS NE RENSEIGNE PAS
			// LE NOM DE LA METHODE DANS CE CAS
			className.append("Crud");
		}
		
		className.append(actionProxyConfig.getClassName());
		className.append("Model");
		
		return className.toString();
	}
	
	public static String getModelClassFullNameFromUrl (final String url) throws Exception {
		
		String actionClassName = Struts2Utils.getActionClassNameFromUrl(url);
		
		if (actionClassName == null || actionClassName.isEmpty()) {
			
			throw new Exception("IMPOSSIBLE DE DETERMINER LE NOM DE LA CLASSE DE MODEL CORRESPONDANT A L'URL / " + url + " CAR LE NOM DE LA CLASSE DE L'ACTION STRUTS CORRESPONDANT A LA MEME URL EST INDETERMINABLE !");
		}
		
        String actionClassMethodName = Struts2Utils.getActionClassMethodNameFromUrl(url);
        
		if (actionClassMethodName == null || actionClassMethodName.isEmpty()) {
			
			throw new Exception("IMPOSSIBLE DE DETERMINER LE NOM DE LA CLASSE DE MODEL CORRESPONDANT A L'URL / " + url + " CAR LE NOM DE LA METHODE DE LA CLASSE DE L'ACTION STRUTS CORRESPONDANT A LA MEME URL EST INDETERMINABLE !");
		}
		
		String modelClassName = actionClassMethodName.substring(0, 1).toUpperCase() + actionClassMethodName.substring(1) + actionClassName + "Model";
        String modelClassPackageName = CHOUETTE_struts_PACKAGE_NAME + actionClassName.toLowerCase();
        
		return modelClassPackageName + "." + modelClassName;
	}

	public static String getActionClassMethodNameFromUrl (final String url) {
		
        Matcher matcher = actionAndActionMethodExtractionPattern.matcher(url);

        if (matcher.find()) {
        	return matcher.group(1);
        } 	
        
		return null;
	}
	
	public static String getActionClassNameFromUrl (final String url) {
		
        Matcher matcher = actionAndActionMethodExtractionPattern.matcher(url);

        if (matcher.find()) {
        	return matcher.group(2);
        } 	
        
		return null;
	}
}
