package fr.certu.chouette.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.dialect.PostgreSQLDialect;
//import org.hibernatespatial.postgis.PostgisDialect;

public class ExtendedPostgresSQLDialect extends /*PostgisDialect*/ PostgreSQLDialect {
	
	private static final Log logger	= LogFactory.getLog(ExtendedPostgresSQLDialect.class);
	
	// CONSTRUCTEUR APPELE LORS DE L'INITIALISATION
	// DE LA FACTORY DES SESSIONS HIBERNATE
	// (cf. applicationContext.xml DANS LA CAS OU SPRING EST ENCORE UTILISE)
	public ExtendedPostgresSQLDialect() {
		super();
		logger.info("ENREGISTREMENT DES NOUVELLES FONCTIONS APPORTEES AU DIALECT POSTGRESQL .. ");
		registerFunction("bitwise_and", new BitwiseAndSQLFunction("?1 & ?2", Hibernate.INTEGER));
		
	}
}
