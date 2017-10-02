package mobi.chouette.dao;

import mobi.chouette.model.SimpleObjectReference;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.persistence.hibernate.ContextHolder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ejb.EJB;
import java.io.File;

public class RelationsToStopAreaInterceptorTest extends Arquillian {

    @EJB
    ScheduledStopPointDAO scheduledStopPointDAO;

    @EJB
    StopAreaDAO stopAreaDAO;

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
    public void stopAreasArePersistedWhenScheduledStopPointsArePersistedAndPopulatedUponFetching() {
        ScheduledStopPoint scheduledStopPoint = new ScheduledStopPoint();
        scheduledStopPoint.setObjectId("ScheduledStopPoint:ID");

        StopArea stopArea = new StopArea();
        stopArea.setAreaType(ChouetteAreaEnum.BoardingPosition);
        stopArea.setObjectId("StopArea:ID");

        scheduledStopPoint.setContainedInStopAreaRef(new SimpleObjectReference(stopArea));

        ContextHolder.setContext("chouette_gui"); // set tenant schema
        scheduledStopPointDAO.create(scheduledStopPoint);

        ScheduledStopPoint dbScheduledStopPoint = scheduledStopPointDAO.findByObjectId(scheduledStopPoint.getObjectId());

        Assert.assertEquals(dbScheduledStopPoint.getContainedInStopAreaRef().getObjectId(), stopArea.getObjectId());

        StopArea dbStopArea = stopAreaDAO.findByObjectId(stopArea.getObjectId());

        Assert.assertEquals(dbStopArea.getContainedScheduledStopPoints().size(), 1);
        Assert.assertEquals(dbStopArea.getContainedScheduledStopPoints().get(0).getObjectId(), scheduledStopPoint.getObjectId());
    }

}
