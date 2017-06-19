package mobi.chouette.dao;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;

import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.StopAreaImportModeEnum;
import mobi.chouette.persistence.hibernate.ContextHolder;

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

        String oldStopAreaRef = "sa-old";
        StopArea stopAreaOld = new StopArea();
        stopAreaOld.setImportMode(StopAreaImportModeEnum.READ_ONLY);
        stopAreaOld.setObjectId(oldStopAreaRef);
        String newStopAreaRef = "sa-new";

        StopPoint sp1 = createStopPoint("sp1", stopAreaOld);
        StopPoint sp2 = createStopPoint("sp2", stopAreaOld);
        StopPoint sp3 = createStopPoint("sp3", null);

        Assert.assertTrue(stopPointDAO.getStopPointsContainedInStopArea(oldStopAreaRef).containsAll(Arrays.asList(sp1, sp2)));

        stopPointDAO.replaceContainedInStopAreaReference(oldStopAreaRef, newStopAreaRef);
        Assert.assertTrue(stopPointDAO.getStopPointsContainedInStopArea(oldStopAreaRef).isEmpty());

        List<StopPoint> stopPointsForNewStopAreaRef = stopPointDAO.getStopPointsContainedInStopArea(newStopAreaRef);
        Assert.assertEquals(stopPointsForNewStopAreaRef.size(), 2);
        Assert.assertTrue(stopPointsForNewStopAreaRef.containsAll(Arrays.asList(sp1, sp2)));

    }

    private StopPoint createStopPoint(String id, StopArea stopArea) {
        StopPoint sp = new StopPoint();
        sp.setObjectId(id);
        sp.setContainedInStopArea(stopArea);
        stopPointDAO.create(sp);
        return sp;
    }


}

