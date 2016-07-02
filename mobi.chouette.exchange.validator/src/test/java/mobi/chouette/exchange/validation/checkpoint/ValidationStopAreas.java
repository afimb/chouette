package mobi.chouette.exchange.validation.checkpoint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.exchange.validator.DummyChecker;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

import org.codehaus.jettison.json.JSONArray;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.vividsolutions.jts.geom.Polygon;

@Log4j
public class ValidationStopAreas extends AbstractTestValidation {
	private StopAreaCheckPoints checkPoint = new StopAreaCheckPoints();
	private ValidationParameters fullparameters;
	private StopArea bean1;
	private StopArea bean2;
	private StopArea bean3;
	private StopArea bean4;
	private StopArea bean5;
	private List<StopArea> beansFor4 = new ArrayList<>();

	@EJB 
	StopAreaDAO stopAreaDao;

	@PersistenceContext(unitName = "referential")
	EntityManager em;

	@Inject
	UserTransaction utx;

	@Deployment
	public static EnterpriseArchive createDeployment() {

		EnterpriseArchive result;
		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.exchange.validator").withTransitivity().asFile();
		List<File> jars = new ArrayList<>();
		List<JavaArchive> modules = new ArrayList<>();
		for (File file : files) {
			if (file.getName().startsWith("mobi.chouette.exchange"))
			{
				String name = file.getName().split("\\-")[0]+".jar";
				JavaArchive archive = ShrinkWrap
						  .create(ZipImporter.class, name)
						  .importFrom(file)
						  .as(JavaArchive.class);
				modules.add(archive);
			}
			else
			{
				jars.add(file);
			}
		}
		File[] filesDao = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.dao").withTransitivity().asFile();
		if (filesDao.length == 0) 
		{
			throw new NullPointerException("no dao");
		}
		for (File file : filesDao) {
			if (file.getName().startsWith("mobi.chouette.dao"))
			{
				String name = file.getName().split("\\-")[0]+".jar";
				
				JavaArchive archive = ShrinkWrap
						  .create(ZipImporter.class, name)
						  .importFrom(file)
						  .as(JavaArchive.class);
				modules.add(archive);
				if (!modules.contains(archive))
				   modules.add(archive);
			}
			else
			{
				if (!jars.contains(file))
				   jars.add(file);
			}
		}
		final WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
				.addClass(DummyChecker.class)
				.addClass(JobDataTest.class)
				.addClass(AbstractTestValidation.class)
				.addClass(ValidationStopAreas.class);
		
		result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
				.addAsLibraries(jars.toArray(new File[0]))
				.addAsModules(modules.toArray(new JavaArchive[0]))
				.addAsModule(testWar)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;
	}


	@BeforeGroups(groups = { "stopArea" })
	public void init() {
		super.init();

		long id = 1;

		fullparameters = null;
		try {
			fullparameters = loadFullParameters();
			fullparameters.setCheckStopArea(1);

			bean1 = new StopArea();
			bean1.setId(id++);
			bean1.setObjectId("test1:StopArea:1");
			bean1.setName("test1");
			bean1.setAreaType(ChouetteAreaEnum.BoardingPosition);
			bean1.setCountryCode("60124");
			bean1.setCityName("Ville1");
			bean2 = new StopArea();
			bean2.setId(id++);
			bean2.setObjectId("test2:StopArea:1");
			bean2.setName("test2");
			bean2.setAreaType(ChouetteAreaEnum.BoardingPosition);
			bean2.setCountryCode("60123");
			bean2.setCityName("Ville1");
			bean3 = new StopArea();
			bean3.setId(id++);
			bean3.setObjectId("test2:StopArea:3");
			bean3.setName("test2");
			bean3.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
			bean3.setCountryCode("60124");
			bean3.setCityName("Ville1");
			bean4 = new StopArea();
			bean4.setId(id++);
			bean4.setObjectId("test2:StopArea:4");
			bean4.setName("test4");
			bean4.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
			bean4.setCountryCode("60124");
			bean4.setCityName("Ville2");
			bean5 = new StopArea();
			bean5.setId(id++);
			bean5.setObjectId("test2:StopArea:4");
			bean5.setName("test4");
			bean5.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
			bean5.setCountryCode("50124");
			bean5.setCityName("Ville1");

			beansFor4.add(bean1);
			beansFor4.add(bean2);

			

		} catch (Exception e) {
			fullparameters = null;
			e.printStackTrace();
		}

	}

	@Test(groups = { "stopArea" }, description = "enveloppe", priority = 1)
	public void verifyEnveloppe() throws Exception {

		log.info(Color.BLUE + "enveloppe" + Color.NORMAL);
		
		importLines("model.zip", 7, 7, true);
		
		Assert.assertNotNull(fullparameters, "no parameters for test");

		String saveEnveloppe = fullparameters.getStopAreasArea();
		fullparameters.setStopAreasArea("");

		Polygon enveloppe = checkPoint.getEnveloppe(fullparameters);
		Assert.assertNotNull(enveloppe, "enveloppe should be found");

		fullparameters.setStopAreasArea(null);
		enveloppe = checkPoint.getEnveloppe(fullparameters);
		Assert.assertNotNull(enveloppe, "enveloppe should be found");

		fullparameters.setStopAreasArea(saveEnveloppe);

	}

	@Test(groups = { "stopArea" }, description = "3-StopArea-1", priority = 2)
	public void verifyTest3_1() throws Exception {
		// 3-StopArea-1 : check if all non ITL stopArea has geolocalization
		log.info(Color.BLUE + "3-StopArea-1" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<StopArea> beans = stopAreaDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		StopArea area1 = beans.get(0);
		area1.setLongLatType(null);
		area1.setLongitude(null);

		ValidationData data = new ValidationData();
		data.getStopAreas().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-StopArea-1");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-StopArea-1 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.ERROR,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");
		String detailKey = "3-StopArea-1".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-StopArea-1",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		for (CheckPointErrorReport detail : details) {
			log.warn(detail);
			Assert.assertEquals(detail.getSource().getObjectId(), area1.getObjectId(), "area1 must be source of error");
		}

		utx.rollback();

	}

	@Test(groups = { "stopArea" }, description = "3-StopArea-2", priority = 3)
	public void verifyTest3_2() throws Exception {
		// 3-StopArea-2 : check distance of stop areas with different name
		log.info(Color.BLUE + "3-StopArea-2" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<StopArea> beans = stopAreaDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		StopArea area1 = null;
		for (StopArea stopArea : beans) {
			if (stopArea.getAreaType().equals(ChouetteAreaEnum.BoardingPosition)) {
				area1 = stopArea;
				break;
			}
		}
		StopArea area2 = new StopArea();
		area2.setId(1000000L);
		area2.setObjectId("NINOXE:StopArea:1000000");
		area2.setName("Doublon " + area1.getName());
		area2.setAreaType(area1.getAreaType());
		area2.setLongLatType(area1.getLongLatType());
		area2.setLongitude(area1.getLongitude());
		area2.setLatitude(area1.getLatitude());
		beans.add(0, area2);

		ValidationData data = new ValidationData();
		data.getStopAreas().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-StopArea-2");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-StopArea-2 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");
		String detailKey = "3-StopArea-2".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-StopArea-2",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}
		utx.rollback();
	}

	@Test(groups = { "stopArea" }, description = "3-StopArea-3", priority = 4)
	public void verifyTest3_3() throws Exception {

		// 3-StopArea-3 : check multiple occurrence of a stopArea
		log.info(Color.BLUE + "3-StopArea-3" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<StopArea> beans = stopAreaDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		StopArea area1 = null;
		StopArea area2 = null;
		for (StopArea stopArea : beans) {
			if (stopArea.getObjectId().equals("NINOXE:StopArea:15568801")) // St
			// Paul
			{
				area1 = stopArea;
			}
			if (stopArea.getObjectId().equals("NINOXE:StopArea:15568802")) // place
			// de
			// verdun
			{
				area2 = stopArea;
			}
			if (area1 != null && area2 != null)
				break;

		}
		area2.setName(area1.getName());
		area2.setAreaType(area1.getAreaType());
		area2.setStreetName(area1.getStreetName());
		area2.setCountryCode(area1.getCountryCode());

		ValidationData data = new ValidationData();
		data.getStopAreas().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-StopArea-3");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-StopArea-3 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");
		String detailKey = "3-StopArea-3".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-StopArea-3",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}

		utx.rollback();
	}

	@Test(groups = { "stopArea" }, description = "3-StopArea-4", priority = 5)
	public void verifyTest3_4() throws Exception {
		// 3-StopArea-4 : check localization in a region
		log.info(Color.BLUE + "3-StopArea-4" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<StopArea> beans = stopAreaDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		double minLat = 90;
		double maxLat = 0;
		double minLon = 180;
		double maxLon = -180;
		for (StopArea area : beans) {
			if (area.getAreaType().equals(ChouetteAreaEnum.ITL))
				continue;
			if (area.getLatitude().doubleValue() > maxLat)
				maxLat = area.getLatitude().doubleValue();
			if (area.getLatitude().doubleValue() < minLat)
				minLat = area.getLatitude().doubleValue();
			if (area.getLongitude().doubleValue() > maxLon)
				maxLon = area.getLongitude().doubleValue();
			if (area.getLongitude().doubleValue() < minLon)
				minLon = area.getLongitude().doubleValue();
		}

		minLon = minLon + (maxLon - minLon) * 0.1;

		JSONArray array = new JSONArray();
		array.put(new JSONArray().put(minLon).put(minLat));
		array.put(new JSONArray().put(minLon).put(maxLat));
		array.put(new JSONArray().put(maxLon).put(maxLat));
		array.put(new JSONArray().put(maxLon).put(minLat));
		array.put(new JSONArray().put(minLon).put(minLat));

		fullparameters.setStopAreasArea(array.toString());
		context.put(VALIDATION, fullparameters);

		ValidationData data = new ValidationData();
		data.getStopAreas().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-StopArea-4");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-StopArea-4 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 17, " checkPointReport must have 17 item");
		String detailKey = "3-StopArea-4".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-StopArea-4",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}

		utx.rollback();

	}

	@Test(groups = { "stopArea" }, description = "3-StopArea-5", priority = 6)
	public void verifyTest3_5() throws Exception {
		// 3-StopArea-5 : check distance with parents
		log.info(Color.BLUE + "3-StopArea-3" + Color.NORMAL);
		Context context = initValidatorContext();
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());

		Assert.assertNotNull(fullparameters, "no parameters for test");

		utx.begin();
		em.joinTransaction();

		List<StopArea> beans = stopAreaDao.findAll();
		Assert.assertFalse(beans.isEmpty(), "No data for test");

		fullparameters.setParentStopAreaDistanceMax(300);

		ValidationData data = new ValidationData();
		data.getStopAreas().addAll(beans);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertNotEquals(report.getCheckPoints().size(), 0, " report must have items");

		CheckPointReport checkPointReport = report.findCheckPointReportByName("3-StopArea-5");
		Assert.assertNotNull(checkPointReport, "report must contain a 3-StopArea-5 checkPoint");

		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.WARNING,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 2, " checkPointReport must have 2 item");
		String detailKey = "3-StopArea-5".replaceAll("-", "_").toLowerCase();
		List<CheckPointErrorReport> details = checkReportForTest(report,"3-StopArea-5",-1);
		for (CheckPointErrorReport detail : details) {
			Assert.assertTrue(detail.getKey().startsWith(detailKey),
					"details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
		}

		utx.rollback();

	}

	@Test(groups = { "stopArea" }, description = "4-StopArea-1 no test", priority = 7)
	public void verifyTest4_1_notest() throws Exception {
		// 4-StopArea-1 : check columns
		log.info(Color.BLUE + "4-StopArea-1 no test" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");
		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckStopArea(0);
		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getStopAreas().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-StopArea-1") == null,
				" report must not have item 4-StopArea-1");

		fullparameters.setCheckStopArea(1);
		context.put(VALIDATION_REPORT, new ValidationReport());

		checkPoint.validate(context, null);
		report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-StopArea-1") != null, " report must have item 4-StopArea-1");
		Assert.assertEquals(report.findCheckPointReportByName("4-StopArea-1").getCheckPointErrorCount(), 0,
				" checkpoint must have no detail");

	}

	@Test(groups = { "stopArea" }, description = "4-StopArea-1 unicity", priority = 8)
	public void verifyTest4_1_unique() throws Exception {
		// 4-StopArea-1 : check columns
		log.info(Color.BLUE + "4-StopArea-1 unicity" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");

		context.put(VALIDATION_REPORT, new ValidationReport());

		fullparameters.setCheckStopArea(1);
		fullparameters.getStopArea().getObjectId().setUnique(1);
		context.put(VALIDATION, fullparameters);
		ValidationData data = new ValidationData();
		data.getStopAreas().addAll(beansFor4);
		context.put(VALIDATION_DATA, data);

		checkPoint.validate(context, null);
		fullparameters.getStopArea().getObjectId().setUnique(0);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);

		List<CheckPointErrorReport> details = checkReportForTest(report, "4-StopArea-1", 1);
		CheckPointErrorReport detail = details.get(0);
		Assert.assertEquals(detail.getReferenceValue(), "ObjectId", "detail must refer column");
		Assert.assertEquals(detail.getValue(), bean2.getObjectId().split(":")[2], "detail must refer value");
	}

	@Test(groups = { "StopArea" }, description = "4-StopArea-2", priority = 9)
	public void verifyTest4_2() throws Exception {
		// 4-StopArea-2 : check parent
		log.info(Color.BLUE + "4-StopArea-2" + Color.NORMAL);
		Context context = initValidatorContext();
		Assert.assertNotNull(fullparameters, "no parameters for test");
		context.put(VALIDATION_REPORT, new ValidationReport());

		bean1.setParent(bean3);
		bean2.setParent(bean3);

		List<StopArea> list = new ArrayList<>(beansFor4);
		list.add(bean3);
		ValidationData data = new ValidationData();
		data.getStopAreas().addAll(list);
		context.put(VALIDATION_DATA, data);

		fullparameters.setCheckStopParent(0);
		context.put(VALIDATION, fullparameters);
		checkPoint.validate(context, null);

		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(report.findCheckPointReportByName("4-StopArea-2") == null,
				" report must not have item 4-StopArea-2");

		fullparameters.setCheckStopParent(1);
		context.put(VALIDATION, fullparameters);
		context.put(VALIDATION_REPORT, new ValidationReport());
		checkPoint.validate(context, null);

		report = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPointReport checkPointReport = report.findCheckPointReportByName("4-StopArea-2");
		Assert.assertNotNull(checkPointReport, "report must contain a 4-StopArea-2 checkPoint");
		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.OK, " checkPointReport must be ok");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 0, " checkPointReport must have 0 item");

		bean1.setParent(null);
		context.put(VALIDATION_REPORT, new ValidationReport());
		checkPoint.validate(context, null);

		report = (ValidationReport) context.get(VALIDATION_REPORT);
		checkPointReport = report.findCheckPointReportByName("4-StopArea-2");
		Assert.assertNotNull(checkPointReport, "report must contain a 4-StopArea-2 checkPoint");
		Assert.assertEquals(checkPointReport.getState(), ValidationReporter.RESULT.NOK, " checkPointReport must be nok");
		Assert.assertEquals(checkPointReport.getSeverity(), CheckPointReport.SEVERITY.ERROR,
				" checkPointReport must be on severity error");
		Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), 1, " checkPointReport must have 1 item");

	}


}
