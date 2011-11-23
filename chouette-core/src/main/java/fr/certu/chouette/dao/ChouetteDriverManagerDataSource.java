/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.dao;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class ChouetteDriverManagerDataSource extends DriverManagerDataSource
{
	private String databaseSchema;

	public void setDatabaseSchema(String databaseSchema) 
	{
	   if (databaseSchema.trim().equals(databaseSchema))
		   this.databaseSchema = databaseSchema;
	   else
	      throw new IllegalArgumentException("Database schema must not ends with white spaces");
	}

	public String getDatabaseSchema() 
	{
		return databaseSchema;
	}
	
}
