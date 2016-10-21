package mobi.chouette.dao;

import java.io.File;
import java.util.Collection;
import java.util.UUID;

import javax.ejb.EJB;
import javax.transaction.Transactional;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.statistics.LineAndTimetable;
import mobi.chouette.persistence.hibernate.ContextHolder;

public class TimetableDaoTest extends Arquillian {
	@EJB
	TimetableDAO timetableDao;

	@EJB
	LineDAO lineDao;

	@EJB
	RouteDAO routeDao;
	
	@EJB
	VehicleJourneyDAO vjDao;



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

	// TODO create test data in common method

	@Test
	public void getTimetableForLineTest() {
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Line l = new Line();
		l.setId(1L);

		Collection<Timetable> timetableForLine = timetableDao.getTimetableForLine(l);
		Assert.assertNotNull(timetableForLine);
	}

	@Test
	@Transactional
	public void getAllTimetableForAllLines() {
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		// Cleanup
		lineDao.truncate();
		vjDao.truncate();
		timetableDao.truncate();
		routeDao.truncate();
		
		String uuid = UUID.randomUUID().toString();
		
		Line l = new Line();
		l.setObjectId("TST:Line:"+uuid);

		Route r = new Route();
		r.setObjectId("TST:Route:"+uuid);

		VehicleJourney vj = new VehicleJourney();
		vj.setObjectId("TST:VehicleJourney:"+uuid);

		Timetable t = new Timetable();
		t.setObjectId("TST:Timetable:"+uuid);
		

		// Wire together

		r.setLine(l);
		vj.setRoute(r);
		t.addVehicleJourney(vj);
		
		lineDao.create(l);
		//timetableDao.create(t);
		vjDao.create(vj);
//		timetableDao.update(t);
		
		
		Collection<LineAndTimetable> allTimetableForAllLines = timetableDao.getAllTimetableForAllLines();

		Assert.assertNotNull(allTimetableForAllLines);
		Assert.assertEquals(1, allTimetableForAllLines.size());
		LineAndTimetable lat = allTimetableForAllLines.iterator().next();

		Assert.assertNotNull(lat.getLineId());
		Assert.assertEquals(1, lat.getTimetables().size());

	}

}
