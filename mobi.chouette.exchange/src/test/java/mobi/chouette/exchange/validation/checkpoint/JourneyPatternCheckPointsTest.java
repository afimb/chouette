package mobi.chouette.exchange.validation.checkpoint;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.DestinationDisplay;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.SimpleObjectReference;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;

import static mobi.chouette.model.type.TransportModeNameEnum.*;
import static mobi.chouette.model.type.TransportSubModeNameEnum.*;

import mobi.chouette.model.type.*;

public class JourneyPatternCheckPointsTest {

	@Test
	public void check3JourneyPattern3Fail() {
		JourneyPatternCheckPoints checkPoints = new JourneyPatternCheckPoints();
		Context context = createValidationContext(AbstractValidation.JOURNEY_PATTERN_3);

		checkPoints.check3JourneyPattern3(context, createJourneyPattern(Bus, null, Tram, null));
		checkPoints.check3JourneyPattern3(context, createJourneyPattern(Bus, null, Bus, RailReplacementBus));
		Assert.assertEquals(((ValidationReport) context.get(Constant.VALIDATION_REPORT)).getCheckPointErrors().size(), 2);
	}

	@Test
	public void check3JourneyPattern3OK() {
		JourneyPatternCheckPoints checkPoints = new JourneyPatternCheckPoints();
		Context context = createValidationContext(AbstractValidation.JOURNEY_PATTERN_3);

		checkPoints.check3JourneyPattern3(context, createJourneyPattern(Bus, null, Coach, null));
		checkPoints.check3JourneyPattern3(context, createJourneyPattern(Bus, RailReplacementBus, Bus, RailReplacementBus));
		checkPoints.check3JourneyPattern3(context, createJourneyPattern(Bus, RailReplacementBus, Bus, null));
		Assert.assertEquals(((ValidationReport) context.get(Constant.VALIDATION_REPORT)).getCheckPointErrors().size(), 0);
	}


	@Test
	public void check3JourneyPatternRb3_sameStopSequenceSameDestinationDisplaysAndSameBoardingAlighting_givesWarning() {
		JourneyPatternCheckPoints checkPoints = new JourneyPatternCheckPoints();
		Context context = createValidationContext(AbstractValidation.JOURNEY_PATTERN_RB_3);

		DestinationDisplay dd1 = new DestinationDisplay();
		dd1.setObjectId("id");
		JourneyPattern jp1 = createJourneyPattern(createSP("1", dd1), createSP("2", null));
		JourneyPattern jp2 = createJourneyPattern(createSP("1", dd1), createSP("2", null));

		checkPoints.check3JourneyPatternRb3(context, jp1, jp2);
		Assert.assertEquals(((ValidationReport) context.get(Constant.VALIDATION_REPORT)).getCheckPointErrors().size(), 1);
	}


	@Test
	public void check3JourneyPatternRb3_sameStopSequenceDifferentDestinationDisplays_givesNoWarning() {
		JourneyPatternCheckPoints checkPoints = new JourneyPatternCheckPoints();
		Context context = createValidationContext(AbstractValidation.JOURNEY_PATTERN_RB_3);

		DestinationDisplay dd1 = new DestinationDisplay();
		dd1.setObjectId("id");
		DestinationDisplay dd2 = new DestinationDisplay();
		dd2.setObjectId("differentId");
		JourneyPattern jp1 = createJourneyPattern(createSP("1", dd1), createSP("2", null));
		JourneyPattern jp2 = createJourneyPattern(createSP("1", dd2), createSP("2", null));

		checkPoints.check3JourneyPatternRb3(context, jp1, jp2);
		Assert.assertEquals(((ValidationReport) context.get(Constant.VALIDATION_REPORT)).getCheckPointErrors().size(), 0);
	}

	@Test
	public void check3JourneyPatternRb3_sameStopSequenceDifferentBoardingAlighting_givesNoWarning() {
		JourneyPatternCheckPoints checkPoints = new JourneyPatternCheckPoints();
		Context context = createValidationContext(AbstractValidation.JOURNEY_PATTERN_RB_3);

		DestinationDisplay dd1 = new DestinationDisplay();
		dd1.setObjectId("id");
		JourneyPattern jp1 = createJourneyPattern(createSP("1", dd1), createSP("2", null));
		JourneyPattern jp2 = createJourneyPattern(createSP("1", dd1), createSP("2", null, BoardingPossibilityEnum.normal,AlightingPossibilityEnum.forbidden));

		checkPoints.check3JourneyPatternRb3(context, jp1, jp2);
		Assert.assertEquals(((ValidationReport) context.get(Constant.VALIDATION_REPORT)).getCheckPointErrors().size(), 0);
	}

	@Test
	public void check3JourneyPatternRb3_differentStopSequenceGivesNoWarning() {
		JourneyPatternCheckPoints checkPoints = new JourneyPatternCheckPoints();
		Context context = createValidationContext(AbstractValidation.JOURNEY_PATTERN_RB_3);

		DestinationDisplay dd1 = new DestinationDisplay();
		dd1.setObjectId("id");
		JourneyPattern jp1 = createJourneyPattern(createSP("1", dd1), createSP("2", null));
		JourneyPattern jp2 = createJourneyPattern(createSP("1", dd1), createSP("3", null));

		checkPoints.check3JourneyPatternRb3(context, jp1, jp2);
		Assert.assertEquals(((ValidationReport) context.get(Constant.VALIDATION_REPORT)).getCheckPointErrors().size(), 0);

		jp2 = createJourneyPattern(createSP("1", dd1), createSP("2", null), createSP("3", null));

		checkPoints.check3JourneyPatternRb3(context, jp1, jp2);
		Assert.assertEquals(((ValidationReport) context.get(Constant.VALIDATION_REPORT)).getCheckPointErrors().size(), 0);
	}


	private StopPoint createSP(String stopAreaId, DestinationDisplay dd) {
		return createSP(stopAreaId, dd, BoardingPossibilityEnum.normal, AlightingPossibilityEnum.normal);
	}

	private StopPoint createSP(String stopAreaId, DestinationDisplay dd, BoardingPossibilityEnum forBoarding, AlightingPossibilityEnum forAlighting) {
		StopPoint sp = new StopPoint();
		ScheduledStopPoint ssp = new ScheduledStopPoint();
		StopArea stopArea = new StopArea();
		stopArea.setObjectId(stopAreaId);
		ssp.setContainedInStopAreaRef(new SimpleObjectReference(stopArea));
		sp.setScheduledStopPoint(ssp);
		sp.setDestinationDisplay(dd);
		sp.setForBoarding(forBoarding);
		sp.setForAlighting(forAlighting);
		return sp;
	}

	private JourneyPattern createJourneyPattern(StopPoint... stopPoints) {
		JourneyPattern journeyPattern = new JourneyPattern();
		if (stopPoints != null) {
			journeyPattern.setStopPoints(Arrays.asList(stopPoints));
		}
		return journeyPattern;
	}


	protected Context createValidationContext(String checkPointName) {
		Context context = new Context();
		ValidationReport validationReport = new ValidationReport();
		context.put(Constant.VALIDATION_REPORT, validationReport);
		ValidationReporter reporter = ValidationReporter.Factory.getInstance();
		reporter.addItemToValidationReport(context, checkPointName, "E");
		return context;
	}

	private JourneyPattern createJourneyPattern(TransportModeNameEnum lineMode, TransportSubModeNameEnum lineSubMode, TransportModeNameEnum stopMode, TransportSubModeNameEnum stopSubMode) {
		StopArea sa = new StopArea();

		sa.setTransportModeName(stopMode);
		sa.setTransportSubMode(stopSubMode);

		JourneyPattern jp = new JourneyPattern();
		StopPoint sp = new StopPoint();
		ScheduledStopPoint ssp = new ScheduledStopPoint();
		sp.setScheduledStopPoint(ssp);
		ssp.setContainedInStopAreaRef(new SimpleObjectReference<>(sa));
		jp.getStopPoints().add(sp);

		Route r = new Route();
		jp.setRoute(r);


		Line l = new Line();
		r.setLine(l);

		l.setTransportModeName(lineMode);
		l.setTransportSubModeName(lineSubMode);


		return jp;
	}

}
