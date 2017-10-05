package mobi.chouette.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import mobi.chouette.dao.ScheduledStopPointDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.SimpleObjectReference;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.StopAreaTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.TransportSubModeNameEnum;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StopAreaServiceTest extends Arquillian {


	@EJB
	StopAreaService stopAreaService;

	@EJB
	StopAreaDAO stopAreaDAO;

	@EJB
	StopPointDAO stopPointDAO;

	@EJB
	ScheduledStopPointDAO scheduledStopPointDAO;

	@PersistenceContext(unitName = "public")
	private EntityManager em;

	@Inject
	UserTransaction utx;

	@Deployment
	public static EnterpriseArchive createDeployment() {


		EnterpriseArchive result;
		File[] files = Maven.resolver().loadPomFromFile("pom.xml").resolve("mobi.chouette:mobi.chouette.service")
				.withTransitivity().asFile();
		List<File> jars = new ArrayList<>();
		List<JavaArchive> modules = new ArrayList<>();
		for (File file : files) {
			if (file.getName().startsWith("mobi.chouette.exchange")
					|| file.getName().startsWith("mobi.chouette.service")
					|| file.getName().startsWith("mobi.chouette.dao")) {
				String name = file.getName().split("\\-")[0] + ".jar";
				JavaArchive archive = ShrinkWrap.create(ZipImporter.class, name).importFrom(file).as(JavaArchive.class);
				modules.add(archive);
			} else {
				jars.add(file);
			}
		}
		File[] filesDao = Maven.resolver().loadPomFromFile("pom.xml").resolve("mobi.chouette:mobi.chouette.dao")
				.withTransitivity().asFile();
		if (filesDao.length == 0) {
			throw new NullPointerException("no dao");
		}
		for (File file : filesDao) {
			if (file.getName().startsWith("mobi.chouette.dao")) {
				String name = file.getName().split("\\-")[0] + ".jar";

				JavaArchive archive = ShrinkWrap.create(ZipImporter.class, name).importFrom(file).as(JavaArchive.class);
				modules.add(archive);
				if (!modules.contains(archive))
					modules.add(archive);
			} else {
				if (!jars.contains(file))
					jars.add(file);
			}
		}
		final WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war")
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
				.addAsWebInfResource("postgres-ds.xml").addClass(DummyChecker.class)
				.addClass(StopAreaServiceTest.class);

		result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear").addAsLibraries(jars.toArray(new File[0]))
				.addAsModules(modules.toArray(new JavaArchive[0])).addAsModule(testWar)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;
	}


	@Test
	public void testUpdateQuaysOnChildStop() throws Exception {
		utx.begin();
		em.joinTransaction();

		StopArea alreadyExistingParent= new StopArea();
		alreadyExistingParent.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
		alreadyExistingParent.setObjectId( "NSR:StopPlace:58291");
		stopAreaDAO.create(alreadyExistingParent);


		StopArea alreadyExistingChild = new StopArea();
		alreadyExistingChild.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
		alreadyExistingChild.setObjectId( "NSR:StopPlace:62034");

		alreadyExistingChild.setParent(alreadyExistingParent);
		stopAreaDAO.create(alreadyExistingChild);

		utx.commit();
		utx.begin();
		em.joinTransaction();


		stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(new FileInputStream("src/test/data/StopAreasMergedQuaysInChildStop.xml"));

		utx.commit();
		utx.begin();
		em.joinTransaction();

		assertStopPlace(alreadyExistingChild.getObjectId(), "NSR:Quay:104061", "NSR:Quay:8128");

	}


	@Test
	public void testStopAreaUpdate() throws Exception {
		utx.begin();
		em.joinTransaction();

		stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(new FileInputStream("src/test/data/StopAreasInitialSynch.xml"));

		Assert.assertTrue(StringUtils.isEmpty(stopAreaDAO.findByObjectId("NSR:Quay:7").getName()));

		assertStopPlace("NSR:StopPlace:1", "NSR:Quay:1a", "NSR:Quay:1b");
		assertStopPlace("NSR:StopPlace:2", "NSR:Quay:2a");
		assertStopPlace("NSR:StopPlace:3", "NSR:Quay:3a");

		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:StopPlace:4"), "Did not expect to find inactive stop place");
		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:StopPlace:4a"), "Did not expect to find quay for inactive stop place");
		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:StopPlace:4b"), "Did not expect to find quay for inactive stop place");

		utx.commit();

		utx.begin();
		em.joinTransaction();

		// Update stop places
		stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(new FileInputStream("src/test/data/StopAreasUpdate.xml"));

		Assert.assertFalse(StringUtils.isEmpty(stopAreaDAO.findByObjectId("NSR:Quay:7").getName()), "Expected quay name to be updated");

		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:StopPlace:1"), "Did not expect to find deactivated stop place");
		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:1a"), "Did not expect to find quay for deactivated stop place");
		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:1b"), "Did not expect to find quay for deactivated stop place");

		// New quay, removed quay and moved quay for 2
		assertStopPlace("NSR:StopPlace:2", "NSR:Quay:3a", "NSR:Quay:2b");
		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:2a"), "Did not expect to find removed quay");
		assertStopPlace("NSR:StopPlace:3");

		ContextHolder.setContext("chouette_gui");
		cleanStopPoints();
		// Create stop point contained in quay 5, later to be merged into quay 6.
		StopPoint spToHaveStopAreaRefReplacedByMerger = createStopPoint("1", stopAreaDAO.findByObjectId("NSR:Quay:5"));
		// Create stop point with ref to non NSR-id to be replaced by new Quay whit org id as import_id

		StopArea stopAreaWithImportId = new StopArea();
		stopAreaWithImportId.setAreaType(ChouetteAreaEnum.BoardingPosition);
		stopAreaWithImportId.setObjectId("SKY:Quay:777777");
		stopAreaDAO.create(stopAreaWithImportId);
		StopPoint spToHaveStopAreaRefReplacedByAddedOriginalId = createStopPoint("2", stopAreaWithImportId);

		utx.commit();
		utx.begin();
		em.joinTransaction();
		stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(new FileInputStream("src/test/data/StopAreasUpdateMergedStops.xml"));


		// Quay 5 merged with quay 6
		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:5"), "Did not expect to find quay merged into another quay");
		assertStopPlace("NSR:StopPlace:6", "NSR:Quay:6");

		assertStopPlace("NSR:StopPlace:7", "NSR:Quay:7");
		utx.commit();

		utx.begin();
		em.joinTransaction();
		ContextHolder.setContext("chouette_gui");
		StopPoint spWithReplacedStopAreaRefByMerger = stopPointDAO.findByObjectId(spToHaveStopAreaRefReplacedByMerger.getObjectId());
		Assert.assertEquals(spWithReplacedStopAreaRefByMerger.getScheduledStopPoint().getContainedInStopAreaRef().getObjectId(), "NSR:Quay:6", "Expected stop point to updated when quays have been merged.");

		StopPoint spWithReplacedStopAreaRefByAddedOriginalId = stopPointDAO.findByObjectId(spToHaveStopAreaRefReplacedByAddedOriginalId.getObjectId());
		Assert.assertEquals(spWithReplacedStopAreaRefByAddedOriginalId.getScheduledStopPoint().getContainedInStopAreaRef().getObjectId(), "NSR:Quay:7", "Expected stop point to updated when quay id has been added as original id to another quay.");


		stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(new FileInputStream("src/test/data/StopAreasMovedQuay.xml"));

		Assert.assertEquals(stopAreaDAO.findByObjectId("NSR:Quay:99319").getParent().getObjectId(), "NSR:StopPlace:62006", "Expected quay to have moved to new parent stop area");

		StopArea knownStopArea = stopAreaDAO.findByObjectId("NSR:StopPlace:62006");

		assertCodeValuesForKnownStop(knownStopArea);
		knownStopArea.getContainedStopAreas().forEach(quay -> assertCodeValuesForKnownStop(quay));


		utx.commit();
	}

	private void assertCodeValuesForKnownStop(StopArea knownStopArea) {
		Assert.assertEquals(knownStopArea.getStopAreaType(), StopAreaTypeEnum.RailStation);
		Assert.assertEquals(knownStopArea.getTransportModeName(), TransportModeNameEnum.Rail);
		Assert.assertEquals(knownStopArea.getTransportSubMode(), TransportSubModeNameEnum.TouristRailway);
	}


	@Test
	public void testStopAreaUpdateForMultiModalStop() throws Exception {
		utx.begin();
		em.joinTransaction();

		String parentName = "Super stop place name";

		stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(new FileInputStream("src/test/data/StopAreasMultiModalImport.xml"));

		StopArea stopAreaParent = assertStopPlace("NSR:StopPlace:4000");
		Assert.assertEquals(stopAreaParent.getName(), parentName);

		StopArea stopAreaChild1 = assertStopPlace("NSR:StopPlace:1000", "NSR:Quay:1000");
		Assert.assertEquals(stopAreaChild1.getParent(), stopAreaParent, "Expected child to have parent set");
		Assert.assertEquals(stopAreaChild1.getName(), parentName, "Expected child to get parents name");
		StopArea stopAreaChild2 = assertStopPlace("NSR:StopPlace:2000");
		Assert.assertEquals(stopAreaChild2.getParent(), stopAreaParent, "Expected child to have parent set");
		Assert.assertEquals(stopAreaChild2.getName(), parentName, "Expected child to get parents name");


		stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(new FileInputStream("src/test/data/StopAreasMultiModalRemoval.xml"));

		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:StopPlace:4000"), "Did not expect to find deactivated parent stop place");
		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:StopPlace:2000"), "Did not expect to find stop with deactivated parent ");
		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:StopPlace:1000"), "Did not expect to find stop with deactivated parent");
		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:1000"), "Did not expect to find quay with deactivated stop place parent");

		utx.rollback();
	}

	@Test
	public void testDeleteStopAreaWithQuays() throws Exception {
		String stopAreaId = "NSR:StopPlace:1";
		utx.begin();
		em.joinTransaction();

		stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(new FileInputStream("src/test/data/StopAreasInitialSynch.xml"));
		assertStopPlace(stopAreaId, "NSR:Quay:1a", "NSR:Quay:1b");

		stopAreaService.deleteStopArea(stopAreaId);

		Assert.assertNull(stopAreaDAO.findByObjectId(stopAreaId));
		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:1a"), "Expected quay to have been cascade deleted");
		Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:1b"), "Expected quay to have been cascade deleted");

		utx.rollback();
	}

	@Test
	public void testDeleteUnusedStopAreas() throws Exception {
		utx.begin();
		em.joinTransaction();
		StopArea inUseStop = commercialStopWithTwoBoardingPositions("1");
		StopArea unusedStop = commercialStopWithTwoBoardingPositions("2");

		ContextHolder.setContext("chouette_gui");
		cleanStopPoints();
		StopPoint stopPoint = createStopPoint("2", inUseStop.getContainedStopAreas().get(0));

		utx.commit();

		stopAreaService.deleteUnusedStopAreas();

		utx.begin();
		em.joinTransaction();
		Assert.assertNull(stopAreaDAO.findByObjectId(unusedStop.getObjectId()), "Expected unused stop area to be deleted");

		StopArea inUseStopAfterDelete = stopAreaDAO.findByObjectId(inUseStop.getObjectId());
		Assert.assertNotNull(inUseStopAfterDelete, "Expected stop area referred to by stop point not to be deleted");
		Assert.assertEquals(inUseStopAfterDelete.getContainedStopAreas().size(), 2);
		utx.rollback();
	}

	private void cleanStopPoints() {
		stopPointDAO.truncate();
		scheduledStopPointDAO.truncate();
	}


	private StopArea commercialStopWithTwoBoardingPositions(String id) {
		StopArea bp1 = new StopArea();
		bp1.setAreaType(ChouetteAreaEnum.BoardingPosition);
		bp1.setObjectId("SKY:Quay:" + id + "a");

		StopArea bp2 = new StopArea();
		bp2.setAreaType(ChouetteAreaEnum.BoardingPosition);
		bp2.setObjectId("SKY:Quay:" + id + "b");

		StopArea commercialStop = new StopArea();
		commercialStop.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
		commercialStop.setObjectId("SKY:StopPlace:" + id);

		bp1.setParent(commercialStop);
		bp2.setParent(commercialStop);

		stopAreaDAO.create(commercialStop);
		return commercialStop;
	}


	private StopArea assertStopPlace(String stopPlaceId, String... quayIds) {
		StopArea stopPlace = stopAreaDAO.findByObjectId(stopPlaceId);
		Assert.assertNotNull(stopPlace, "Expected to find stop place with known id: " + stopPlaceId);
		if (quayIds != null) {

			for (String quayId : quayIds) {
				StopArea quay = stopAreaDAO.findByObjectId(quayId);
				Assert.assertNotNull(quay, "Expected stop to have quay with known id: " + quayId);
				Assert.assertEquals(quay.getParent(), stopPlace);
			}
		}

		return stopPlace;
	}

	private StopPoint createStopPoint(String id, StopArea containedStopArea) {
		StopPoint sp = new StopPoint();
		sp.setObjectId("XXX:StopPoint:" + id);

		ScheduledStopPoint scheduledStopPoint = new ScheduledStopPoint();
		scheduledStopPoint.setObjectId("XXX:ScheduledStopPoint:" + id);

		scheduledStopPoint.setContainedInStopAreaRef(new SimpleObjectReference(containedStopArea));
		sp.setScheduledStopPoint(scheduledStopPoint);
		stopPointDAO.create(sp);
		return sp;
	}


}
