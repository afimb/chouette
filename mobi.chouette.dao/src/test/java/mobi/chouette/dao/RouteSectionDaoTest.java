package mobi.chouette.dao;

import java.io.File;
import java.sql.SQLException;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.RouteSection;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.annotations.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;

@Log4j
public class RouteSectionDaoTest extends Arquillian
{
	@EJB
	RouteSectionDAO routeSectionDao;


	@Deployment
	public static WebArchive createDeployment() {

		try
		{
		WebArchive result;
		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.dao").withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
				.addAsLibraries(files).addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;
		}
		catch (RuntimeException e)
		{
			System.out.println(e.getClass().getName());
			throw e;
		}

	}
	
	@Test
	public void checkLineString()
	{
		try
		{
		ContextHolder.setContext("chouette_gui"); // set tenant schema
		RouteSection section = new RouteSection();
		section.setObjectId("Test:"+RouteSection.ROUTE_SECTION_KEY+":1");
		GeometryFactory factory =  new GeometryFactory(new PrecisionModel(10),4326);
		Coordinate[] coordinates = new Coordinate[2];
		coordinates[0] = new Coordinate(2.338767,48.8612525);
		coordinates[1] = new Coordinate(2.343579,48.866239);
		LineString inputGeometry = factory.createLineString(coordinates );
		section.setInputGeometry(inputGeometry );
		routeSectionDao.create(section);
		}
		catch (RuntimeException ex)
		{
			 Throwable cause = ex.getCause();
			while (cause != null)
			{
				log.error(cause);
				if (cause instanceof SQLException) traceSqlException((SQLException)cause);
				cause = cause.getCause();
			}
			throw ex;
		}
	}
	
	private void traceSqlException(SQLException ex)
	{
		while (ex.getNextException() != null)
		{
			ex = ex.getNextException();
			log.error(ex);
		}
	}
	

}
