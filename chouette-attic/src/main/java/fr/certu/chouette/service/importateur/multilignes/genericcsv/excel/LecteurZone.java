package fr.certu.chouette.service.importateur.multilignes.genericcsv.excel;

import chouette.schema.Address;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import fr.certu.chouette.service.database.ChouetteDriverManagerDataSource;
import chouette.schema.types.ChouetteAreaType;
import chouette.schema.types.LongLatTypeType;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.genericcsv.ILecteurZone;
import fr.certu.chouette.service.validation.LongLatType;
import java.util.ResourceBundle;

public class LecteurZone implements ILecteurZone {
    
    private static final Logger                                               logger                        = Logger.getLogger(LecteurZone.class);
    private              int                                                  colonneDesTitres;             // 7
    private              IIdentificationManager                               identificationManager;        // 
    private              Map<String, PositionGeographique>                    zones;
    private              Map<PositionGeographique, Set<PositionGeographique>> arretsPhysiquesParZoneParente;
    private              Map<Identification, PositionGeographique>            arretsPhysiques;
    private              List<PositionGeographique>                           arretsPhysiquesOrdonnes;
    private              Map<Ligne, List<PositionGeographique>>               arretsPhysiquesParLigne;
    private              Map<Ligne, List<PositionGeographique>>               zonesParLigne;
    private              Set<PositionGeographique>                            zonesDeLigne;
    private              int                                                  counter;
    private              Map<Ligne, Map<String, String>>                      zoneParenteParObjectId;
    private              ChouetteDriverManagerDataSource                      managerDataSource;
    private              Connection                                           connexion           = null;
    
    class Identification implements Comparable<Identification> {
	
	String name;
	String adresse;
	String codePostal;
	
	Identification(String name, String adresse, String codePostal) {
	    this.name = name;
	    this.adresse = adresse;
	    this.codePostal = codePostal;
	}
	
	void setName(String name) {
	    this.name = name;
	}
	
	String getName() {
	    return name;
	}
	
	void setAdesse(String adresse) {
	    this.adresse = adresse;
	}
	
	String getAdresse() {
	    return adresse;
	}
		
	void setCodePostal(String codePostal) {
	    this.codePostal = codePostal;
	}
	
	String getCodePostal() {
	    return codePostal;
	}
	
        @Override
	public int compareTo(Identification id) {
	    int na = name.compareTo(id.getName());
	    if (na != 0)
		return na;
	    int ad = adresse.compareTo(id.getAdresse());
	    if (ad != 0)
		return ad;
	    return codePostal.compareTo(id.getCodePostal());
	}
	
        @Override
	public boolean equals(Object obj) {
	    if (obj != null)
		if (obj instanceof Identification)
		    if (this.compareTo((Identification)obj) == 0)
			return true;
	    return false;
	}

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 37 * hash + (this.adresse != null ? this.adresse.hashCode() : 0);
            hash = 37 * hash + (this.codePostal != null ? this.codePostal.hashCode() : 0);
            return hash;
        }
        
        public String toSimpleString() {
            String result = "";
            if (name != null)
                result += name.trim().replace(' ', '_');
            if (adresse != null)
                result += '_' + adresse.trim().replace(' ', '_');
            if (codePostal != null)
                result += '_' + codePostal.trim().replace(' ', '_');
            return result;
        }
    }
    
    @Override
    public Map<String, String> getZoneParenteParObjectId(Ligne ligne) {
	return zoneParenteParObjectId.get(ligne);
    }
    
    @Override
    public List<PositionGeographique> getArretsPhysiques() {
	return arretsPhysiquesOrdonnes;
    }
    
    @Override
    public List<PositionGeographique> getArretsPhysiques(Ligne ligne) {
	return arretsPhysiquesParLigne.get(ligne);
    }
    
    @Override
    public List<PositionGeographique> getZones(Ligne ligne) {
	return zonesParLigne.get(ligne);
    }
    
    @Override
    public Map<PositionGeographique, Set<PositionGeographique>> getArretsPhysiquesParZoneParente() {
	return arretsPhysiquesParZoneParente;
    }
    
    @Override
    public Map<String, PositionGeographique> getZones() {
	return zones;
    }
    
    @Override
    public void reinit(ResourceBundle bundle) {
	zones = new HashMap<String, PositionGeographique>();
	arretsPhysiquesParZoneParente = new HashMap<PositionGeographique, Set<PositionGeographique>>();
	arretsPhysiquesParLigne = new HashMap<Ligne, List<PositionGeographique>>();
	zonesParLigne = new HashMap<Ligne, List<PositionGeographique>>();
	zoneParenteParObjectId = new HashMap<Ligne, Map<String, String>>();
	counter = 0;
	init();
    }
    
    @Override
    public void init() {
	arretsPhysiques = new HashMap<Identification, PositionGeographique>();
	arretsPhysiquesOrdonnes = new ArrayList<PositionGeographique>();
	zonesDeLigne = new HashSet<PositionGeographique>();
    }
    
    @Override
    public boolean isTitreReconnu(String[] ligneCSV) {
	return true;
    }
    
    @Override
    public void lire(String[] ligneCSV, String _lineNumber) {
    }
    
    @Override
    public void lire(Ligne ligne, String[] ligneCSV) {
	PositionGeographique positionGeographique;
	if (ligneCSV[6] != null)
	    if (ligneCSV[6].trim().length() > 0) {
		positionGeographique = zones.get(ligneCSV[6].trim());
		if (positionGeographique == null) {
		    /*
		    boolean created = false;
		    try {
			Properties props = new Properties();
			props.setProperty("user", managerDataSource.getUsername());
			props.setProperty("password", managerDataSource.getPassword());
			props.setProperty("allowEncodingChanges", "true");
			connexion = DriverManager.getConnection(managerDataSource.getUrl(), props);
			connexion.setAutoCommit(false);
			String name = ligneCSV[6].trim().replaceAll("'", "''");
			ResultSet rs = connexion.createStatement().executeQuery("SELECT id, parentId, objectid, objectversion, creationtime, creatorid, \"name\", \"comment\", areatype, registrationnumber, nearesttopicname,  farecode, longitude, latitude, longlattype, x, y, projectiontype, countrycode, streetname FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE \"name\"='"+name+"' AND areatype='CommercialStopPoint';");
			if (rs.next()) {
			    //TODO. LA CAS idparent != null
			    positionGeographique = new PositionGeographique();
			    positionGeographique.setName(ligneCSV[6].trim());
			    zones.put(positionGeographique.getName(), positionGeographique);
			    positionGeographique.setObjectId(rs.getString(3));
			    positionGeographique.setAreaType(ChouetteAreaType.COMMERCIALSTOPPOINT);
			    if (rs.getObject(8) != null)
				positionGeographique.setComment(rs.getString(8));
			    if ((ligneCSV[5] != null) && (ligneCSV[5].trim().length() > 0))
				positionGeographique.setCountryCode(ligneCSV[5].trim()); // Code postal
			    else if (rs.getObject(19) != null)
				positionGeographique.setCountryCode(rs.getString(19));
			    if (rs.getObject(5) != null)
				positionGeographique.setCreationTime(rs.getDate(5));
			    if (rs.getObject(6) != null)
				positionGeographique.setCreatorId(rs.getString(6));
			    if (rs.getObject(12) != null)
				positionGeographique.setFareCode(rs.getInt(12));
			    if ((ligneCSV[2] != null) && (ligneCSV[2].trim().length() > 0))
				positionGeographique.setLatitude(new BigDecimal(ligneCSV[2].trim()));
			    else if (rs.getObject(13) != null)
				positionGeographique.setLatitude(rs.getBigDecimal(13));
			    if ((ligneCSV[3] != null) && (ligneCSV[3].trim().length() > 0))
				positionGeographique.setLongitude(new BigDecimal(ligneCSV[3].trim()));
			    else if (rs.getObject(14) != null)
				positionGeographique.setLongitude(rs.getBigDecimal(14));
			    if (rs.getObject(15) != null)
				if ("WGS84".equals(rs.getString(15).toUpperCase()))
				    positionGeographique.setLongLatType(LongLatTypeType.WGS84);
				else if ("WGS92".equals(rs.getString(15).toUpperCase()))
				    positionGeographique.setLongLatType(LongLatTypeType.WGS92);
				else if ("STANDARD".equals(rs.getString(15).toUpperCase()))
				    positionGeographique.setLongLatType(LongLatTypeType.STANDARD);
				else
				    positionGeographique.setLongLatType(LongLatTypeType.STANDARD);
			    else
				positionGeographique.setLongLatType(LongLatTypeType.STANDARD);
			    if (rs.getObject(11) != null)
				positionGeographique.setNearestTopicName(rs.getString(11));
			    if (rs.getObject(4) != null)
				positionGeographique.setObjectVersion(rs.getInt(4));
			    else
				positionGeographique.setObjectVersion(1);
			    if (rs.getObject(18) != null)
				positionGeographique.setProjectionType(rs.getString(18));
			    if (rs.getObject(10) != null)
				positionGeographique.setRegistrationNumber(rs.getString(10));
			    if ((ligneCSV[4] != null) && (ligneCSV[4].trim().length() > 0))
				positionGeographique.setStreetName(ligneCSV[4].trim());
			    else if (rs.getObject(20) != null)
				positionGeographique.setStreetName(rs.getString(20));
			    if ((ligneCSV[0] != null) && (ligneCSV[0].trim().length() > 0))
				positionGeographique.setX(new BigDecimal(ligneCSV[0].trim()));
			    else if (rs.getObject(16) != null)
				positionGeographique.setX(rs.getBigDecimal(16));
			    if ((ligneCSV[1] != null) && (ligneCSV[1].trim().length() > 0))
				positionGeographique.setY(new BigDecimal(ligneCSV[1].trim()));
			    else if (rs.getObject(17) != null)
				positionGeographique.setY(rs.getBigDecimal(17));
			    created = true;
			}
		    }
		    catch (SQLException e) {
		    }
		    if (!created) {
		    */
		    logger.debug("CREATING COMMERCIAL STOP : "+ligneCSV[6].trim());
		    positionGeographique = new PositionGeographique();
		    positionGeographique.setName(ligneCSV[6].trim());
		    counter++;
		    positionGeographique.setObjectId(identificationManager.getIdFonctionnel("StopArea", ligneCSV[6].trim().replace(' ', '_')));
		    positionGeographique.setAreaType(ChouetteAreaType.COMMERCIALSTOPPOINT);
                    /*
                    if (ligneCSV[0] != null)
			if (ligneCSV[0].trim().length() > 0)
                            positionGeographique.setX(new BigDecimal(ligneCSV[0]));
                    if (ligneCSV[1] != null)
			if (ligneCSV[1].trim().length() > 0)
                            positionGeographique.setY(new BigDecimal(ligneCSV[1]));
                    if (positionGeographique.getX() != null && positionGeographique.getY() != null)
                        positionGeographique.setProjectionType("LAMBERT I");
                    if (ligneCSV[2] != null)
			if (ligneCSV[2].trim().length() > 0)
                            positionGeographique.setLatitude(new BigDecimal(ligneCSV[2]));
                    if (ligneCSV[3] != null)
			if (ligneCSV[3].trim().length() > 0)
                            positionGeographique.setLongitude(new BigDecimal(ligneCSV[3]));
                    positionGeographique.setLongLatType(LongLatTypeType.WGS84);
                    if (ligneCSV[4] != null)
			if (ligneCSV[4].trim().length() > 0) {
                            positionGeographique.getAreaCentroid().getAddress().setCountryCode(ligneCSV[4].trim());
                        }
                     */
		    if (ligneCSV[5] != null)
			if (ligneCSV[5].trim().length() > 0)
			    positionGeographique.setCountryCode(ligneCSV[5].trim());
		    zones.put(positionGeographique.getName(), positionGeographique);
		}
		else
		    logger.debug("READING COMMERCIAL STOP : "+ligneCSV[6].trim());
		
		if (zonesParLigne.get(ligne) == null)
		    zonesParLigne.put(ligne, new ArrayList<PositionGeographique>());
		if (zonesDeLigne.add(positionGeographique))
		    zonesParLigne.get(ligne).add(positionGeographique);
		if (ligneCSV[7] != null)
		    if (ligneCSV[7].trim().length() > 0) {
			Identification id = new Identification(ligneCSV[7].trim(), ligneCSV[4].trim(), ligneCSV[5].trim());
			for (Identification ident : arretsPhysiques.keySet())
			    if (id.equals(ident)) {
				id = ident;
				break;
			    }
			PositionGeographique arretPhysique = arretsPhysiques.get(id);
			if (arretPhysique == null) {
			    logger.debug("\tCREATING STOP : "+ligneCSV[7].trim());
			    //arretPhysique = PositionGeographique.creerArretPhysique(ligneCSV[7].trim());
			    arretPhysique = new PositionGeographique();
			    arretPhysique.setName(ligneCSV[7].trim());
			    arretPhysique.setAreaType(ChouetteAreaType.BOARDINGPOSITION);

			    if (ligneCSV[0] != null)
                                if (ligneCSV[0].trim().length() > 0)
                                    arretPhysique.setX(new BigDecimal(ligneCSV[0].trim()));
                            if (ligneCSV[1] != null)
                                if (ligneCSV[1].trim().length() > 0)
                                    arretPhysique.setY(new BigDecimal(ligneCSV[1].trim()));
			    if (ligneCSV[2] != null)
				if (ligneCSV[2].trim().length() > 0)
				    arretPhysique.setLatitude(new BigDecimal(ligneCSV[2].trim()));
			    if (ligneCSV[3] != null)
				if (ligneCSV[3].trim().length() > 0)
				    arretPhysique.setLongitude(new BigDecimal(ligneCSV[3].trim()));
                            if (arretPhysique.getLongitude() != null && arretPhysique.getLatitude() != null)
                                arretPhysique.setLongLatType(LongLatTypeType.WGS84);
			    if (ligneCSV[4] != null)
				if (ligneCSV[4].trim().length() > 0)
				    arretPhysique.setStreetName(ligneCSV[4].trim()); // Adresse
			    if (ligneCSV[5] != null)
				if (ligneCSV[5].trim().length() > 0)
				    arretPhysique.setCountryCode(ligneCSV[5].trim()); // Code postal


			    counter++;
			    arretPhysique.setObjectId(identificationManager.getIdFonctionnel("StopArea", id.toSimpleString()));
			    //positionGeographique.getStopArea().addContains(arretPhysique.getObjectId());
			    if (arretsPhysiquesParZoneParente.get(positionGeographique) == null)
				arretsPhysiquesParZoneParente.put(positionGeographique, new HashSet<PositionGeographique>());
			    arretsPhysiquesParZoneParente.get(positionGeographique).add(arretPhysique);
			    if (zoneParenteParObjectId.get(ligne) == null)
				zoneParenteParObjectId.put(ligne, new HashMap<String, String>());
			    zoneParenteParObjectId.get(ligne).put(arretPhysique.getObjectId(), positionGeographique.getObjectId());
			    arretsPhysiques.put(id, arretPhysique);
			    //logger.error("YYYYYY "+ligne.getPublishedName()+"\t:\t"+positionGeographique.getName()+"\t:\t"+arretPhysique.getName());
			    //logger.error("ZZZZZZ "+ligne.getPublishedName()+"\t:\t"+positionGeographique.getObjectId()+"\t:\t"+arretPhysique.getObjectId());
			}
			else
			    logger.debug("\tREADING STOP : "+ligneCSV[7].trim());
			arretsPhysiquesOrdonnes.add(arretPhysique);
		    }
	    }
	arretsPhysiquesParLigne.put(ligne, arretsPhysiquesOrdonnes);
    }
    
    @Override
    public void validerCompletude() {
    }
    
    public IIdentificationManager getIdentificationManager() {
	return identificationManager;
    }
    
    public void setIdentificationManager(IIdentificationManager identificationManager) {
	this.identificationManager = identificationManager;
    }
    
    public int getColonneDesTitres() {
	return colonneDesTitres;
    }
    
    public void setColonneDesTitres(int colonneDesTitres) {
	this.colonneDesTitres = colonneDesTitres;
    }
	
    public void setManagerDataSource(ChouetteDriverManagerDataSource managerDataSource) {
	this.managerDataSource = managerDataSource;
    }
}
