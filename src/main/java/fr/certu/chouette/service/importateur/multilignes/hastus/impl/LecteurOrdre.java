package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
//import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurOrdre;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import org.apache.log4j.Logger;

public class LecteurOrdre implements ILecteurOrdre {
    
    private static final Logger                    logger                                = Logger.getLogger(LecteurOrdre.class);
    private              int                       counter;
    //private              IIdentificationManager    identificationManager;                // 
    private              String                    cleCode;                              // "07"
    private              Map<String, List<String>> listeOrdonneeArretsParItineraireName;
    
    public boolean isTitreReconnu(String[] ligneCSV) {
	if ((ligneCSV == null) || (ligneCSV.length == 0))
	    return false;
	return ligneCSV[0].equals(getCleCode());
    }
    
    public void lire(String[] ligneCSV) {
	if ((ligneCSV == null) || (ligneCSV.length == 0))
	    return;
	if (ligneCSV.length != 4)
	    throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_ORDRE, "La longeur des lignes dans \"Ordre des Arr�ts\" est 4 : "+ligneCSV.length);
	if ((ligneCSV[1] == null) || (ligneCSV[1].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_ITINERAIRENAME_ORDRE, "Le nom d'itineraire dans \"Ordre des Arr�ts\" ne peut �tre null.");
	List<String> listeOrdonneeArrets = listeOrdonneeArretsParItineraireName.get(ligneCSV[1].trim());
	if (listeOrdonneeArrets == null)
	    listeOrdonneeArrets = new ArrayList<String>();
	if ((ligneCSV[2] == null) || (ligneCSV[2].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_ARRETNAME_ORDRE, "Le nom d'arr�t dans \"Ordre des Arr�ts\" ne peut �tre null.");
	if ((ligneCSV[3] == null) || (ligneCSV[3].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_ORDRE_ORDRE, "L'ordre dans \"Ordre des Arr�ts\" ne peut �tre null.");
	try {
	    int ordre = Integer.parseInt(ligneCSV[3].trim());
	    if ((listeOrdonneeArrets.size() > ordre) && (listeOrdonneeArrets.get(ordre) != null))
		throw new ServiceException(CodeIncident.INVALIDE_ORDRE_ORDRE, "Un seul arret dans un itineraire ("+ligneCSV[1].trim()+") par position ("+ligneCSV[3].trim()+") : "+listeOrdonneeArrets.get(ordre)+" , "+ligneCSV[2].trim());
	    listeOrdonneeArrets.add(ordre, ligneCSV[2].trim());
	}
	catch(NumberFormatException e) {
	    throw new ServiceException(CodeIncident.INVALIDE_ORDRE_ORDRE, "L'ordre dans \"Ordre des Arr�ts\" doit �tre un entier positif : "+ligneCSV[3].trim());
	}
	listeOrdonneeArretsParItineraireName.put(ligneCSV[1].trim(), listeOrdonneeArrets);
    }
    
    public void reinit() {
	listeOrdonneeArretsParItineraireName = new HashMap<String, List<String>>();
    }
    
    public int getCounter() {
	return counter;
    }
    
    public void setCounter(int counter) {
	this.counter = counter;
    }
    /*
      public IIdentificationManager getIdentificationManager() {
      return identificationManager;
      }
      
	public void setIdentificationManager(IIdentificationManager identificationManager) {
	this.identificationManager = identificationManager;
	}*/
    
    public String getCleCode() {
	return cleCode;
    }
    
    public void setCleCode(String cleCode) {
	this.cleCode = cleCode;
    }
    
    public Map<String, List<String>> getListeOrdonneeArretsParItineraireName() {
	return listeOrdonneeArretsParItineraireName;
    }
}
