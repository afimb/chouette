package mobi.chouette.dao;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;

import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.StopAreaImportModeEnum;
import mobi.chouette.persistence.hibernate.ContextHolder;

import com.google.common.collect.Sets;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;


public class ScheduledStopPointDAOTest extends Arquillian {
    @EJB
    ScheduledStopPointDAO scheduledStopPointDAO;


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


    @Test
    public void testReplaceStopAreaReference() {
        ContextHolder.setContext("chouette_gui"); // set tenant schema
        scheduledStopPointDAO.truncate();

        String oldStopAreaRef1 = "sa-old";
        StopArea stopAreaOld1 = new StopArea();
        stopAreaOld1.setImportMode(StopAreaImportModeEnum.READ_ONLY);
        stopAreaOld1.setObjectId(oldStopAreaRef1);


        String oldStopAreaRef2 = "sa-old-2";
        StopArea stopAreaOld2 = new StopArea();
        stopAreaOld2.setImportMode(StopAreaImportModeEnum.READ_ONLY);
        stopAreaOld2.setObjectId(oldStopAreaRef2);

        String newStopAreaRef = "sa-new";

        ScheduledStopPoint sp1 = createScheduledStopPoint("sp1", stopAreaOld1);
        ScheduledStopPoint sp2 = createScheduledStopPoint("sp2", stopAreaOld1);
        ScheduledStopPoint sp3 = createScheduledStopPoint("sp3", stopAreaOld2);
        ScheduledStopPoint sp4 = createScheduledStopPoint("sp4", null);

        Assert.assertTrue(scheduledStopPointDAO.getScheduledStopPointsContainedInStopArea(oldStopAreaRef1).containsAll(Arrays.asList(sp1, sp2)));
        Assert.assertTrue(scheduledStopPointDAO.getScheduledStopPointsContainedInStopArea(oldStopAreaRef2).containsAll(Arrays.asList(sp3)));

        scheduledStopPointDAO.replaceContainedInStopAreaReferences(Sets.newHashSet(oldStopAreaRef1, oldStopAreaRef2), newStopAreaRef);
        Assert.assertTrue(scheduledStopPointDAO.getScheduledStopPointsContainedInStopArea(oldStopAreaRef1).isEmpty());
        Assert.assertTrue(scheduledStopPointDAO.getScheduledStopPointsContainedInStopArea(oldStopAreaRef2).isEmpty());

        List<ScheduledStopPoint> ScheduledStopPointsForNewStopAreaRef = scheduledStopPointDAO.getScheduledStopPointsContainedInStopArea(newStopAreaRef);
        Assert.assertEquals(ScheduledStopPointsForNewStopAreaRef.size(), 3);
        Assert.assertTrue(ScheduledStopPointsForNewStopAreaRef.containsAll(Arrays.asList(sp1, sp2, sp3)));


        scheduledStopPointDAO.replaceContainedInStopAreaReferences(new HashSet<>(), "shouldNotFail");
        scheduledStopPointDAO.replaceContainedInStopAreaReferences(null, "shouldNotFail");
    }

    @Test
    public void testGetAllStopAreaObjectIds() {
        ContextHolder.setContext("chouette_gui"); // set tenant schema
        scheduledStopPointDAO.truncate();

        String stopAreaRef1 = "sa";
        StopArea stopArea1 = new StopArea();
        stopArea1.setImportMode(StopAreaImportModeEnum.READ_ONLY);
        stopArea1.setObjectId(stopAreaRef1);


        String stopAreaRef2 = "sa-2";
        StopArea stopArea2 = new StopArea();
        stopArea2.setImportMode(StopAreaImportModeEnum.READ_ONLY);
        stopArea2.setObjectId(stopAreaRef2);

        ScheduledStopPoint sp1 = createScheduledStopPoint("sp1AllStopAreas", stopArea1);
        ScheduledStopPoint sp2 = createScheduledStopPoint("sp2AllStopAreas", stopArea1);
        ScheduledStopPoint sp3 = createScheduledStopPoint("sp3AllStopAreas", stopArea2);
        ScheduledStopPoint sp4 = createScheduledStopPoint("sp4AllStopAreas", null);


        List<String> allStopAreaObjectIds = scheduledStopPointDAO.getAllStopAreaObjectIds();

        Assert.assertEquals(new HashSet<>(allStopAreaObjectIds), Sets.newHashSet(stopAreaRef1, stopAreaRef2));
    }


    private ScheduledStopPoint createScheduledStopPoint(String id, StopArea stopArea) {
        ScheduledStopPoint sp = new ScheduledStopPoint();
        sp.setObjectId(id);
        sp.setContainedInStopArea(stopArea);
        scheduledStopPointDAO.create(sp);
        return sp;
    }


}

