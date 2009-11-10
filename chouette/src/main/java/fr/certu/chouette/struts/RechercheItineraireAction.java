package fr.certu.chouette.struts;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


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
