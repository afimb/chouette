package fr.certu.chouette.service.exportateur.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import fr.certu.chouette.service.database.ChouetteDriverManagerDataSource;
import fr.certu.chouette.service.exportateur.IExportCorrespondances;

public class ExportCorrespondances implements IExportCorrespondances {
	
	private ChouetteDriverManagerDataSource managerDataSource;
	
	public List<String[]> exporter() {
		Connection     connexion = null;
		List<String[]> data      = null;
		try {
			Properties props = new Properties();
			props.setProperty("user",managerDataSource.getUsername());
			props.setProperty("password",managerDataSource.getPassword());
			props.setProperty("allowEncodingChanges","true");
			connexion = DriverManager.getConnection(managerDataSource.getUrl(), props);
			connexion.setAutoCommit(false);
			int longueur = 12;
			Statement statement = connexion.createStatement();
			String selectStatement = "SELECT (SELECT registrationnumber FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE id=departureId), (SELECT registrationnumber FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE id=arrivalId), " +
			"\"name\", linkdistance, linktype, defaultduration, frequenttravellerduration, occasionaltravellerduration, mobilityrestrictedtravellerduration, " +
			"mobilityrestrictedsuitability, stairsavailability, liftavailability FROM " + managerDataSource.getDatabaseSchema() + ".connectionlink;";
			ResultSet rs = statement.executeQuery(selectStatement);
			data = new ArrayList<String[]>();
			while (rs.next()) {
				String[] line = new String[longueur];
				for (int i = 0; i < longueur; i++)
					if (rs.getObject(i+1) != null)
						line[i] = rs.getObject(i+1).toString();
				data.add(line);
			}
		}
		catch(Exception e) {
			try {
				if (connexion != null)
					connexion.rollback();
			}
			catch(Exception ex) {
			}
			throw new RuntimeException(e);
		}
		finally {
			try {
				if (connexion != null)
					connexion.close();
			}
			catch(Exception e) {
			}
		}
		return data;
	}
	
	public void setManagerDataSource(ChouetteDriverManagerDataSource managerDataSource) {
		this.managerDataSource = managerDataSource;
	}
}
