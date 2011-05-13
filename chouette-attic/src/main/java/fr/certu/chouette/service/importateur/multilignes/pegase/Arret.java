package fr.certu.chouette.service.importateur.multilignes.pegase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import chouette.schema.types.ChouetteAreaType;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class Arret {

    private String code;
    private String shortName;
    private String name;
    private String commune;
    private PositionGeographique zoneCommerciale;
    private Map<Ligne, Set<PositionGeographique>> arretsPhysiques;
    private Map<PositionGeographique, Set<ArretItineraire>> arretsItineraires;
    private IIdentificationManager identificationManager;
    private static final Logger logger = Logger.getLogger(fr.certu.chouette.service.importateur.multilignes.pegase.Arret.class);

    public Arret(IIdentificationManager identificationManager, String code, String shortName, String name, String commune) {
        this.identificationManager = identificationManager;
        this.code = code;
        this.shortName = shortName;
        this.name = name;
        this.commune = commune;
        arretsPhysiques = new HashMap<Ligne, Set<PositionGeographique>>();
    }

    public void addArretItineraire(PositionGeographique zoneCommeriale, ArretItineraire arretItineraire) {
        if (arretsItineraires == null) {
            arretsItineraires = new HashMap<PositionGeographique, Set<ArretItineraire>>();
        }
        if (arretsItineraires.get(zoneCommeriale) == null) {
            arretsItineraires.put(zoneCommeriale, new HashSet<ArretItineraire>());
        }
        arretsItineraires.get(zoneCommeriale).add(arretItineraire);
    }

    public Set<ArretItineraire> getArretsItineraires(PositionGeographique zoneCommeriale) {
        if (arretsItineraires == null) {
            return null;
        }
        return arretsItineraires.get(zoneCommeriale);
    }

    public List<PositionGeographique> getArretsPhysiques() {
        List<PositionGeographique> aps = new ArrayList<PositionGeographique>();
        for (Set<PositionGeographique> tmp : arretsPhysiques.values()) {
            aps.addAll(tmp);
        }
        return aps;
    }

    public Set<PositionGeographique> getArretsPhysiques(Ligne ligne) {
        return arretsPhysiques.get(ligne);
    }

    public void putArretsPhysiques(Ligne ligne, Set<PositionGeographique> aps) {
        arretsPhysiques.put(ligne, aps);
    }

    public void addArretPhysique(Ligne ligne, PositionGeographique arretPhysique) {
        if (arretsPhysiques.get(ligne) == null) {
            putArretsPhysiques(ligne, new HashSet<PositionGeographique>());
        }
        arretsPhysiques.get(ligne).add(arretPhysique);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getCommune() {
        return commune;
    }

    private void zoneCommerciale(Connection connexion) {
        String objectId = null;
        String countryCode = null;
        try {
            String aName = new String(shortName);
            aName = aName.replaceAll("'", "''");
            String aCommune = new String(commune);
            aCommune = aCommune.replaceAll("'", "''");
            String select = "SELECT objectid, countrycode FROM stoparea WHERE name='" + aName + " [" + aCommune + "]' AND areatype='CommercialStopPoint';";
            Statement st = connexion.createStatement();
            ResultSet rs = st.executeQuery(select);
            if (rs.next()) {
                objectId = rs.getString(1);
                countryCode = rs.getString(2);
                logger.error("IL EXISTE UN ARRET COMMERCIAL AVEC L'OBJECT ID : " + objectId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        zoneCommerciale = new PositionGeographique();
        zoneCommerciale.setAreaType(ChouetteAreaType.COMMERCIALSTOPPOINT);
        zoneCommerciale.setComment("Arret commercial " + name);
        if (countryCode != null) {
            zoneCommerciale.setCountryCode(countryCode);
        } else {
            zoneCommerciale.setCountryCode(commune);
        }
        zoneCommerciale.setCreationTime(new Date());
        zoneCommerciale.setName(shortName + " [" + commune + "]");
        if (objectId == null) {
            objectId = identificationManager.getIdFonctionnel("StopArea", String.valueOf(LecteurPrincipal.counter++));
        }
        zoneCommerciale.setObjectId(objectId);
        zoneCommerciale.setObjectVersion(1);
        zoneCommerciale.setNearestTopicName(name);
        zoneCommerciale.setRegistrationNumber(code);
    }

    public PositionGeographique arretPhysique(Ligne ligne, char sens) {
        PositionGeographique arretPhysique = new PositionGeographique();
        arretPhysique.setAreaType(ChouetteAreaType.BOARDINGPOSITION);
        arretPhysique.setComment("Arret physique " + name + "[" + sens + "] [" + commune + "]");
        arretPhysique.setCountryCode(commune);
        arretPhysique.setCreationTime(new Date());
        arretPhysique.setName(shortName + "[" + sens + "] [" + commune + "]");
        arretPhysique.setObjectId(identificationManager.getIdFonctionnel("StopArea", String.valueOf(LecteurPrincipal.counter++)));
        arretPhysique.setNearestTopicName(name + "[" + sens + "]");
        arretPhysique.setRegistrationNumber(code + "_" + arretsPhysiques.get(ligne).size());
        addArretPhysique(ligne, arretPhysique);
        return arretPhysique;
    }

    public PositionGeographique getZoneCommerciale(Connection connexion) {
        if (zoneCommerciale == null) {
            zoneCommerciale(connexion);
        }
        return zoneCommerciale;
    }

    public PositionGeographique getArretPhysique(Ligne ligne, char sens) {
        if (getArretsPhysiques(ligne) == null) {
            putArretsPhysiques(ligne, new HashSet<PositionGeographique>());
        }
        Set<PositionGeographique> arretsPhysiques = getArretsPhysiques(ligne);
        for (PositionGeographique arretPhysique : arretsPhysiques) {
            if (arretPhysique.getName().indexOf("[" + sens + "]") > 0) {
                return arretPhysique;
            }
        }
        return arretPhysique(ligne, sens);
    }
}
