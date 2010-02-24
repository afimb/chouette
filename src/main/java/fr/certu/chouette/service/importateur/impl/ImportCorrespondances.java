package fr.certu.chouette.service.importateur.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import fr.certu.chouette.service.database.ChouetteDriverManagerDataSource;
import chouette.schema.types.ConnectionLinkTypeType;
import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.service.database.ICorrespondanceManager;
import fr.certu.chouette.service.importateur.IImportCorrespondances;

public class ImportCorrespondances implements IImportCorrespondances {
	
	private ChouetteDriverManagerDataSource managerDataSource;
	private ICorrespondanceManager  correspondanceManager;

	private SimpleDateFormat  sdf = new SimpleDateFormat("HH:mm:ss");
	
	public List<String> lire(String canonicalPath) {
		Connection     connexion   = null;
		List<String>   messages    = new ArrayList<String>();
		List<String[]> donneesLues = null;
		try {
			Properties props = new Properties();
			props.setProperty("user",managerDataSource.getUsername());
			props.setProperty("password",managerDataSource.getPassword());
			props.setProperty("allowEncodingChanges","true");
			connexion = DriverManager.getConnection(managerDataSource.getUrl(), props);
			connexion.setAutoCommit(false);
			CSVReader csvReader = new CSVReader(new FileReader(canonicalPath), ';');
			List<String[]> _donneesLues = (List<String[]>)csvReader.readAll();
			if (_donneesLues == null)
				return null;
			for (String[] ligne : _donneesLues) {
				if (ligne == null)
					continue;
				if (ligne.length == 0)
					continue;
				if ((ligne[0] == null) || (ligne[1] == null) || (ligne[0].trim().length() == 0) || (ligne[1].trim().length() == 0)) {
					messages.add("Toute correspondance doit connecter deux \"stoparea\".");
					continue;
				}
				String firstIdStatement = "SELECT id FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE registrationnumber=\'"+ligne[0]+"\'";
				Statement firstStatement = connexion.createStatement();
				ResultSet firstResultSet = firstStatement.executeQuery(firstIdStatement);
				String firstId = null;
				while (firstResultSet.next())
					firstId = firstResultSet.getObject(1).toString();
				String secondIdStatement = "SELECT id FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE registrationnumber=\'"+ligne[1]+"\'";
				Statement secondStatement = connexion.createStatement();
				ResultSet secondResultSet = secondStatement.executeQuery(secondIdStatement);
				String secondId = null;
				while (secondResultSet.next())
					secondId = secondResultSet.getObject(1).toString();
				if (firstId == null) {
					messages.add("Il n'y a pas de \"stoparea\" avec le nom : \""+ligne[0]+"\"");
					continue;
				}
				if (secondId == null) {
					messages.add("Il n'y a pas de \"stoparea\" avec le nom : \""+ligne[1]+"\"");
					continue;
				}
				// Ecrire dans donneesLues les données
				String[] newLigne = new String[ligne.length];
				newLigne[0] = firstId;
				newLigne[1] = secondId;
				for (int i = 2; i < ligne.length; i++)
					newLigne[i] = ligne[i];
				if (donneesLues == null)
					donneesLues = new ArrayList<String[]>();
				donneesLues.add(newLigne);
			}
			if (donneesLues != null)
				for (String[] ligne : donneesLues) {
					Correspondance correspondance = new Correspondance();
					correspondance.setIdDepartArrivee(new Long(ligne[0]), new Long(ligne[1]));
					if ((ligne[2] == null) || (ligne[2].trim().length() == 0))
						messages.add("Le nom d'une correspondance ne doit pas etre null.");
					else
						correspondance.setName(ligne[2]);
					try {
						correspondance.setLinkDistance(new BigDecimal(ligne[3]));
					}
					catch(NumberFormatException e) {
						messages.add("La distance d'une correspondance doit être donnée en nombre entier positive.");
					}
					correspondance.setLinkType(ConnectionLinkTypeType.UNDERGROUND);
					if (ligne[4] != null)
						if (ligne[4].trim().toLowerCase().equals("underground"))
							correspondance.setLinkType(ConnectionLinkTypeType.UNDERGROUND);
						else if (ligne[4].trim().toLowerCase().equals("overground"))
							correspondance.setLinkType(ConnectionLinkTypeType.OVERGROUND);
						else if (ligne[4].trim().toLowerCase().equals("mixed"))
							correspondance.setLinkType(ConnectionLinkTypeType.MIXED);
						else
							messages.add("Type de correspondance inconu : "+ligne[4]);
					try {
						correspondance.setDefaultDuration(sdf.parse(ligne[5]));
					}
					catch(ParseException e) {
						messages.add("La \"DefaultDuration\" doit être au format \"hh:mm:ss\" et non pas \""+ligne[5]+"\"");
					}
					try {
						correspondance.setFrequentTravellerDuration(sdf.parse(ligne[6]));
					}
					catch(ParseException e) {
						messages.add("La \"FrequentTravellerDuration\" doit être au format \"hh:mm:ss\" et non pas \""+ligne[6]+"\"");
					}
					try {
						correspondance.setOccasionalTravellerDuration(sdf.parse(ligne[7]));
					}
					catch(ParseException e) {
						messages.add("La \"OccasionalTravellerDuration\" doit être au format \"hh:mm:ss\" et non pas \""+ligne[7]+"\"");
					}
					try {
						correspondance.setMobilityRestrictedTravellerDuration(sdf.parse(ligne[8]));
					}
					catch(ParseException e) {
						messages.add("La \"MobilityRestrictedTravellerDuration\" doit être au format \"hh:mm:ss\" et non pas \""+ligne[8]+"\"");
					}
					correspondance.setMobilityRestrictedSuitability(false);
					if (ligne[9] != null)
						if ((ligne[9].trim().toLowerCase().equals("true")) || (ligne[9].trim().toLowerCase().equals("t")))
							correspondance.setMobilityRestrictedSuitability(true);
						else if (!((ligne[9].trim().toLowerCase().equals("false")) || (ligne[9].trim().toLowerCase().equals("f"))))
							messages.add("La \"MobilityRestrictedSuitability\" doit être \"t\" ou \"true\" ou \"f\" ou \"false\" et non pas \""+ligne[9]+"\"");
					correspondance.setStairsAvailability(false);
					if (ligne[10] != null)
						if ((ligne[10].trim().toLowerCase().equals("true")) || (ligne[10].trim().toLowerCase().equals("t")))
							correspondance.setStairsAvailability(true);
						else if (!((ligne[10].trim().toLowerCase().equals("false")) || (ligne[10].trim().toLowerCase().equals("f"))))
							messages.add("La \"StairsAvailability\" doit être \"t\" ou \"true\" ou \"f\" ou \"false\" et non pas \""+ligne[10]+"\"");
					correspondance.setLiftAvailability(false);
					if (ligne[11] != null)
						if ((ligne[11].trim().toLowerCase().equals("true")) || (ligne[11].trim().toLowerCase().equals("t")))
							correspondance.setLiftAvailability(true);
						else if (!((ligne[11].trim().toLowerCase().equals("false")) || (ligne[11].trim().toLowerCase().equals("f"))))
							messages.add("La \"LiftAvailability\" doit être \"t\" ou \"true\" ou \"f\" ou \"false\" et non pas \""+ligne[11]+"\"");
					correspondanceManager.creer(correspondance);
				}
		}
		catch (FileNotFoundException e) {
			messages.add("Fichier \""+canonicalPath+"\" non trouvé.");
		}
		catch (IOException e) {
			messages.add("IOException : "+e.getMessage());
		}
		catch (SQLException e) {
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
		return messages;
	}
	
	public void setManagerDataSource(ChouetteDriverManagerDataSource managerDataSource) {
		this.managerDataSource = managerDataSource;
	}
	
	public void setCorrespondanceManager(ICorrespondanceManager correspondanceManager) {
		this.correspondanceManager = correspondanceManager;
	}
}
