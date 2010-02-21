package unit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

public class CopyDumpTestCase
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(CopyDumpTestCase.class);
	
	public CopyDumpTestCase() 
	{
	}

	@Test(groups="tests de faisabilite", description="consultation - mise a jour d'une sequence")
   public void testSequence()
   {
	   try
	   {
		   Class.forName("org.postgresql.Driver");
		   Connection conn = DriverManager.getConnection( "jdbc:postgresql://localhost:5432/chouette", "postgres", "postgres0");
		  
		   Statement stmt = conn.createStatement();
		   conn.setAutoCommit( false);

		   int val = 0;
			ResultSet rs = stmt.executeQuery( "select nextval( 'hibernate_sequence');");
			while ( rs.next()) 
			{
				logger.debug( rs.getObject( 1));
				val = Integer.parseInt( rs.getObject( 1).toString());
			}
			stmt.executeQuery( "select setval( 'hibernate_sequence', "+(val+10)+");");
			rs = stmt.executeQuery( "select nextval( 'hibernate_sequence');");
			while ( rs.next()) 
			{
				logger.debug( rs.getObject( 1));
			}
		   conn.close();
	   }
	   catch( Exception e)
	   {
		   logger.error( e.getMessage(), e);
		   throw new RuntimeException( e);
	   }
   }

	@Test(groups="tests de faisabilite", description="chargement d'une table par copy")
   public void testCopy()
   {

	   try
	   {
		   Class.forName("org.postgresql.Driver");
		   Connection conn = DriverManager.getConnection( "jdbc:postgresql://localhost:5432/chouette", "postgres", "postgres0");
		  
		   Statement stmt = conn.createStatement();
		   conn.setAutoCommit( false);

//		   stmt.executeUpdate(
//	        "delete FROM vehiclejourneyatstop");

		   stmt.executeUpdate(
	        "COPY vehiclejourneyatstop FROM '/home/marc/projets/CHOUETTE/Test/vehiclejourneyatstop.csv'");
		   
		   conn.commit();
	   }
	   catch( Exception e)
	   {
		   logger.error( e.getMessage(), e);
		   throw new RuntimeException( e);
	   }
	}
	

}
