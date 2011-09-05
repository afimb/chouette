package fr.certu.chouette.service.importateur.multilignes.genericcsv.excel;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.genericcsv.ILecteurReseau;
import java.util.ResourceBundle;

public class LecteurReseau implements ILecteurReseau {

    private static final Logger logger = Logger.getLogger(LecteurReseau.class);
    private int colonneDesTitres;      // 7
    private IIdentificationManager identificationManager; // 
    private String cleNom;                // "Nom du réseau"
    private String cleCode;               // "Code Réseau"
    private String cleDescription;        // "Description du réseau"
    private Reseau reseau;
    private Set<String> cellulesNonRenseignees;
    private Set<String> titres;
    private ResourceBundle bundle;
    private String lineNumber;

    @Override
    public void reinit(ResourceBundle bundle) {
        titres = new HashSet<String>();
        reseau = null;
        titres.add(cleNom);
        titres.add(cleCode);
        titres.add(cleDescription);
        cellulesNonRenseignees = new HashSet<String>(titres);
        this.bundle = bundle;
    }

    @Override
    public Reseau getReseau() {
        return reseau;
    }

    private boolean isTitreNouvelleDonnee(String titre) {
        return cleNom.equals(titre);
    }

    private void validerCompletudeDonneeEnCours() {
        if (reseau != null) {
            validerCompletude();
        }
    }

    @Override
    public void validerCompletude() {
        if (cellulesNonRenseignees.size() > 0) {
            String[] cellsTab = cellulesNonRenseignees.toArray(new String[0]);
            String cells = cellsTab[0];
            for (int i = 1; i < cellsTab.length; i++) {
                cells += ", " + cellsTab[i];
            }
            throw new ServiceException(bundle, CodeIncident.ERROR00003, CodeDetailIncident.NETWORK_MISSINGDATA, lineNumber, cells);
        }
        logger.debug("END READING NETWORK.");
    }

    @Override
    public void lire(String[] ligneCSV, String _lineNumber) {
        lineNumber = _lineNumber;
        String titre = ligneCSV[colonneDesTitres];
        String valeur = null;
        if (ligneCSV.length > (colonneDesTitres + 1)) {
            valeur = ligneCSV[colonneDesTitres + 1];
        }
        if (isTitreNouvelleDonnee(titre)) {
            logger.debug("START READING NETWORK.");
            validerCompletudeDonneeEnCours();
            cellulesNonRenseignees = new HashSet<String>(titres);
            reseau = new Reseau();
            reseau.setObjectVersion(1);
            reseau.setCreationTime(new Date());
            reseau.setVersionDate(new Date());
        }
        if (!cellulesNonRenseignees.remove(titre)) {
            throw new ServiceException(bundle, CodeIncident.ERROR00003, CodeDetailIncident.NETWORK_DUPLICATEDATA, lineNumber, titre);
        }
        if (cleNom.equals(titre)) {
            if (valeur == null || valeur.trim().length() == 0) {
                throw new ServiceException(bundle, CodeIncident.ERROR00003, CodeDetailIncident.NETWORK_NULL_NAME, lineNumber);
            } else {
                reseau.setName(valeur.trim());
                reseau.setObjectId(identificationManager.getIdFonctionnel("PtNetwork", reseau.getName().replace(' ', '_')));
            }
        } else if (cleCode.equals(titre)) {
            if (valeur != null && valeur.trim().length() > 0) {
                reseau.setRegistrationNumber(valeur.trim());
            } else {
                reseau.setRegistrationNumber(reseau.getName());
            }
        } else if (cleDescription.equals(titre)) {
            if (valeur != null && valeur.trim().length() > 0) {
                reseau.setDescription(valeur.trim());
                reseau.setComment(valeur.trim());
            }
        }
        //reseau.setId(id);
        //reseau.setCreatorId(creatorId);
        //reseau.setSourceIdentifier(sourceIdentifier);
        //reseau.setSourceName(sourceName);
        //reseau.setSourceType(sourceType);
    }

    @Override
    public boolean isTitreReconnu(String[] ligneCSV) {
        if ((ligneCSV == null) || (ligneCSV.length < colonneDesTitres + 1)) {
            return false;
        }
        String titre = ligneCSV[colonneDesTitres];
        if (titre == null) {
            return false;
        }
        return titres.contains(titre);
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

    public String getCleCode() {
        return cleCode;
    }

    public void setCleCode(String cleCode) {
        this.cleCode = cleCode;
    }

    public String getCleDescription() {
        return cleDescription;
    }

    public void setCleDescription(String cleDescription) {
        this.cleDescription = cleDescription;
    }

    public int getColonneDesTitres() {
        return colonneDesTitres;
    }

    public void setColonneDesTitres(int colonneDesTitres) {
        this.colonneDesTitres = colonneDesTitres;
    }
}
