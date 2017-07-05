package mobi.chouette.dao;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.DestinationDisplay;
import mobi.chouette.persistence.hibernate.ContextHolder;

@Log4j
public class DestinationDisplayDaoTest extends Arquillian {
	@EJB 
	DestinationDisplayDAO destinationDisplayDAO;

	@Deployment
	public static WebArchive createDeployment() {

		try {
			WebArchive result;
			File[] files = Maven.resolver().loadPomFromFile("pom.xml").resolve("mobi.chouette:mobi.chouette.dao")
					.withTransitivity().asFile();

			result = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
					.addAsLibraries(files).addAsResource(EmptyAsset.INSTANCE, "beans.xml");
			return result;
		} catch (RuntimeException e) {
			System.out.println(e.getClass().getName());
			throw e;
		}

	}


	
	@Test
	public void checkDestinationDisplay() {
		try {
			ContextHolder.setContext("chouette_gui"); // set tenant schema
			
			destinationDisplayDAO.deleteAll();
			
			DestinationDisplay parent = new DestinationDisplay();
			
			parent.setName("Parent");
			parent.setFrontText("FrontText");
			parent.setSideText("SideText");
			parent.setObjectId("X:DestinationDisplay:1");
			
			DestinationDisplay child = new DestinationDisplay();
			child.setName("Child");
			child.setFrontText("FrontText");
			child.setSideText("SideText");
			child.setObjectId("X:DestinationDisplay:2");
			
			parent.getVias().add(child);

			DestinationDisplay child2 = new DestinationDisplay();
			child2.setName("Child2");
			child2.setFrontText("FrontText");
			child2.setSideText("SideText");
			child2.setObjectId("X:DestinationDisplay:3");
			
			parent.getVias().add(child2);

			//destinationDisplayDAO.create(child);
			destinationDisplayDAO.create(parent);
			
			
			List<DestinationDisplay> findAll = destinationDisplayDAO.findAll();
			Assert.assertEquals(findAll.size(),3);
			for(DestinationDisplay d : findAll) {
				Assert.assertNotNull(d.getName());
				Assert.assertNotNull(d.getFrontText());
				Assert.assertNotNull(d.getSideText());
			
				if(d.getObjectId().equals(parent.getObjectId())) {
					Assert.assertEquals(2, d.getVias().size());
					Assert.assertEquals(d.getVias().get(1).getName(), "Child2");
				}
			}
			
			
			
			
			
		} catch (RuntimeException ex) {
			Throwable cause = ex.getCause();
			while (cause != null) {
				log.error(cause);
				if (cause instanceof SQLException)
					traceSqlException((SQLException) cause);
				cause = cause.getCause();
			}
			throw ex;
		}
	}
	

	private void traceSqlException(SQLException ex) {
		while (ex.getNextException() != null) {
			ex = ex.getNextException();
			log.error(ex);
		}
	}

}
