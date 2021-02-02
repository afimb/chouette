package mobi.chouette.dao;

import mobi.chouette.dao.exception.ChouetteStatisticsTimeoutException;
import mobi.chouette.model.DatedServiceJourney;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.statistics.LineAndTimetable;
import mobi.chouette.persistence.hibernate.ContextHolder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ejb.EJB;
import javax.transaction.Transactional;
import java.io.File;
import java.util.Collection;
import java.util.UUID;

public class TimetableDaoTest extends Arquillian {
	@EJB
	TimetableDAO timetableDao;

	@EJB
	LineDAO lineDao;

	@EJB
	RouteDAO routeDao;
	
	@EJB
	VehicleJourneyDAO vjDao;

	@EJB
	DatedServiceJourneyDAO datedServiceJourneyDAO;

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

	@Test(groups = "Statistics")
	@Transactional
	public void getAllTimetableForAllLines() throws ChouetteStatisticsTimeoutException {
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
		vjDao.create(vj);
		
		
		Collection<LineAndTimetable> allTimetableForAllLines = timetableDao.getAllTimetableForAllLines();

		Assert.assertNotNull(allTimetableForAllLines);
		Assert.assertEquals(1, allTimetableForAllLines.size());
		LineAndTimetable lat = allTimetableForAllLines.iterator().next();

		Assert.assertNotNull(lat.getLineId());
		Assert.assertEquals(1, lat.getTimetables().size());

	}

	@Test(groups = "Statistics")
	@Transactional
	public void getAllTimetableForAllLinesWithDSJ() throws ChouetteStatisticsTimeoutException {
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		// Cleanup||
		datedServiceJourneyDAO.truncate();
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

		DatedServiceJourney dsj = new DatedServiceJourney();
		dsj.setObjectId("TST:DatedServiceJourney:"+uuid);
		dsj.setOperatingDay(new LocalDate());


		// Wire together

		r.setLine(l);
		vj.setRoute(r);
		dsj.setVehicleJourney(vj);

		lineDao.create(l);
		vjDao.create(vj);

		Collection<LineAndTimetable> allTimetableForAllLines = timetableDao.getAllTimetableForAllLines();

		Assert.assertNotNull(allTimetableForAllLines);
		Assert.assertEquals(allTimetableForAllLines.size(),1 );
		LineAndTimetable lat = allTimetableForAllLines.iterator().next();

		Assert.assertNotNull(lat.getLineId());
		Assert.assertEquals(lat.getTimetables().size(),1);


	}

}
