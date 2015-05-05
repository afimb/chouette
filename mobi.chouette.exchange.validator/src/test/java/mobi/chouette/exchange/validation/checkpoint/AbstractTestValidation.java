package mobi.chouette.exchange.validation.checkpoint;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Time;
import java.util.Locale;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.neptune.importer.NeptuneImporterCommand;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.LineInfo.LINE_STATE;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.parameters.NetworkParameters;
import mobi.chouette.exchange.validation.parameters.TransportModeParameters;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.exchange.validator.ValidateParameters;
import mobi.chouette.model.NeptuneLocalizedObject;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.testng.Arquillian;
import org.testng.Assert;

@Log4j
public abstract class AbstractTestValidation  extends Arquillian implements Constant, ReportConstant {

	@EJB
	LineDAO lineDao;


	protected InitialContext initialContext;

	public void init()
	{
		Locale.setDefault(Locale.ENGLISH);
		if (initialContext == null) {
			try {
				initialContext = new InitialContext();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

	}

	protected Context initImportContext() {
		init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(MAIN_VALIDATION_REPORT, new ValidationReport());
		NeptuneImportParameters configuration = new NeptuneImportParameters();
		context.put(CONFIGURATION, configuration);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setNoSave(true);
		configuration.setOrganisationName("organisation");
		configuration.setReferentialName("test");
		JobDataTest test = new JobDataTest();
		context.put(JOB_DATA, test);
		test.setPathName( "target/referential/test");
		File f = new File("target/referential/test");
		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		f.mkdirs();
		test.setReferential( "chouette_gui");
		test.setAction( IMPORTER);
		test.setType("neptune");
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}
	protected Context initValidatorContext() {
		init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(MAIN_VALIDATION_REPORT, new ValidationReport());
		ValidateParameters configuration = new ValidateParameters();
		context.put(CONFIGURATION, configuration);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setOrganisationName("organisation");
		configuration.setReferentialName("test");
		JobDataTest test = new JobDataTest();
		context.put(JOB_DATA, test);
		test.setPathName( "target/referential/test");
		File f = new File("target/referential/test");
		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		f.mkdirs();
		test.setReferential( "chouette_gui");
		test.setAction( VALIDATOR);
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}

	protected static final String path = "src/test/data/checkpoints";
	public static  void copyFile(String fileName) throws IOException {
		File srcFile = new File(path, fileName);
		File destFile = new File("target/referential/test", fileName);
		FileUtils.copyFile(srcFile, destFile);
	}

	protected void importLines(String file, int fileCount, int lineCount) throws Exception
	{
		Context context = initImportContext();


		NeptuneImporterCommand command = (NeptuneImporterCommand) CommandFactory.create(initialContext,
				NeptuneImporterCommand.class.getName());
		copyFile(file);
		JobDataTest test = (JobDataTest) context.get(JOB_DATA);
		test.setFilename( file);
		NeptuneImportParameters configuration = (NeptuneImportParameters) context.get(CONFIGURATION);
		configuration.setNoSave(false);
		configuration.setCleanRepository(true);
		try {
			command.execute(context);
		} catch (Exception ex) {
			log.error("test failed", ex);
			throw ex;
		}
		ActionReport report = (ActionReport) context.get(REPORT);
		Assert.assertEquals(report.getResult(), STATUS_OK, "result");
		Assert.assertEquals(report.getFiles().size(), fileCount, "file reported");
		Assert.assertEquals(report.getLines().size(), lineCount, "line reported");
		for (LineInfo info : report.getLines()) {
			Assert.assertEquals(info.getStatus(), LINE_STATE.OK, "line status");
		}


	}


	/**
	 * calculate distance on spheroid
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static double distance(NeptuneLocalizedObject obj1,
			NeptuneLocalizedObject obj2)
	{
		double long1rad = Math.toRadians(obj1.getLongitude().doubleValue());
		double lat1rad = Math.toRadians(obj1.getLatitude().doubleValue());
		double long2rad = Math.toRadians(obj2.getLongitude().doubleValue());
		double lat2rad = Math.toRadians(obj2.getLatitude().doubleValue());

		double alpha = Math.cos(lat1rad) * Math.cos(lat2rad)
				* Math.cos(long2rad - long1rad) + Math.sin(lat1rad)
				* Math.sin(lat2rad);

		double distance = 6378. * Math.acos(alpha);

		return distance * 1000.;
	}

	public static long diffTime(Time first, Time last)
	{
		if (first == null || last == null)
			return Long.MIN_VALUE; // TODO
		long diff = last.getTime() / 1000L - first.getTime() / 1000L;
		if (diff < 0)
			diff += 86400L; // step upon midnight : add one day in seconds
		return diff;
	}

	/**
	 * @param report
	 */
	protected Detail checkReportForTest4_1(ValidationReport report, String key, String objectId)
	{
		Assert.assertFalse(report.getCheckPoints().isEmpty(), " report must have items");
		Assert.assertNotNull(report.findCheckPointByName(key), " report must have 1 item on key "+key);
		CheckPoint checkPointReport = report.findCheckPointByName(key);
		Assert.assertEquals(checkPointReport.getDetails().size(), 1, " checkpoint must have 1 detail");
		Detail detail =  checkPointReport.getDetails().get(0);
		return detail;
	}

	protected ValidationParameters loadFullParameters() throws Exception
	{
		//		String filename = "fullparameterset.json";
		//		File f = new File(path,filename);
		//		return (ValidationParameters) JSONUtil.fromJSON(f.toPath(), ValidationParameters.class);
		ValidationParameters result = new ValidationParameters();
		result.setStopAreasArea("[[-5.2,42.25],[-5.2,51.1],[8.23,51.1],[8.23,42.25],[-5.2,42.25]]");
		result.setInterStopAreaDistanceMin(20);
		result.setParentStopAreaDistanceMax(350);
		result.setInterAccessPointDistanceMin(20);
		result.setInterConnectionLinkDistanceMax(800);
		result.setWalkDefaultSpeedMax(5);
		result.setWalkFrequentTravellerSpeedMax(4);
		result.setWalkOccasionalTravellerSpeedMax(4);
		result.setWalkMobilityRestrictedTravellerSpeedMax(2);
		result.setInterAccessLinkDistanceMax(300);
		result.setInterStopDurationMax(180);
		result.setFacilityStopAreaDistanceMax(300);
		result.setCheckAllowedTransportModes(0);
		result.setCheckLinesInGroups(0);
		result.setCheckLineRoutes(0);
		result.setCheckStopParent(0);
		result.setCheckConnectionLinkOnPhysical(0);
		result.setModeCoach(new TransportModeParameters(1, 500, 10000, 90, 40, 20));
		result.setModeAir(new TransportModeParameters(1, 200, 10000, 800, 700, 60));
		result.setModeWaterborne(new TransportModeParameters(1, 200, 10000, 40, 5, 60));
		result.setModeBus(new TransportModeParameters(1, 100, 40000, 1000, 5, 2000));
		result.setModeFerry(new TransportModeParameters(1, 200, 10000, 40, 5, 60));
		result.setModeWalk(new TransportModeParameters(1, 1, 10000, 6, 1, 10));
		result.setModeMetro(new TransportModeParameters(1, 300, 20000, 500, 25, 2000));
		result.setModeShuttle(new TransportModeParameters(1, 500, 20000, 80, 20, 10));
		result.setModeRapidTransit(new TransportModeParameters(1, 2000, 500000, 300, 20, 60));
		result.setModeTaxi(new TransportModeParameters(1, 500, 300000, 130, 20, 60));
		result.setModeLocalTrain(new TransportModeParameters(1, 2000, 500000, 300, 20, 60));
		result.setModeTrain(new TransportModeParameters(1, 2000, 500000, 300, 20, 60));
		result.setModeLongDistanceTrain(new TransportModeParameters(1, 2000, 500000, 300, 20, 60));
		result.setModeTramway(new TransportModeParameters(1, 300, 2000, 50, 20, 30));
		result.setModeTrolleybus(new TransportModeParameters(1, 300, 2000, 50, 20, 30));
		result.setModePrivateVehicle(new TransportModeParameters(1, 500, 300000, 130, 20, 60));
		result.setModeBicycle(new TransportModeParameters(1, 300, 30000, 40, 10, 10));
		result.setModeBicycle(new TransportModeParameters(1, 300, 30000, 40, 10, 10));
		result.setCheckNetwork(0);
		result.setNetwork(new NetworkParameters());
		
		return result;
	}

	protected ValidationParameters loadParameters() throws Exception
	{
		String filename = "parameterset.json";
		File f = new File(path,filename);
		byte[] bytes = Files.readAllBytes(f.toPath());
		String text = new String(bytes, "UTF-8");
		return (ValidationParameters) JSONUtil.fromJSON(text, ValidationParameters.class);

	}

}
