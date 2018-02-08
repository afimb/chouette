package mobi.chouette.dao;

import java.io.File;
import java.sql.SQLException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.RouteSection;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;

@Log4j
public class RouteSectionDaoTest extends Arquillian {
	@EJB
	RouteSectionDAO routeSectionDao;

	@PersistenceContext(unitName = "referential")
	EntityManager em;

	@Inject
	UserTransaction utx;

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
	public void checkLineString() {
		Long id = null;
		{
			
			try {
				ContextHolder.setContext("chouette_gui"); // set tenant schema
				RouteSection section = new RouteSection();
				section.setObjectId("Test:" + RouteSection.ROUTE_SECTION_KEY + ":1");
				GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
				Coordinate[] coordinates = new Coordinate[2];
				coordinates[0] = new Coordinate(2.338767, 48.8612525);
				log.info("C1 = "+coordinates[0].x + " "+coordinates[0].y);
				coordinates[1] = new Coordinate(2.343579, 48.866239);
				LineString inputGeometry = factory.createLineString(coordinates);
				
				section.setInputGeometry(inputGeometry);
				routeSectionDao.create(section);
				id = section.getId();
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
		{
			try {
				utx.begin();
				em.joinTransaction();
				RouteSection r = routeSectionDao.find(id);
				Assert.assertNotNull(r.getId(), "referential id");
				Assert.assertEquals(r.getObjectId(), "Test:" + RouteSection.ROUTE_SECTION_KEY + ":1", "objectid");
				Assert.assertNotNull(r.getInputGeometry(), "geometry");
				LineString l = r.getInputGeometry();
				Assert.assertEquals(l.getCoordinates().length,2,"size of linestring");
				Coordinate c1 = l.getCoordinateN(0);
				Assert.assertTrue(Math.abs(c1.x-2.338767) < 0.00001," value of c1.x ="+c1.x);
				Assert.assertTrue(Math.abs(c1.y-48.8612525) < 0.00001," value of c1.y ="+c1.y);
				Coordinate c2 = l.getCoordinateN(1);
				Assert.assertTrue(Math.abs(c2.x-2.343579) < 0.00001," value of c2.x ="+c2.x);
				Assert.assertTrue(Math.abs(c2.y-48.866239) < 0.00001," value of c2.y ="+c2.y);
				routeSectionDao.delete(r);
				utx.commit();
			} catch (NotSupportedException | SystemException | SecurityException | IllegalStateException
					| RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void traceSqlException(SQLException ex) {
		while (ex.getNextException() != null) {
			ex = ex.getNextException();
			log.error(ex);
		}
	}

}
