package fr.certu.chouette.service.database;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class ChouetteDriverManagerDataSource extends DriverManagerDataSource
{
	private String databaseSchema;

	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}

	public String getDatabaseSchema() {
		return databaseSchema;
	}
	
}
