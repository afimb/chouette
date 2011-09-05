package fr.certu.chouette.service.importateur.multilignes.genericcsv.excel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import chouette.schema.types.TransportModeNameType;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.genericcsv.ILecteurLigne;
import java.util.ResourceBundle;

public class LecteurLigne implements ILecteurLigne {
    
    private static final Logger                 logger                 = Logger.getLogger(LecteurLigne.class);
    private              int                    colonneDesTitres;      // 7
    private              IIdentificationManager identificationManager; // 
    private              String                 cleNom;                // "Nom de la ligne"
    private              String                 cleNomPublic;          // "Nom public"
    private              String                 cleNumero;             // "Numero de la ligne"
    private              String                 cleComment;            // "Commentaire de la ligne"
    private              String                 cleMode;               // "Mode de Transport (BUS,METRO,RER,TRAIN ou TRAMWAY)"
    private              List<Ligne>            lignes;
    private              Ligne                  ligneEnCours;
    private              Set<String>            cellulesNonRenseignees;
    private              Set<String>            titres;
    
    @Override
    public List<Ligne> getLignes() {
	return lignes;
    }
    
    @Override
    public void reinit(ResourceBundle bundle) {
	lignes = new ArrayList<Ligne>();
	ligneEnCours = null;
	titres = new HashSet<String>();
	titres.add(cleNom);
	titres.add(cleNomPublic);
	titres.add(cleNumero);
	titres.add(cleComment);
	titres.add(cleMode);
	cellulesNonRenseignees = new HashSet<String>(titres);
    }
    
    @Override
    public Ligne getLigneEnCours() {
	return ligneEnCours;
    }
    
    private boolean isTitreNouvelleDonnee(String titre) {
	return cleNom.equals(titre);
    }
    
    @Override
    public boolean isTitreReconnu(String[] ligneCSV) {
	if ((ligneCSV == null) || (ligneCSV.length < colonneDesTitres+1))
	    return false;
	String titre = ligneCSV[colonneDesTitres];
	if (titre == null)
	    return false;
	return titres.contains(titre);
    }
    
    private void validerCompletudeDonneeEnCours() {
	if (ligneEnCours != null)
	    validerCompletude();
    }
    
    @Override
    public void validerCompletude() {
	if (cellulesNonRenseignees.size() > 0) {
	    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.LINE_MISSINGDATA,cellulesNonRenseignees);
	}
    }
    
    @Override
    public void lire(String[] ligneCSV, String _lineNumber) {
	if (ligneCSV.length < colonneDesTitres+2)
	    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.COLUMN_COUNT,ligneCSV.length,(colonneDesTitres+2));
	String titre = ligneCSV[colonneDesTitres];
	String valeur = ligneCSV[colonneDesTitres+1];
	if (isTitreNouvelleDonnee(titre)) {
	    validerCompletudeDonneeEnCours();
	    cellulesNonRenseignees = new HashSet<String>(titres);
	    ligneEnCours = new Ligne();
	    ligneEnCours.setObjectVersion(1);
	    ligneEnCours.setCreationTime(new Date());
	    lignes.add(ligneEnCours);
	    logger.debug("NEW LINE : "+valeur);
	}
	if (!cellulesNonRenseignees.remove(titre))
	    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.TIMETABLE_DUPLICATEDATA,titre);
	if (cleNom.equals(titre)) {
	    ligneEnCours.setName(valeur);
            ligneEnCours.setRegistrationNumber(ligneEnCours.getName());
	    ligneEnCours.setObjectId(identificationManager.getIdFonctionnel("Line", ligneEnCours.getName().replace(' ', '_')));
        }
	else if (cleNomPublic.equals(titre))
	    ligneEnCours.setPublishedName(valeur);
	else if (cleNumero.equals(titre))
	    ligneEnCours.setNumber(valeur);
	else if (cleComment.equals(titre))
	    ligneEnCours.setComment(valeur);
	else if (cleMode.equals(titre))
	    if (valeur != null)
		if (valeur.trim().length() != 0) {
		    TransportModeNameType transportMode = getTransportModeNameType(valeur.trim());
		    if (transportMode != null)
			ligneEnCours.setTransportModeName(transportMode);
		}
	//ligneEnCours.setCreatorId(creatorId);
	//ligneEnCours.setId(id);
	//ligneEnCours.setIdReseau(idReseau);
	//ligneEnCours.setIdTransporteur(idTransporteur);
    }
    
    private TransportModeNameType getTransportModeNameType(String transportMode) {
	if (transportMode.equals("BUS"))
	    return TransportModeNameType.BUS;
	if (transportMode.equals("METRO"))
	    return TransportModeNameType.METRO;
	if (transportMode.equals("RER"))
	    return TransportModeNameType.LOCALTRAIN;
	if (transportMode.equals("TRAIN"))
	    return TransportModeNameType.TRAIN;
	if (transportMode.equals("TRAMWAY"))
	    return TransportModeNameType.TRAMWAY;
	return null;
    }
    
    public IIdentificationManager getIdentificationManager() {
	return identificationManager;
    }
    
    public void setIdentificationManager(IIdentificationManager identificationManager) {
	this.identificationManager = identificationManager;
    }
    
    public String getCleNom() {
	return cleNom;
    }
    
    public void setCleNom(String cleNom) {
	this.cleNom = cleNom;
    }
    
    public void setCleNomPublic(String cleNomPublic) {
	this.cleNomPublic = cleNomPublic;
    }
    
    public String getCleNomPublic() {
	return cleNomPublic;
    }
    
    public String getCleNumero() {
	return cleNumero;
    }
    
    public void setCleNumero(String cleNumero) {
	this.cleNumero = cleNumero;
    }
    
    public void setCleComment(String cleComment) {
	this.cleComment = cleComment;
    }
    
    public String getCleComment() {
	return cleComment;
    }
    
    public void setCleMode(String cleMode) {
	this.cleMode = cleMode;
    }
    
    public String getCleMode() {
	return cleMode;
    }
    
    public int getColonneDesTitres() {
	return colonneDesTitres;
    }
    
    public void setColonneDesTitres(int colonneDesTitres) {
	this.colonneDesTitres = colonneDesTitres;
    }
}
