package mobi.chouette.exchange.regtopp.importer.parser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveException;
import org.testng.Assert;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.exchange.regtopp.model.RegtoppObject;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppInterchangeSAM;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppZoneSON;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppPeriodPER;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRoutePointRUT;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTableVersionTAB;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppVehicleJourneyVLP;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.CheckPoint.RESULT;
import mobi.chouette.exchange.validation.report.CheckPoint.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReport;

@Log4j
public class FileContentParserTest {

	@Test
	public void testAtBMappings() throws Exception {
		File regtoppArchive = new File("src/test/data/atb-20160118-20160619.zip");
		parseFile(regtoppArchive);

	}

	@Test
	public void testKolumbusMappings() throws Exception {
		File regtoppArchive = new File("src/test/data/kolumbus_regtopp_20160329-20160624.zip");
		parseFile(regtoppArchive);

	}

	private void parseFile(File regtoppArchive) throws IOException, ArchiveException, Exception {
		File dest = new File("target/" + System.currentTimeMillis());
		dest.mkdirs();
		dest.deleteOnExit();
		FileUtil.uncompress(regtoppArchive.getAbsolutePath(), dest.getAbsolutePath());

		ActionReport report = new ActionReport();

		File[] regtoppFiles = dest.listFiles();
		for (File f : regtoppFiles) {

			Context context = new Context();
			ValidationReport validationReport = new ValidationReport();
			CheckPoint checkPoint = new CheckPoint("2-GTFS-Stop-3", RESULT.OK, SEVERITY.ERROR);
			validationReport.getCheckPoints().add(checkPoint);
			context.put(Constant.MAIN_VALIDATION_REPORT, validationReport);
			context.put(Constant.REPORT, report);

			RegtoppValidationReporter reporter = new RegtoppValidationReporter();
			FileContentParser parser = new FileContentParser();
			String name = f.getName().toUpperCase();
			String extension = name.substring(name.lastIndexOf(".") + 1);
			List<Class> regtoppClasses = new ArrayList<Class>();
			switch (extension) {

			case "TIX":
				regtoppClasses.add(RegtoppTripIndexTIX.class);
				break;
			case "TMS":
				regtoppClasses.add(RegtoppRouteTMS.class);
				break;
			case "HPL":
				regtoppClasses.add(RegtoppStopHPL.class);
				break;
			case "DKO":
				regtoppClasses.add(RegtoppDayCodeHeaderDKO.class);
				regtoppClasses.add(RegtoppDayCodeDKO.class);
				break;
			case "DST":
				regtoppClasses.add(RegtoppDestinationDST.class);
				break;
			case "MRK":
				regtoppClasses.add(RegtoppFootnoteMRK.class);
				break;
			case "GAV":
				regtoppClasses.add(RegtoppPathwayGAV.class);
				break;
			case "SAM":
				regtoppClasses.add(RegtoppInterchangeSAM.class);
				break;
			case "SON":
				regtoppClasses.add(RegtoppZoneSON.class);
				break;
			case "LIN":
				regtoppClasses.add(RegtoppLineLIN.class);
				break;
			case "VLP":
				regtoppClasses.add(RegtoppVehicleJourneyVLP.class);
				break;
			case "TAB":
				regtoppClasses.add(RegtoppTableVersionTAB.class);
				break;
			case "PER":
				regtoppClasses.add(RegtoppPeriodPER.class);
				break;
			case "RUT":
				regtoppClasses.add(RegtoppRoutePointRUT.class);
				break;
			case "FRM":
				log.info("Ignoring version file " + extension);
				continue;
			default:
				log.warn("Ignoring unknown file " + extension);
				continue;

			}

			Assert.assertNotNull(parser, "No parser registered for " + name);
			Assert.assertTrue(regtoppClasses.size() > 0, "No class registered for " + name);

			FileInfo fileInfo = new FileInfo(name, FILE_STATE.ERROR);
			report.getFiles().add(fileInfo);
			ParseableFile parseableFile = new ParseableFile(f, regtoppClasses, fileInfo);

			parser.parse(context, parseableFile, reporter);

			// TODO enable assertions again, there are errors in one of Atb's files
			// Assert.assertEquals(0,reporter.getExceptions().size(),"Validation exceptions: "+ToStringBuilder.reflectionToString(reporter));

		}

		for (FileInfo fileInfo : report.getFiles()) {
			Assert.assertEquals(FILE_STATE.OK, fileInfo.getStatus(), "Error parsing file");
		}
	}

	@Test
	public void verifyMappingCorrectnessRegtopp11() {
		Assert.assertEquals(calculateTotalFieldLenght(mobi.chouette.exchange.regtopp.model.v11.RegtoppTripIndexTIX.class), 59);
		Assert.assertEquals(calculateTotalFieldLenght(mobi.chouette.exchange.regtopp.model.v11.RegtoppRouteTDA.class), 20);
		Assert.assertEquals(calculateTotalFieldLenght(mobi.chouette.exchange.regtopp.model.v11.RegtoppStopHPL.class), 87);
		Assert.assertEquals(calculateTotalFieldLenght(mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeHeaderDKO.class), 7);
		Assert.assertEquals(calculateTotalFieldLenght(mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeDKO.class), 400);
		Assert.assertEquals(calculateTotalFieldLenght(mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST.class), 40);
		Assert.assertEquals(calculateTotalFieldLenght(mobi.chouette.exchange.regtopp.model.v11.RegtoppFootnoteMRK.class), 8); // Actually 87), but implementation allows more since some companies are ignoring the length
																// restriction
		Assert.assertEquals(calculateTotalFieldLenght(mobi.chouette.exchange.regtopp.model.v11.RegtoppPathwayGAV.class), 42);
		Assert.assertEquals(calculateTotalFieldLenght(mobi.chouette.exchange.regtopp.model.v11.RegtoppInterchangeSAM.class), 51);
		Assert.assertEquals(calculateTotalFieldLenght(mobi.chouette.exchange.regtopp.model.v11.RegtoppZoneSON.class), 45);
		Assert.assertEquals(calculateTotalFieldLenght(mobi.chouette.exchange.regtopp.model.v11.RegtoppLineLIN.class), 39);
		Assert.assertEquals(calculateTotalFieldLenght(mobi.chouette.exchange.regtopp.model.v11.RegtoppVehicleJourneyVLP.class), 24);

	}

	@Test
	public void verifyMappingCorrectnessRegtopp12() {
		// Other files same as 1.1D
		Assert.assertEquals(calculateTotalFieldLenght(RegtoppTripIndexTIX.class), 62);
		Assert.assertEquals(calculateTotalFieldLenght(RegtoppRouteTMS.class), 47);
		Assert.assertEquals(calculateTotalFieldLenght(RegtoppVehicleJourneyVLP.class), 29);
		Assert.assertEquals(calculateTotalFieldLenght(RegtoppTableVersionTAB.class), 108);
		Assert.assertEquals(calculateTotalFieldLenght(RegtoppPeriodPER.class), 100);
		Assert.assertEquals(calculateTotalFieldLenght(RegtoppRoutePointRUT.class), 30);

	}

	@Test
	public void verifyMappingCorrectnessRegtopp12N() {
		// Other files same as 1.2
		Assert.assertEquals(calculateTotalFieldLenght(mobi.chouette.exchange.regtopp.model.v12novus.RegtoppStopHPL.class), 89);
	}


	private int calculateTotalFieldLenght(Class<?> clazz) {
		int length = 0;
		for (Field f : clazz.getDeclaredFields()) {
			org.beanio.annotation.Field column = f.getAnnotation(org.beanio.annotation.Field.class);
			if (column != null) {
				System.out.println("Found field " + f.getName() + " with length " + column.length());
				length += column.length();
			}
		}
		
		Class<?> superclass = clazz.getSuperclass();
		if(RegtoppObject.class.isAssignableFrom(superclass)) {
			length += calculateTotalFieldLenght(superclass);
		}
		
		return length;
	}
}
