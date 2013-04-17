package fr.certu.chouette.dao.init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import fr.certu.chouette.dao.ChouetteDriverManagerDataSource;

@ContextConfiguration(locations={"classpath:initContext.xml"})

public class InitHibernateTests extends AbstractTestNGSpringContextTests
{

	private static final Logger logger = Logger.getLogger(InitHibernateTests.class);
	private static final String request = "select count(*) from information_schema.schemata where schema_name = ?";

	@Test (groups = {"before hibernate"}, description = "create schema" )
	public void createSchema() throws Exception 
	{
		ChouetteDriverManagerDataSource dataSource = (fr.certu.chouette.dao.ChouetteDriverManagerDataSource) applicationContext.getBean("chouetteDataSource");
		String schema = dataSource.getDatabaseSchema().toLowerCase().trim();
		Connection connection = dataSource.getConnection();
		PreparedStatement stmt = connection.prepareStatement(request);
		stmt.setString(1, schema);
		ResultSet rst = stmt.executeQuery();
		if (rst.next())
		{
			int count = rst.getInt(1);
			if (count == 0)
			{
				// create schema
				logger.info("creating schema "+schema);
				Statement stcr = connection.createStatement();
				stcr.execute("CREATE SCHEMA "+schema);
				stcr.close();
			}
			else
			{
				logger.info("schema "+schema+ " already exists");
			}
		}
		rst.close();
		stmt.close();
		connection.close();

	}

}
