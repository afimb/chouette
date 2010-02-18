package fr.certu.chouette.service.fichier.formatinterne.impl;

import fr.certu.chouette.service.fichier.formatinterne.IFournisseurId;
import fr.certu.chouette.service.fichier.formatinterne.IGestionSequence;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class GestionSequence implements IGestionSequence, IFournisseurId {
	
	private Connection connexion;
	private long       valeurSequence = 0L;
	private int        totalIdCrees   = 0;
	private String databaseSchema;
	
	public void actualiser() {
		try {
			Statement stmt = connexion.createStatement();
			stmt.executeQuery("select setval('" + databaseSchema + ".hibernate_sequence', " + (valeurSequence + totalIdCrees ) + ");");
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void initialiser() {
		try {
			Statement stmt = connexion.createStatement();
			ResultSet rs = stmt.executeQuery("select nextval('" + databaseSchema + ".hibernate_sequence');");
			while (rs.next())
				valeurSequence = Long.parseLong(rs.getObject(1).toString());
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public long getNouvelId(String objectId1, String objectId2) {
		return _getNouvelId();
	}
	
	public long getNouvelId(String objectId) {
		return _getNouvelId();
	}
	
	private long _getNouvelId() {
		totalIdCrees++;
		return (valeurSequence + totalIdCrees);
	}
	
	public void setConnexion(Connection connexion) {
		this.connexion = connexion;
	}

	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}

	public String getDatabaseSchema() {
		return databaseSchema;
	}
}
