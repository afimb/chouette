package fr.certu.chouette.service.importateur.monoligne.csv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chouette.schema.Line;
import chouette.schema.types.TransportModeNameType;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class LecteurLigneCSV extends Lecteur {

    private IIdentificationManager identificationManager;
    private String cleNom;
    private String cleNomPublic;
    private String cleNumero;
    private String cleCommentaire;
    private String cleMode;

    public Ligne lire(Map<String, String> contenu) {
        Ligne ligne = new Ligne();
        ligne.setName(contenu.get(cleNom));
        ligne.setPublishedName(contenu.get(cleNomPublic));
        ligne.setNumber(contenu.get(cleNumero));
        ligne.setComment(contenu.get(cleCommentaire));
        ligne.setCreationTime(new Date());
        String mode = contenu.get(cleMode);
        if (mode != null) {
            if (TransportModeNameType.BUS.value().equals(mode)) {
                ligne.setTransportModeName(TransportModeNameType.BUS);
            } else if (TransportModeNameType.METRO.value().equals(mode)) {
                ligne.setTransportModeName(TransportModeNameType.METRO);
            } else if (TransportModeNameType.TRAMWAY.value().equals(mode)) {
                ligne.setTransportModeName(TransportModeNameType.TRAMWAY);
            } else if (TransportModeNameType.TRAIN.value().equals(mode)) {
                ligne.setTransportModeName(TransportModeNameType.TRAIN);
            } else if (mode.equals("RER")) {
                ligne.setTransportModeName(TransportModeNameType.LOCALTRAIN);
            }
        }
        ligne.setObjectId(identificationManager.getIdFonctionnel("Line", trimInside(ligne.getName())));
        ligne.setObjectVersion(1);
        return ligne;
    }

    public List<String[]> ecrire(Line ligne, int length, int colonneTitrePartieFixe) {
        List<String[]> resultat = new ArrayList<String[]>();
        for (int i = 0; i < 5; i++) {
            String[] line = new String[length];
            if (ligne != null) {
                switch (i) {
                    case 0:
                        if (colonneTitrePartieFixe < length) {
                            line[colonneTitrePartieFixe] = cleNom;
                        }
                        if (ligne.getName() != null && (colonneTitrePartieFixe + 1 < length)) {
                            line[colonneTitrePartieFixe + 1] = ligne.getName();
                        }
                        break;
                    case 1:
                        if (colonneTitrePartieFixe < length) {
                            line[colonneTitrePartieFixe] = cleNomPublic;
                        }
                        if (ligne.getPublishedName() != null && (colonneTitrePartieFixe + 1 < length)) {
                            line[colonneTitrePartieFixe + 1] = ligne.getPublishedName();
                        }
                        break;
                    case 2:
                        if (colonneTitrePartieFixe < length) {
                            line[colonneTitrePartieFixe] = cleNumero;
                        }
                        if (ligne.getNumber() != null && (colonneTitrePartieFixe + 1 < length)) {
                            line[colonneTitrePartieFixe + 1] = ligne.getNumber();
                        }
                        break;
                    case 3:
                        if (colonneTitrePartieFixe < length) {
                            line[colonneTitrePartieFixe] = cleCommentaire;
                        }
                        if (ligne.getComment() != null && (colonneTitrePartieFixe + 1 < length)) {
                            line[colonneTitrePartieFixe + 1] = ligne.getComment();
                        }
                        break;
                    case 4:
                        if (colonneTitrePartieFixe < length) {
                            line[colonneTitrePartieFixe] = cleMode;
                        }
                        if (ligne.getTransportModeName() != null && (colonneTitrePartieFixe + 1 < length)) {
                            switch (ligne.getTransportModeName()) {
                                case AIR:
                                    line[colonneTitrePartieFixe + 1] = "AIR";
                                    break;
                                case BICYCLE:
                                    line[colonneTitrePartieFixe + 1] = "BICYCLE";
                                    break;
                                case BUS:
                                    line[colonneTitrePartieFixe + 1] = "BUS";
                                    break;
                                case COACH:
                                    line[colonneTitrePartieFixe + 1] = "COACH";
                                    break;
                                case FERRY:
                                    line[colonneTitrePartieFixe + 1] = "FERRY";
                                    break;
                                case LOCALTRAIN:
                                    line[colonneTitrePartieFixe + 1] = "RER";
                                    break;
                                case LONGDISTANCETRAIN:
                                    line[colonneTitrePartieFixe + 1] = "LONGDISTANCETRAIN";
                                    break;
                                case METRO:
                                    line[colonneTitrePartieFixe + 1] = "METRO";
                                    break;
                                case OTHER:
                                    line[colonneTitrePartieFixe + 1] = "OTHER";
                                    break;
                                case PRIVATEVEHICLE:
                                    line[colonneTitrePartieFixe + 1] = "PRIVATEVEHICLE";
                                    break;
                                case RAPIDTRANSIT:
                                    line[colonneTitrePartieFixe + 1] = "RAPIDTRANSIT";
                                    break;
                                case SHUTTLE:
                                    line[colonneTitrePartieFixe + 1] = "SHUTTLE";
                                    break;
                                case TAXI:
                                    line[colonneTitrePartieFixe + 1] = "TAXI";
                                    break;
                                case TRAIN:
                                    line[colonneTitrePartieFixe + 1] = "TRAIN";
                                    break;
                                case TRAMWAY:
                                    line[colonneTitrePartieFixe + 1] = "TRAMWAY";
                                    break;
                                case TROLLEYBUS:
                                    line[colonneTitrePartieFixe + 1] = "TROLLEYBUS";
                                    break;
                                case VAL:
                                    line[colonneTitrePartieFixe + 1] = "VAL";
                                    break;
                                case WALK:
                                    line[colonneTitrePartieFixe + 1] = "WALK";
                                    break;
                                case WATERBORNE:
                                    line[colonneTitrePartieFixe + 1] = "WATERBORNE";
                                    break;
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
        cles.add(cleNomPublic);
        cles.add(cleNumero);
        cles.add(cleCommentaire);
        cles.add(cleMode);
        return cles;
    }

    public void setCleCommentaire(String cleCommentaire) {
        this.cleCommentaire = cleCommentaire;
    }

    public void setCleMode(String cleMode) {
        this.cleMode = cleMode;
    }

    public void setCleNomPublic(String cleNomPublic) {
        this.cleNomPublic = cleNomPublic;
    }

    public void setCleNumero(String cleNumero) {
        this.cleNumero = cleNumero;
    }

    public void setCleNom(String cleNom) {
        this.cleNom = cleNom;
    }

    public void setIdentificationManager(
            IIdentificationManager identificationManager) {
        this.identificationManager = identificationManager;
    }
}
