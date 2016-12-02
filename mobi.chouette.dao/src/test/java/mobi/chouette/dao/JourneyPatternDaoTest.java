package mobi.chouette.dao;

import java.io.File;
import java.sql.SQLException;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ChouetteId;
import mobi.chouette.model.JourneyPattern;
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
public class JourneyPatternDaoTest extends Arquillian {
	@EJB 
	JourneyPatternDAO journeyPatternDao;

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
	public void checkJourneyPatternSections() {
		try {
			ContextHolder.setContext("chouette_gui"); // set tenant schema
			RouteSection section = new RouteSection();
//			section.getChouetteId().setObjectId("Test:" + RouteSection.ROUTE_SECTION_KEY + ":1");
			ChouetteId chouetteId = new ChouetteId("test", "1", false);
			section.setChouetteId(chouetteId);
			GeometryFactory factory = new GeometryFactory(new PrecisionModel(10), 4326);
			Coordinate[] coordinates = new Coordinate[2];
			coordinates[0] = new Coordinate(2.338767,48.8612525);
			coordinates[1] = new Coordinate(2.343579,48.866239);
			LineString inputGeometry = factory.createLineString(coordinates);
			section.setInputGeometry(inputGeometry);
			// routeSectionDao.create(section);

			JourneyPattern jp = new JourneyPattern();
//			jp.getChouetteId().setObjectId("Test:" + JourneyPattern.JOURNEYPATTERN_KEY + ":1");
			ChouetteId chouetteId2 = new ChouetteId("test", "1", false);
			jp.setChouetteId(chouetteId2);
			jp.getRouteSections().add(section);
			journeyPatternDao.create(jp);
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
