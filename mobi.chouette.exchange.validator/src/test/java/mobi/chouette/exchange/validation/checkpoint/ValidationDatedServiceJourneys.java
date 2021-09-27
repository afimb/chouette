package mobi.chouette.exchange.validation.checkpoint;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.dao.JourneyPatternDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validator.DummyChecker;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.model.DatedServiceJourney;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.VehicleJourney;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class ValidationDatedServiceJourneys extends AbstractTestValidation {
    private DatedServiceJourneyCheckPoints checkPoint = new DatedServiceJourneyCheckPoints();
    private ValidationParameters fullparameters;
    private VehicleJourney bean1;
    private VehicleJourney bean2;
    private List<VehicleJourney> beansFor4 = new ArrayList<>();

    @Deployment
    public static EnterpriseArchive createDeployment() {

        EnterpriseArchive result;
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("mobi.chouette:mobi.chouette.exchange.validator").withTransitivity().asFile();
        List<File> jars = new ArrayList<>();
        List<JavaArchive> modules = new ArrayList<>();
        for (File file : files) {
            if (file.getName().startsWith("mobi.chouette.exchange")) {
                String name = file.getName().split("\\-")[0] + ".jar";
                JavaArchive archive = ShrinkWrap
                        .create(ZipImporter.class, name)
                        .importFrom(file)
                        .as(JavaArchive.class);
                modules.add(archive);
            } else {
                jars.add(file);
            }
        }
        File[] filesDao = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("mobi.chouette:mobi.chouette.dao").withTransitivity().asFile();
        if (filesDao.length == 0) {
            throw new NullPointerException("no dao");
        }
        for (File file : filesDao) {
            if (file.getName().startsWith("mobi.chouette.dao")) {
                String name = file.getName().split("\\-")[0] + ".jar";

                JavaArchive archive = ShrinkWrap
                        .create(ZipImporter.class, name)
                        .importFrom(file)
                        .as(JavaArchive.class);
                modules.add(archive);
                if (!modules.contains(archive))
                    modules.add(archive);
            } else {
                if (!jars.contains(file))
                    jars.add(file);
            }
        }
        final WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
                .addClass(DummyChecker.class)
                .addClass(JobDataTest.class)
                .addClass(AbstractTestValidation.class)
                .addClass(ValidationDatedServiceJourneys.class);

        result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
                .addAsLibraries(jars.toArray(new File[0]))
                .addAsModules(modules.toArray(new JavaArchive[0]))
                .addAsModule(testWar)
                .addAsResource(EmptyAsset.INSTANCE, "beans.xml");
        return result;
    }

    @BeforeGroups(groups = {"datedServiceJourney"})
    public void init() {

    super.init();

        long id = 1;

        fullparameters = null;
        try {
            fullparameters = loadFullParameters();
            fullparameters.setCheckVehicleJourney(1);

            Line line = new Line();
            line.setId(id++);
            line.setObjectId("test1:Line:1");
            line.setName("test");
            Route route = new Route();
            route.setId(id++);
            route.setObjectId("test1:Route:1");
            route.setName("test1");
            route.setLine(line);
            JourneyPattern jp = new JourneyPattern();
            jp.setId(id++);
            jp.setObjectId("test1:JourneyPattern:1");
            jp.setName("test1");
            jp.setRoute(route);
            bean1 = new VehicleJourney();
            bean1.setId(id++);
            bean1.setObjectId("test1:VehicleJourney:1");
            bean1.setPublishedJourneyName("test1");
            bean1.setJourneyPattern(jp);
            bean2 = new VehicleJourney();
            bean2.setId(id++);
            bean2.setObjectId("test2:VehicleJourney:1");
            bean2.setPublishedJourneyName("test2");
            bean2.setJourneyPattern(jp);

            DatedServiceJourney dsj1 = new DatedServiceJourney();
            dsj1.setVehicleJourney(bean2);
            dsj1.setObjectId("TEST-DSJ-1");
            dsj1.setOperatingDay(LocalDate.parse("2021-09-19"));

            DatedServiceJourney dsj2 = new DatedServiceJourney();
            dsj2.setVehicleJourney(bean2);
            dsj2.setObjectId("TEST-DSJ-2");
            dsj2.setOperatingDay(LocalDate.parse("2021-09-20"));

            dsj2.getOriginalDatedServiceJourneys().add(dsj1);

            beansFor4.add(bean1);
            beansFor4.add(bean2);
        } catch (Exception e) {
            fullparameters = null;
            e.printStackTrace();
        }

    }

    @Test(groups = {"datedServiceJourney"}, description = "3-DatedServiceJourney-1 same operating day for referenced dsj", priority = 2)
    public void verifyTest3_1() throws Exception {
   log.info(Color.BLUE + "3-DatedServiceJourney-1 same operating day for referenced dsj" + Color.NORMAL);
        Context context = initValidatorContext();
        Assert.assertNotNull(fullparameters, "no parameters for test");

        context.put(VALIDATION, fullparameters);
        context.put(VALIDATION_REPORT, new ValidationReport());

        fullparameters.setCheckVehicleJourney(1);
        fullparameters.getVehicleJourney().getObjectId().setUnique(1);

        ValidationData data = new ValidationData();
        data.getVehicleJourneys().addAll(beansFor4);
        context.put(VALIDATION_DATA, data);

        checkPoint.validate(context, null);
        fullparameters.getRoute().getObjectId().setUnique(0);
        // unique
        ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

        List<CheckPointErrorReport> details = checkReportForTest(report, "3-DatedServiceJourney-1", 2);
        for (CheckPointErrorReport detail : details) {
            Assert.assertEquals(detail.getSource().getObjectId(), "TEST-DSJ-2");
            Assert.assertEquals(detail.getTargets().get(0).getObjectId(), "TEST-DSJ-1");
        }
    }


}
