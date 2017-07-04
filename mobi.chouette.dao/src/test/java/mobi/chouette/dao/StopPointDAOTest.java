package mobi.chouette.dao;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;

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


public class StopPointDAOTest extends Arquillian {
    @EJB
    StopPointDAO stopPointDAO;


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

        String oldStopAreaRef1 = "sa-old";
        StopArea stopAreaOld1 = new StopArea();
        stopAreaOld1.setImportMode(StopAreaImportModeEnum.READ_ONLY);
        stopAreaOld1.setObjectId(oldStopAreaRef1);


        String oldStopAreaRef2 = "sa-old-2";
        StopArea stopAreaOld2 = new StopArea();
        stopAreaOld2.setImportMode(StopAreaImportModeEnum.READ_ONLY);
        stopAreaOld2.setObjectId(oldStopAreaRef2);

        String newStopAreaRef = "sa-new";

        StopPoint sp1 = createStopPoint("sp1", stopAreaOld1);
        StopPoint sp2 = createStopPoint("sp2", stopAreaOld1);
        StopPoint sp3 = createStopPoint("sp3", stopAreaOld2);
        StopPoint sp4 = createStopPoint("sp4", null);

        Assert.assertTrue(stopPointDAO.getStopPointsContainedInStopArea(oldStopAreaRef1).containsAll(Arrays.asList(sp1, sp2)));
        Assert.assertTrue(stopPointDAO.getStopPointsContainedInStopArea(oldStopAreaRef2).containsAll(Arrays.asList(sp3)));

        stopPointDAO.replaceContainedInStopAreaReferences(Sets.newHashSet(oldStopAreaRef1, oldStopAreaRef2), newStopAreaRef);
        Assert.assertTrue(stopPointDAO.getStopPointsContainedInStopArea(oldStopAreaRef1).isEmpty());
        Assert.assertTrue(stopPointDAO.getStopPointsContainedInStopArea(oldStopAreaRef2).isEmpty());

        List<StopPoint> stopPointsForNewStopAreaRef = stopPointDAO.getStopPointsContainedInStopArea(newStopAreaRef);
        Assert.assertEquals(stopPointsForNewStopAreaRef.size(), 3);
        Assert.assertTrue(stopPointsForNewStopAreaRef.containsAll(Arrays.asList(sp1, sp2, sp3)));


        stopPointDAO.replaceContainedInStopAreaReferences(new HashSet<>(), "shouldNotFail");
        stopPointDAO.replaceContainedInStopAreaReferences(null, "shouldNotFail");
    }

    private StopPoint createStopPoint(String id, StopArea stopArea) {
        StopPoint sp = new StopPoint();
        sp.setObjectId(id);
        sp.setContainedInStopArea(stopArea);
        stopPointDAO.create(sp);
        return sp;
    }


}

