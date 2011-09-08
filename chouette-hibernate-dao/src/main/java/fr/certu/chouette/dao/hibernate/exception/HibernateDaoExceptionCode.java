/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.dao.hibernate.exception;

public enum HibernateDaoExceptionCode 
{
	DATABASE_INTEGRITY, 
	INVALID_CONSTRAINT, 
	UNKNOWN_ID, 
	INVALID_DATA, 
	DATABASE_SCHEMA_WRONG_FOREIGN_KEY_TYPE, 
	DATABASE_SCHEMA_MISSING_FOREIGN_KEY, 
	DATABASE_SCHEMA_MISSING_PRIMARY_KEY, 
	DATABASE_SCHEMA_WRONG_PRIMARY_KEY,
	NOT_YET_IMPLEMENTED

}
