package fr.certu.chouette.service.exportateur.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import fr.certu.chouette.service.exportateur.IExportCorrespondances;

public class ExportCorrespondances implements IExportCorrespondances {
	
	private DriverManagerDataSource managerDataSource;
	
	public List<String[]> exporter() {
		Connection     connexion = null;
		List<String[]> data      = null;
		try {
			Class.forName(managerDataSource.getDriverClassName());
			Properties props = new Properties();
			props.setProperty("user",managerDataSource.getUsername());
			props.setProperty("password",managerDataSource.getPassword());
			props.setProperty("allowEncodingChanges","true");
			connexion = DriverManager.getConnection(managerDataSource.getUrl(), props);
			connexion.setAutoCommit(false);
			int longueur = 12;
			Statement statement = connexion.createStatement();
			String selectStatement = "SELECT (SELECT registrationnumber FROM stoparea WHERE id=iddepart), (SELECT registrationnumber FROM stoparea WHERE id=idarrivee), " +
			"\"name\", linkdistance, linktype, defaultduration, frequenttravellerduration, occasionaltravellerduration, mobilityrestrictedtravellerduration, " +
			"mobilityrestrictedsuitability, stairsavailability, liftavailability FROM connectionlink;";
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
	
	public void setManagerDataSource(DriverManagerDataSource managerDataSource) {
		this.managerDataSource = managerDataSource;
	}
}
