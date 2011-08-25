package fr.certu.chouette.service.importateur.monoligne.csv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chouette.schema.PTNetwork;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class LecteurReseauCSV extends Lecteur {

    private IIdentificationManager identificationManager;
    private String cleNom;
    private String cleCode;
    private String cleDescription;

    public Reseau lire(Map<String, String> contenu) {
        Reseau reseau = new Reseau();

        reseau.setName(contenu.get(cleNom));
        reseau.setRegistrationNumber(contenu.get(cleCode));
        reseau.setDescription(contenu.get(cleDescription));
        reseau.setCreationTime(new Date());
        reseau.setVersionDate(new Date());

        reseau.setObjectId(identificationManager.getIdFonctionnel("PtNetwork", trimInside(reseau.getName())));
        reseau.setObjectVersion(1);
        return reseau;
    }

    public List<String[]> ecrire(PTNetwork ptNetwork, int length, int colonneTitrePartieFixe) {
        List<String[]> resultat = new ArrayList<String[]>();
        for (int i = 0; i < 3; i++) {
            String[] line = new String[length];
            if (ptNetwork != null) {
                switch (i) {
                    case 0:
                        if (ptNetwork.getName() != null) {
                            if (colonneTitrePartieFixe < length) {
                                line[colonneTitrePartieFixe] = cleNom;
                            }
                            if (colonneTitrePartieFixe + 1 < length) {
                                line[colonneTitrePartieFixe + 1] = ptNetwork.getName();
                            }
                        }
                        break;
                    case 1:
                        if ((ptNetwork.getRegistration() != null) && (ptNetwork.getRegistration().getRegistrationNumber() != null)) {
                            if (colonneTitrePartieFixe < length) {
                                line[colonneTitrePartieFixe] = cleCode;
                            }
                            if (colonneTitrePartieFixe + 1 < length) {
                                line[colonneTitrePartieFixe + 1] = ptNetwork.getRegistration().getRegistrationNumber();
                            }
                        }
                        break;
                    case 2:
                        if (ptNetwork.getDescription() != null) {
                            if (colonneTitrePartieFixe < length) {
                                line[colonneTitrePartieFixe] = cleDescription;
                            }
                            if (colonneTitrePartieFixe + 1 < length) {
                                line[colonneTitrePartieFixe + 1] = ptNetwork.getDescription();
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
        cles.add(cleCode);
        cles.add(cleDescription);
        return cles;
    }

    public void setCleCode(String cleCode) {
        this.cleCode = cleCode;
    }

    public void setCleDescription(String cleDescription) {
        this.cleDescription = cleDescription;
    }

    public void setCleNom(String cleNom) {
        this.cleNom = cleNom;
    }

    public String getCleNom() {
        return cleNom;
    }

    public void setIdentificationManager(
            IIdentificationManager identificationManager) {
        this.identificationManager = identificationManager;
    }
}
