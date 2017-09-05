package mobi.chouette.exchange.netexprofile;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.report.*;
import mobi.chouette.model.*;
import mobi.chouette.model.util.Referential;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static mobi.chouette.common.Constant.VALIDATION_REPORT;

@Log4j
public class NetexTestUtils  {

	protected static final String path = "src/test/data";

	public static Codespace createCodespace(Long id, String xmlns, String xmlnsUrl) {
		Codespace codespace = new Codespace();
		if (id != null)
			codespace.setId(id);
		codespace.setXmlns(xmlns);
		codespace.setXmlnsUrl(xmlnsUrl);
		codespace.setCreatedAt(new Date());
		codespace.setUpdatedAt(new Date());
		return codespace;
	}

	public static  void copyFile(String fileName) throws IOException {
		File srcFile = new File(path, fileName);
		File destFile = new File("target/referential/test", fileName);
		FileUtils.copyFile(srcFile, destFile);
	}

	public static void checkLine(Context context) {
		Referential referential = (Referential) context.get(Constant.REFERENTIAL);
		Assert.assertNotNull(referential, "referential");
		Assert.assertEquals(referential.getLines().size(), 1, "lines size");

		Line line = referential.getLines().get("AVI:Line:WF_TRD-MOL");
		Assert.assertNotNull(line, "line");
		Assert.assertNotNull(line.getNetwork(), "line must have a network");
		Assert.assertNotNull(line.getCompany(), "line must have a company");
		Assert.assertNotNull(line.getRoutes(), "line must have routes");
		Assert.assertEquals(line.getRoutes().size(), 2, "line must have 2 routes");

		Set<StopArea> bps = new HashSet<>();

		for (Route route : line.getRoutes()) {
			Assert.assertNotEquals(route.getJourneyPatterns().size(), 0, "line routes must have journeyPattens");

			for (JourneyPattern jp : route.getJourneyPatterns()) {
				Assert.assertNotEquals(jp.getStopPoints().size(), 0, "line journeyPattens must have stoppoints");

				for (StopPoint point : jp.getStopPoints()) {
					Assert.assertNotNull(point.getContainedInStopArea(), "stoppoints must have StopAreas");
					bps.add(point.getContainedInStopArea());
				}

				Assert.assertNotEquals(jp.getVehicleJourneys().size(), 0, " journeyPattern should have VehicleJourneys");

				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					Assert.assertNotEquals(vj.getTimetables().size(), 0, " vehicleJourney should have timetables");
					Assert.assertEquals(vj.getVehicleJourneyAtStops().size(), jp.getStopPoints().size(), " vehicleJourney should have correct vehicleJourneyAtStop count");
				}
			}
		}

		Assert.assertEquals(bps.size(), 2, "line must have 2 stop areas");
	}

	public static void verifyLine(Context context, String lineId, int numOfRoutes, int numOfStopAreas) {
		Referential referential = (Referential) context.get(Constant.REFERENTIAL);
		Assert.assertNotNull(referential, "referential");
		Assert.assertEquals(referential.getLines().size(), 1, "lines size");

		Line line = referential.getLines().get(lineId);
		Assert.assertNotNull(line, "line");
		Assert.assertNotNull(line.getNetwork(), "line must have a network");
		Assert.assertNotNull(line.getCompany(), "line must have a company");
		Assert.assertNotNull(line.getRoutes(), "line must have routes");
		Assert.assertEquals(line.getRoutes().size(), numOfRoutes, "line must have " + numOfRoutes + " routes");

		Set<StopArea> bps = new HashSet<>();

		for (Route route : line.getRoutes()) {
			Assert.assertNotEquals(route.getJourneyPatterns().size(), 0, "line routes must have journeyPattens");

			for (JourneyPattern jp : route.getJourneyPatterns()) {
				Assert.assertNotEquals(jp.getStopPoints().size(), 0, "line journeyPattens must have stoppoints");

				for (StopPoint point : jp.getStopPoints()) {
					Assert.assertNotNull(point.getContainedInStopArea(), "stoppoints must have StopAreas");
					bps.add(point.getContainedInStopArea());
				}

				Assert.assertNotEquals(jp.getVehicleJourneys().size(), 0, " journeyPattern should have VehicleJourneys");

				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					Assert.assertNotEquals(vj.getTimetables().size(), 0, " vehicleJourney should have timetables");
					Assert.assertEquals(vj.getVehicleJourneyAtStops().size(), jp.getStopPoints().size(), " vehicleJourney should have correct vehicleJourneyAtStop count");
				}
			}
		}

		Assert.assertEquals(bps.size(), numOfStopAreas, "line must have " + numOfStopAreas + " stop areas");
	}

	public static void verifyValidationReport(Context context) {
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);

		for (CheckPointReport checkPoint : validationReport.getCheckPoints()) {
			if (checkPoint.getState().equals(ValidationReporter.RESULT.NOK)) {
				Reporter.log(checkPoint.toString(), true);
			}
		}

		Assert.assertFalse(validationReport.getCheckPoints().isEmpty(),"validation report should not be empty");
		Reporter.log("validation report size :" + validationReport.getCheckPoints().size(), true);
		Reporter.log("validation error report size :" + validationReport.getCheckPointErrors().size(), true);

		int errorCount = 0;

		for (CheckPointErrorReport checkPointError : validationReport.getCheckPointErrors()) {
			CheckPointReport checkPoint = validationReport.findCheckPointReportByName(checkPointError.getTestId());
			Location sourceLocation = checkPointError.getSource();
			FileLocation fileLocation = sourceLocation.getFile();

			String logMessage = "Validation checkpoint : " + checkPointError.getTestId() + " failed for objectId : " + sourceLocation.getObjectId()+". Error value: "+checkPointError.getValue()+ " ReferenceValue: "+checkPointError.getReferenceValue()
					+ " at file location : " + fileLocation.getFilename() + ", Line " + fileLocation.getLineNumber() + ", Column " + fileLocation.getColumnNumber();

			if (checkPoint.getSeverity().equals(CheckPointReport.SEVERITY.ERROR)) {
				Assert.fail(logMessage);
				errorCount++;
			} else {
				log.warn(logMessage);
			}
		}

		Reporter.log("number of validation errors : " + errorCount, true);
		ValidationReporter.VALIDATION_RESULT result = validationReport.getResult();
		Assert.assertTrue(result.equals(ValidationReporter.VALIDATION_RESULT.OK) || result.equals(ValidationReporter.VALIDATION_RESULT.WARNING), "validation report status");
	}

}
