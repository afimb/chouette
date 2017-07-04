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

import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.StopAreaTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.TransportSubModeEnum;
import mobi.chouette.persistence.hibernate.ContextHolder;

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
    public void testStopAreaUpdate() throws Exception {
        utx.begin();
        em.joinTransaction();

        stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(new FileInputStream("src/test/data/StopAreasInitialSynch.xml"));

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

        Assert.assertNull(stopAreaDAO.findByObjectId("NSR:StopPlace:1"), "Did not expect to find deactivated stop place");
        Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:1a"), "Did not expect to find quay for deactivated stop place");
        Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:1b"), "Did not expect to find quay for deactivated stop place");

        // New quay, removed quay and moved quay for 2
        assertStopPlace("NSR:StopPlace:2", "NSR:Quay:3a", "NSR:Quay:2b");
        Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:2a"), "Did not expect to find removed quay");
        assertStopPlace("NSR:StopPlace:3");

        ContextHolder.setContext("chouette_gui");
        stopPointDAO.truncate();
        // Create stop point contained in quay 5, later to be merged into quay 6.
        StopPoint spToHaveStopAreaRefReplacedByMerger = createStopPoint("XXX:StopPoint:1", stopAreaDAO.findByObjectId("NSR:Quay:5"));
        // Create stop point with ref to non NSR-id to be replaced by new Quay whit org id as import_id

        StopArea stopAreaWithImportId = new StopArea();
        stopAreaWithImportId.setAreaType(ChouetteAreaEnum.BoardingPosition);
        stopAreaWithImportId.setObjectId("SKY:Quay:777777");
        stopAreaDAO.create(stopAreaWithImportId);
        StopPoint spToHaveStopAreaRefReplacedByAddedOriginalId = createStopPoint("XXX:StopPoint:2", stopAreaWithImportId);

        utx.commit();
        utx.begin();
        em.joinTransaction();
        stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(new FileInputStream("src/test/data/StopAreasUpdateMergedStops.xml"));


        // Quay 5 merged with quay 6
        Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:5"), "Did not expect to find quay merged into another quay");
        assertStopPlace("NSR:StopPlace:6", "NSR:Quay:6");

        assertStopPlace("NSR:StopPlace:7", "NSR:Quay:7");
        utx.commit();

        ContextHolder.setContext("chouette_gui");
        StopPoint spWithReplacedStopAreaRefByMerger = stopPointDAO.findByObjectId(spToHaveStopAreaRefReplacedByMerger.getObjectId());
        Assert.assertEquals(spWithReplacedStopAreaRefByMerger.getContainedInStopArea().getObjectId(), "NSR:Quay:6", "Expected stop point to updated when quays have been merged.");

        StopPoint spWithReplacedStopAreaRefByAddedOriginalId = stopPointDAO.findByObjectId(spToHaveStopAreaRefReplacedByAddedOriginalId.getObjectId());
        Assert.assertEquals(spWithReplacedStopAreaRefByAddedOriginalId.getContainedInStopArea().getObjectId(), "NSR:Quay:7", "Expected stop point to updated when quay id has been added as original id to another quay.");

        utx.begin();
        em.joinTransaction();
        stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(new FileInputStream("src/test/data/StopAreasMovedQuay.xml"));

        Assert.assertEquals(stopAreaDAO.findByObjectId("NSR:Quay:99319").getParent().getObjectId(), "NSR:StopPlace:62006", "Expected quay to have moved to new parent stop area");

        StopArea knownStopArea = stopAreaDAO.findByObjectId("NSR:StopPlace:62006");

        Assert.assertEquals(knownStopArea.getStopAreaType(), StopAreaTypeEnum.RailStation);
        Assert.assertEquals(knownStopArea.getTransportModeName(), TransportModeNameEnum.Train);
        Assert.assertEquals(knownStopArea.getTransportSubMode(), TransportSubModeEnum.TouristRailway);

        utx.commit();
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
        stopPointDAO.truncate();
        StopPoint stopPoint = createStopPoint("XXX:StopPoint:2", inUseStop.getContainedStopAreas().get(0));

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

    private StopPoint createStopPoint(String id, StopArea stopArea) {
        StopPoint sp = new StopPoint();
        sp.setObjectId(id);
        sp.setContainedInStopArea(stopArea);
        stopPointDAO.create(sp);
        return sp;
    }


}
