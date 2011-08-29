package fr.certu.chouette.service.geographie.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import fr.certu.chouette.service.database.ChouetteDriverManagerDataSource;
import fr.certu.chouette.service.geographie.ICoordonnees;

public class Coordonnees implements ICoordonnees {

    private static final Logger logger = Logger.getLogger(Coordonnees.class);
    private ChouetteDriverManagerDataSource managerDataSource;

    @Override
    public void calculBarycentre() {
        Connection connexion = null;
        Map<Long, List<Double>> longitudes = new HashMap<Long, List<Double>>();
        Map<Long, List<Double>> latitudes = new HashMap<Long, List<Double>>();
        try {
            Properties props = new Properties();
            props.setProperty("user", managerDataSource.getUsername());
            props.setProperty("password", managerDataSource.getPassword());
            props.setProperty("allowEncodingChanges", "true");
            connexion = DriverManager.getConnection(managerDataSource.getUrl(), props);
            connexion.setAutoCommit(false);

            String selection = "SELECT id, parentId, longitude, latitude FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE (areatype = 'BoardingPosition') OR (areatype = 'Quay');";
            Statement sqlStatement = connexion.createStatement();
            ResultSet rs = sqlStatement.executeQuery(selection);
            int rsCount = 0;
            while (rs.next()) {
                String st1 = "";
                if (rs.getObject(1) != null) {
                    st1 = rs.getObject(1).toString();
                }
                String st2 = "";
                if (rs.getObject(2) != null) {
                    st2 = rs.getObject(2).toString();
                } else {
                    continue;
                }
                String st3 = "";
                if (rs.getObject(3) != null) {
                    st3 = rs.getObject(3).toString();
                }
                String st4 = "";
                if (rs.getObject(4) != null) {
                    st4 = rs.getObject(4).toString();
                }
                if ((st3.length() == 0) && (st4.length() == 0)) {
                    continue;
                }
                rsCount++;
                Long idParent = new Long(st2);
                for (Long key : longitudes.keySet()) {
                    if (key.longValue() == idParent.longValue()) {
                        idParent = key;
                        break;
                    }
                }
                if (longitudes.get(idParent) == null) {
                    longitudes.put(idParent, new ArrayList<Double>());
                    latitudes.put(idParent, new ArrayList<Double>());
                }
                longitudes.get(idParent).add(new Double(st3));
                latitudes.get(idParent).add(new Double(st4));
            }
            int bigNumber = 0;
            for (Long key : longitudes.keySet()) {
                double longitude = (double) 0.0;
                for (Double _longitude : longitudes.get(key)) {
                    longitude += _longitude.doubleValue();
                }
                longitude = longitude / ((double) longitudes.get(key).size());
                double latitude = (double) 0.0;
                for (Double _latitude : latitudes.get(key)) {
                    latitude += _latitude.doubleValue();
                }
                latitude = latitude / ((double) latitudes.get(key).size());
                String update = "UPDATE " + managerDataSource.getDatabaseSchema() + ".stoparea SET longitude = '" + longitude + "', latitude = '" + latitude + "', longlattype= 'WGS84' WHERE id ='" + key.longValue() + "';";
                Statement updateStatement = connexion.createStatement();
                int number = updateStatement.executeUpdate(update);
                bigNumber += number;
            }

            longitudes.clear();
            latitudes.clear();
            String selection2 = "SELECT id, parentId, longitude, latitude FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE areatype = 'CommercialStopPoint';";
            Statement sqlStatement2 = connexion.createStatement();
            ResultSet rs2 = sqlStatement2.executeQuery(selection2);
            int rsCount2 = 0;
            while (rs2.next()) {
                String st1 = "";
                if (rs2.getObject(1) != null) {
                    st1 = rs2.getObject(1).toString();
                }
                String st2 = "";
                if (rs2.getObject(2) != null) {
                    st2 = rs2.getObject(2).toString();
                } else {
                    continue;
                }
                String st3 = "";
                if (rs2.getObject(3) != null) {
                    st3 = rs2.getObject(3).toString();
                }
                String st4 = "";
                if (rs2.getObject(4) != null) {
                    st4 = rs2.getObject(4).toString();
                }
                if ((st3.length() == 0) && (st4.length() == 0)) {
                    continue;
                }
                rsCount2++;
                Long idParent = new Long(st2);
                for (Long key : longitudes.keySet()) {
                    if (key.longValue() == idParent.longValue()) {
                        idParent = key;
                        break;
                    }
                }
                if (longitudes.get(idParent) == null) {
                    longitudes.put(idParent, new ArrayList<Double>());
                    latitudes.put(idParent, new ArrayList<Double>());
                }
                longitudes.get(idParent).add(new Double(st3));
                latitudes.get(idParent).add(new Double(st4));
            }
            int bigNumber2 = 0;
            for (Long key : longitudes.keySet()) {
                double longitude = (double) 0.0;
                for (Double _longitude : longitudes.get(key)) {
                    longitude += _longitude.doubleValue();
                }
                longitude = longitude / ((double) longitudes.get(key).size());
                double latitude = (double) 0.0;
                for (Double _latitude : latitudes.get(key)) {
                    latitude += _latitude.doubleValue();
                }
                latitude = latitude / ((double) latitudes.get(key).size());
                String update = "UPDATE " + managerDataSource.getDatabaseSchema() + ".stoparea SET longitude = '" + longitude + "', latitude = '" + latitude + "', longlattype= 'WGS84' WHERE id ='" + key.longValue() + "';";
                Statement updateStatement = connexion.createStatement();
                int number = updateStatement.executeUpdate(update);
                bigNumber2 += number;
            }
            
            connexion.commit();
        } catch (Exception e) {
            try {
                logger.debug("Annuler calcul du barycentre :" + e.getMessage(), e);
                if (connexion != null) {
                    connexion.rollback();
                }
            } catch (Exception ex) {
                logger.error("Echec de la tentative de rollback de la transaction de calcul du barycentre " + ex.getMessage(), ex);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (connexion != null) {
                    connexion.close();
                }
            } catch (Exception e) {
                logger.error("Echec de la tentative de fermeture de la connexion pour calcul du barycentre " + e.getMessage(), e);
            }
        }
    }

    public void setManagerDataSource(ChouetteDriverManagerDataSource managerDataSource) {
        this.managerDataSource = managerDataSource;
    }
}
