package mobi.chouette.dao;

import java.io.File;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.joda.time.Duration;
import org.testng.Assert;
import org.testng.annotations.Test;

@Log4j
public class InterchangeDAOTest extends Arquillian {
	@EJB
	InterchangeDAO interchangeDao;

	@EJB
	VehicleJourneyDAO vehicleJourneyDao;

	@EJB
	StopPointDAO stopPointDao;

	@Deployment
	public static WebArchive createDeployment() {

		try {
			WebArchive result;
			File[] files = Maven.resolver().loadPomFromFile("pom.xml")
					.resolve("mobi.chouette:mobi.chouette.dao").withTransitivity().asFile();

			result = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
					.addAsLibraries(files).addAsResource(EmptyAsset.INSTANCE, "beans.xml");
			return result;
		} catch (RuntimeException e) {
			System.out.println(e.getClass().getName());
			throw e;
		}

	}
	
	  @Resource
	    private UserTransaction trx;    

	@Test
	public void insertInterchangeWithAllFieldsStringReferences() {
		try {
			ContextHolder.setContext("chouette_gui"); // set tenant schema
			interchangeDao.truncate();

			Interchange interchange = new Interchange();
			interchange.setObjectId("TST:" + Interchange.INTERCHANGE_KEY + ":1");
			interchange.setAdvertised(Boolean.TRUE);
			interchange.setGuaranteed(Boolean.TRUE);
			interchange.setPlanned(Boolean.TRUE);
			interchange.setStaySeated(Boolean.TRUE);
			interchange.setMaximumWaitTime(Duration.standardSeconds(3661));
			interchange.setMinimumTransferTime(Duration.standardSeconds(7322));
			interchange.setName("Test interchange");
			interchange.setPriority(1);

			interchange.setConsumerVehicleJourneyObjectid("TST:" + Interchange.VEHICLEJOURNEY_KEY + ":1");
			interchange.setConsumerStopPointObjectid("TST:" + StopPoint.STOPPOINT_KEY + ":1");
			interchange.setConsumerVisitNumber(2);


			interchange.setFeederVehicleJourneyObjectid("TST:" + Interchange.VEHICLEJOURNEY_KEY + ":2");
			interchange.setFeederStopPointObjectid("TST:" + StopPoint.STOPPOINT_KEY + ":2");
			interchange.setFeederVisitNumber(3);

			interchangeDao.create(interchange);
			
			Interchange find = interchangeDao.find(interchange.getId());

			Assert.assertNotNull(find);
			Assert.assertEquals(find.getObjectId(), interchange.getObjectId());
			Assert.assertEquals(find.getAdvertised(), interchange.getAdvertised());
			Assert.assertEquals(find.getGuaranteed(), interchange.getGuaranteed());
			Assert.assertEquals(find.getPlanned(), interchange.getPlanned());
			Assert.assertEquals(find.getStaySeated(), interchange.getStaySeated());
			Assert.assertEquals(find.getPriority(), interchange.getPriority());
			Assert.assertEquals(find.getMaximumWaitTime(), interchange.getMaximumWaitTime());
			Assert.assertEquals(find.getMinimumTransferTime(), interchange.getMinimumTransferTime());
			Assert.assertEquals(find.getFeederVisitNumber(),interchange.getFeederVisitNumber());
			Assert.assertEquals(find.getConsumerVisitNumber(),interchange.getConsumerVisitNumber());
			Assert.assertEquals(find.getConsumerStopPointObjectid(), interchange.getConsumerStopPointObjectid());
			Assert.assertEquals(find.getConsumerVehicleJourneyObjectid(), interchange.getConsumerVehicleJourneyObjectid());
			Assert.assertEquals(find.getFeederStopPointObjectid(), interchange.getFeederStopPointObjectid());
			Assert.assertEquals(find.getFeederVehicleJourneyObjectid(), interchange.getFeederVehicleJourneyObjectid());
			
			
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

	@Test
	public void insertMinimalInterchangeWithObjectReferences() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		try {
			ContextHolder.setContext("chouette_gui"); // set tenant schema
			
			trx.begin();
			
			
			interchangeDao.truncate();
			vehicleJourneyDao.truncate();
			stopPointDao.truncate();

			Interchange interchange = new Interchange();
			interchange.setObjectId("TST:" + Interchange.INTERCHANGE_KEY + ":1");
			
			StopPoint stp1 = new StopPoint();
			stp1.setObjectId("TST:"+StopPoint.STOPPOINT_KEY+":1");
			stopPointDao.create(stp1);
			
			StopPoint stp2 = new StopPoint();
			stp2.setObjectId("TST:"+StopPoint.STOPPOINT_KEY+":2");
			stopPointDao.create(stp2);
			
			
			VehicleJourney j1 = new VehicleJourney();
			j1.setObjectId("TST:"+VehicleJourney.VEHICLEJOURNEY_KEY+":1");
			vehicleJourneyDao.create(j1);
			
			VehicleJourney j2 = new VehicleJourney();
			j2.setObjectId("TST:"+VehicleJourney.VEHICLEJOURNEY_KEY+":2");
			vehicleJourneyDao.create(j2);
			
			
			interchange.setConsumerVehicleJourney(j1);
			interchange.setConsumerStopPoint(stp1);
			
			interchange.setFeederVehicleJourney(j2);
			interchange.setFeederStopPoint(stp2);

			interchangeDao.create(interchange);
			
			trx.commit();
			
			trx.begin();
			
			Interchange find = interchangeDao.find(interchange.getId());

			Assert.assertNotNull(find);
			
			Assert.assertNotNull(find.getConsumerVehicleJourney());
			Assert.assertNotNull(find.getConsumerStopPoint());
			
			Assert.assertNotNull(find.getFeederVehicleJourney());
			Assert.assertNotNull(find.getFeederStopPoint());
			
			Assert.assertEquals(find.getConsumerStopPointObjectid(), interchange.getConsumerStopPointObjectid());
			Assert.assertEquals(find.getConsumerVehicleJourneyObjectid(), interchange.getConsumerVehicleJourneyObjectid());
			Assert.assertEquals(find.getFeederStopPointObjectid(), interchange.getFeederStopPointObjectid());
			Assert.assertEquals(find.getFeederVehicleJourneyObjectid(), interchange.getFeederVehicleJourneyObjectid());
			
			VehicleJourney findJ1 = vehicleJourneyDao.find(j1.getId());
			Assert.assertEquals(findJ1.getConsumerInterchanges().size(), 1);
			
			VehicleJourney findJ2 = vehicleJourneyDao.find(j2.getId());
			Assert.assertEquals(findJ2.getFeederInterchanges().size(), 1);
			
			StopPoint findStp1 = stopPointDao.find(stp1.getId());
			Assert.assertEquals(findStp1.getConsumerInterchanges().size(), 1);

			StopPoint findStp2 = stopPointDao.find(stp2.getId());
			Assert.assertEquals(findStp2.getFeederInterchanges().size(), 1);

		} catch (RuntimeException ex) {
			Throwable cause = ex.getCause();
			while (cause != null) {
				log.error(cause);
				if (cause instanceof SQLException)
					traceSqlException((SQLException) cause);
				cause = cause.getCause();
			}
			throw ex;
		} finally {
			trx.rollback();
		}
	}

	private void traceSqlException(SQLException ex) {
		while (ex.getNextException() != null) {
			ex = ex.getNextException();
			log.error(ex);
		}
	}

}
