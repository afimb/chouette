package fr.certu.chouette.ihm;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.PrincipalAware;
import org.apache.struts2.interceptor.PrincipalProxy;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

public class RechercheItineraireAction extends GeneriqueAction
{
	private final Log				log	= LogFactory.getLog(RechercheItineraireAction.class);
	
	//	Ajout d'une chaine pour les redirections struts
	private static String RECHERCHE = "recherche";
	
	public String execute()
	{
		return RECHERCHE;
	}
}
