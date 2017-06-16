package mobi.chouette.service;

import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;

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

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class StopAreaServiceTest extends Arquillian {


    @EJB
    StopAreaService stopAreaService;

    @EJB
    StopAreaDAO stopAreaDAO;

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

        // Update stop places
        stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(new FileInputStream("src/test/data/StopAreasUpdate.xml"));

        Assert.assertNull(stopAreaDAO.findByObjectId("NSR:StopPlace:1"), "Did not expect to find deactivated stop place");
        Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:1a"), "Did not expect to find quay for deactivated stop place");
        Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:1b"), "Did not expect to find quay for deactivated stop place");

        // New quay, removed quay and moved quay for 2
        assertStopPlace("NSR:StopPlace:2", "NSR:Quay:3a", "NSR:Quay:2b");
        Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:2a"), "Did not expect to find removed quay");
        assertStopPlace("NSR:StopPlace:3");

        utx.commit();
        utx.begin();
        em.joinTransaction();
        stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(new FileInputStream("src/test/data/StopAreasUpdateMergedStops.xml"));
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
        Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:1a"),"Expected quay to have been cascade deleted");
        Assert.assertNull(stopAreaDAO.findByObjectId("NSR:Quay:1b"),"Expected quay to have been cascade deleted");

        utx.rollback();
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

}
