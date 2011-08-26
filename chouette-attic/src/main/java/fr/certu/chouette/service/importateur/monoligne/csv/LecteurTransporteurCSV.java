package fr.certu.chouette.service.importateur.monoligne.csv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chouette.schema.Company;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class LecteurTransporteurCSV extends Lecteur {

    private IIdentificationManager identificationManager;
    private String cleNom;
    private String cleRegistre;
    private String cleNomCourt;
    private String cleDescription;
    private String cleCodePostal;
    private String cleTelephone;
    private String cleFax;
    private String cleEmail;

    public Transporteur lire(Map<String, String> contenu) {
        Transporteur transporteur = new Transporteur();

        transporteur.setName(contenu.get(cleNom));
        transporteur.setRegistrationNumber(contenu.get(cleRegistre));
        transporteur.setShortName(contenu.get(cleNomCourt));
        transporteur.setOrganisationalUnit(contenu.get(cleDescription));
        transporteur.setCode(contenu.get(cleCodePostal));
        transporteur.setPhone(contenu.get(cleTelephone));
        transporteur.setFax(contenu.get(cleFax));
        transporteur.setEmail(contenu.get(cleEmail));

        transporteur.setObjectId(identificationManager.getIdFonctionnel("Company", trimInside(transporteur.getName())));
        transporteur.setObjectVersion(1);
        transporteur.setCreationTime(new Date());
        return transporteur;
    }

    public List<String[]> ecrire(Company[] companies, int length, int colonneTitrePartieFixe) {
        List<String[]> resultat = new ArrayList<String[]>();
        for (int i = 0; i < 8; i++) {
            String[] line = new String[length];
            if ((companies != null) && (companies.length != 0)) {
                switch (i) {
                    case 0:
                        if ((companies[0] != null) && (companies[0].getName() != null)) {
                            if (colonneTitrePartieFixe < length) {
                                line[colonneTitrePartieFixe] = cleNom;
                            }
                            if (colonneTitrePartieFixe + 1 < length) {
                                line[colonneTitrePartieFixe + 1] = companies[0].getName();
                            }
                        }
                        break;
                    case 1:
                        if ((companies[0] != null) && (companies[0].getRegistration() != null) && (companies[0].getRegistration().getRegistrationNumber() != null)) {
                            if (colonneTitrePartieFixe < length) {
                                line[colonneTitrePartieFixe] = cleRegistre;
                            }
                            if (colonneTitrePartieFixe + 1 < length) {
                                line[colonneTitrePartieFixe + 1] = companies[0].getRegistration().getRegistrationNumber();
                            }
                        }
                        break;
                    case 2:
                        if ((companies[0] != null) && (companies[0].getShortName() != null)) {
                            if (colonneTitrePartieFixe < length) {
                                line[colonneTitrePartieFixe] = cleNomCourt;
                            }
                            if (colonneTitrePartieFixe + 1 < length) {
                                line[colonneTitrePartieFixe + 1] = companies[0].getShortName();
                            }
                        }
                        break;
                    case 3:
                        if ((companies[0] != null) && (companies[0].getOrganisationalUnit() != null)) {
                            if (colonneTitrePartieFixe < length) {
                                line[colonneTitrePartieFixe] = cleDescription;
                            }
                            if (colonneTitrePartieFixe + 1 < length) {
                                line[colonneTitrePartieFixe + 1] = companies[0].getOrganisationalUnit();
                            }
                        }
                        break;
                    case 4:
                        if ((companies[0] != null) && (companies[0].getCode() != null)) {
                            if (colonneTitrePartieFixe < length) {
                                line[colonneTitrePartieFixe] = cleCodePostal;
                            }
                            if (colonneTitrePartieFixe + 1 < length) {
                                line[colonneTitrePartieFixe + 1] = companies[0].getCode();
                            }
                        }
                        break;
                    case 5:
                        if ((companies[0] != null) && (companies[0].getPhone() != null)) {
                            if (colonneTitrePartieFixe < length) {
                                line[colonneTitrePartieFixe] = cleTelephone;
                            }
                            if (colonneTitrePartieFixe + 1 < length) {
                                line[colonneTitrePartieFixe + 1] = companies[0].getPhone();
                            }
                        }
                        break;
                    case 6:
                        if ((companies[0] != null) && (companies[0].getFax() != null)) {
                            if (colonneTitrePartieFixe < length) {
                                line[colonneTitrePartieFixe] = cleFax;
                            }
                            if (colonneTitrePartieFixe + 1 < length) {
                                line[colonneTitrePartieFixe + 1] = companies[0].getFax();
                            }
                        }
                        break;
                    case 7:
                        if ((companies[0] != null) && (companies[0].getEmail() != null)) {
                            if (colonneTitrePartieFixe < length) {
                                line[colonneTitrePartieFixe] = cleEmail;
                            }
                            if (colonneTitrePartieFixe + 1 < length) {
                                line[colonneTitrePartieFixe + 1] = companies[0].getEmail();
                            }
                        }
                        break;
                }
            }
            resultat.add(line);
        }
        return resultat;
    }

    public Set<String> getCles() {
        Set<String> cles = new HashSet<String>();
        cles.add(cleNom);
        cles.add(cleRegistre);
        cles.add(cleNomCourt);
        cles.add(cleCodePostal);
        cles.add(cleTelephone);
        cles.add(cleFax);
        cles.add(cleEmail);
        cles.add(cleDescription);
        return cles;
    }

    public void setCleCodePostal(String cleCodePostal) {
        this.cleCodePostal = cleCodePostal;
    }

    public void setCleDescription(String cleDescription) {
        this.cleDescription = cleDescription;
    }

    public void setCleEmail(String cleEmail) {
        this.cleEmail = cleEmail;
    }

    public void setCleFax(String cleFax) {
        this.cleFax = cleFax;
    }

    public void setCleNom(String cleNom) {
        this.cleNom = cleNom;
    }

    public void setCleNomCourt(String cleNomCourt) {
        this.cleNomCourt = cleNomCourt;
    }

    public void setCleRegistre(String cleRegistre) {
        this.cleRegistre = cleRegistre;
    }

    public void setCleTelephone(String cleTelephone) {
        this.cleTelephone = cleTelephone;
    }

    public void setIdentificationManager(
            IIdentificationManager identificationManager) {
        this.identificationManager = identificationManager;
    }
}
