package fr.certu.chouette.service.importateur.multilignes.genericcsv.excel;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.genericcsv.ILecteurTransporteur;
import java.util.ResourceBundle;

public class LecteurTransporteur implements ILecteurTransporteur {

    private static final Logger logger = Logger.getLogger(LecteurTransporteur.class);
    private int colonneDesTitres;      // 7
    private IIdentificationManager identificationManager; // 
    private String cleNom;                // "Nom de l'entreprise de transport"
    private String cleCode;               // "Code transporteur"
    private String cleNomCourt;           // "Nom court"
    private String cleDescription;        // "Description du transporteur"
    private String cleCodePostal;         // "Code postal"
    private String cleTelephone;          // "Téléphone"
    private String cleFax;                // "Fax"
    private String cleEmail;              // "Email"
    private Transporteur transporteur;
    private Set<String> cellulesNonRenseignees;
    private Set<String> titres;
    private ResourceBundle bundle;
    private String lineNumber;

    @Override
    public Transporteur getTransporteur() {
        return transporteur;
    }

    @Override
    public void reinit(ResourceBundle bundle) {
        titres = new HashSet<String>();
        transporteur = null;
        titres.add(cleNom);
        titres.add(cleCode);
        titres.add(cleNomCourt);
        titres.add(cleDescription);
        titres.add(cleCodePostal);
        titres.add(cleTelephone);
        titres.add(cleFax);
        titres.add(cleEmail);
        cellulesNonRenseignees = new HashSet<String>(titres);
        this.bundle = bundle;
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

    private boolean isTitreNouvelleDonnee(String titre) {
        return cleNom.equals(titre);
    }

    private void validerCompletudeDonneeEnCours() {
        if (transporteur != null) {
            validerCompletude();
        }
    }

    @Override
    public void lire(String[] ligneCSV, String _lineNumber) {
        this.lineNumber = _lineNumber;
        if (ligneCSV.length < colonneDesTitres + 2) {
            throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.COLUMN_COUNT, ligneCSV.length, (colonneDesTitres + 2));
        }
        String titre = ligneCSV[colonneDesTitres];
        String valeur = ligneCSV[colonneDesTitres + 1];
        if (isTitreNouvelleDonnee(titre)) {
            logger.debug("START READING COMPANY.");
            validerCompletudeDonneeEnCours();
            cellulesNonRenseignees = new HashSet<String>(titres);
            transporteur = new Transporteur();
            transporteur.setObjectVersion(1);
            transporteur.setCreationTime(new Date());
        }
        if (!cellulesNonRenseignees.remove(titre)) {
            throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.COMPANY_DUPLICATELINE, titre);
        }
        if (cleNom.equals(titre)) {
            transporteur.setName(valeur);
            transporteur.setObjectId(identificationManager.getIdFonctionnel("Company", transporteur.getName().replace(' ', '_')));
        } else if (cleCode.equals(titre)) {
            transporteur.setRegistrationNumber(valeur);
        } else if (cleNomCourt.equals(titre)) {
            transporteur.setShortName(titre);
        } else if (cleDescription.equals(titre)) {
            transporteur.setOrganisationalUnit(valeur);
        } else if (cleCodePostal.equals(titre)) {
            transporteur.setCode(titre);
        } else if (cleTelephone.equals(titre)) {
            transporteur.setPhone(titre);
        } else if (cleFax.equals(titre)) {
            transporteur.setFax(titre);
        } else if (cleEmail.equals(titre)) {
            transporteur.setEmail(titre);
        }
        //transporteur.setCreatorId(creatorId);
        //transporteur.setId(id);
        //transporteur.setOperatingDepartmentName(operatingDepartmentName);
    }

    @Override
    public void validerCompletude() {
        if (cellulesNonRenseignees.size() > 0) {
            throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.COMPANY_MISSINGDATA, cellulesNonRenseignees.toString());
        }
        logger.debug("START READING COMPANY.");
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

    public int getColonneDesTitres() {
        return colonneDesTitres;
    }

    public void setColonneDesTitres(int colonneDesTitres) {
        this.colonneDesTitres = colonneDesTitres;
    }

    public String getCleNomCourt() {
        return cleNomCourt;
    }

    public void setCleNomCourt(String cleNomCourt) {
        this.cleNomCourt = cleNomCourt;
    }

    public String getCleDescription() {
        return cleDescription;
    }

    public void setCleDescription(String cleDescription) {
        this.cleDescription = cleDescription;
    }

    public String getCleCodePostal() {
        return cleCodePostal;
    }

    public void setCleCodePostal(String cleCodePostal) {
        this.cleCodePostal = cleCodePostal;
    }

    public String getCleTelephone() {
        return cleTelephone;
    }

    public void setCleTelephone(String cleTelephone) {
        this.cleTelephone = cleTelephone;
    }

    public String getCleFax() {
        return cleFax;
    }

    public void setCleFax(String cleFax) {
        this.cleFax = cleFax;
    }

    public String getCleEmail() {
        return cleEmail;
    }

    public void setCleEmail(String cleEmail) {
        this.cleEmail = cleEmail;
    }
}
