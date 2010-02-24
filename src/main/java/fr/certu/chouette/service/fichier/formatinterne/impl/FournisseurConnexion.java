package fr.certu.chouette.service.fichier.formatinterne.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import fr.certu.chouette.service.database.ChouetteDriverManagerDataSource;

public class FournisseurConnexion  {
	
	private ChouetteDriverManagerDataSource managerDataSource;
	private Connection              connection;
	
	public void ouvrirConnection() {
		try {
			connection = DriverManager.getConnection(managerDataSource.getUrl(), managerDataSource.getUsername(), managerDataSource.getPassword());
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void valider() {
		try {
			connection.commit();
		} 
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void annuler() {
		try {
			connection.rollback();
		} 
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void fermerConnection() {
		try {
			connection.close();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void setManagerDataSource(ChouetteDriverManagerDataSource managerDataSource) {
		this.managerDataSource = managerDataSource;
	}
}
