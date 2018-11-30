package mobi.chouette.exchange.validation.checkpoint;

import java.util.Arrays;
import java.util.List;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import static mobi.chouette.common.Constant.VALIDATION_REPORT;
import static mobi.chouette.exchange.validation.checkpoint.AbstractValidation.INTERCHANGE_9_1;
import static mobi.chouette.exchange.validation.checkpoint.AbstractValidation.INTERCHANGE_9_2;

public class InterchangeCheckPointsTest {

	InterchangeCheckPoints interchangeCheckPoints = new InterchangeCheckPoints();

	Interchange noRelationsInterchange = interchange("1", "fs1", "cs1", "fvj1", "cvj1");

	@Test
	public void testFindDuplicates() {


		Interchange org1 = interchange("org1", "feederStop", "consumerStop", "feederJourney", "consumerJourney");
		Interchange notDup1 = interchange("notDup1", "feederStop", "consumerStop", "feederJourney", "consumerJourneyOther");
		Interchange notDup2 = interchange("notDup2", "feederStop", "consumerStop", "feederJourneyOther", "consumerJourney");
		Interchange notDup3 = interchange("notDup3", "feederStop", "consumerStopOther", "feederJourney", "consumerJourney");
		Interchange notDup4 = interchange("notDup4", "feederStopOther", "consumerStop", "feederJourney", "consumerJourney");
		Interchange org1dup1 = interchange("org1dup1", "feederStop", "consumerStop", "feederJourney", "consumerJourney");
		Interchange org1dup2 = interchange("org1dup2", "feederStop", "consumerStop", "feederJourney", "consumerJourney");
		Interchange org2 = interchange("org2", "feederStop2", "consumerStop2", "feederJourney2", "consumerJourney2");
		Interchange org2dup = interchange("org2dup", "feederStop2", "consumerStop2", "feederJourney2", "consumerJourney2");


		List<Interchange> interchanges = Arrays.asList(org1, org2, notDup1, notDup2, notDup3, notDup4, org1dup1, org1dup2, org2dup);


		List<Pair<Interchange, Interchange>> duplicates = interchangeCheckPoints.findDuplicates(interchanges);

		Assert.assertEquals(3, duplicates.size());


		Assert.assertTrue(duplicates.stream().anyMatch(d -> d.getLeft().equals(org1) && d.getRight().equals(org1dup1)));
		Assert.assertTrue(duplicates.stream().anyMatch(d -> d.getLeft().equals(org1) && d.getRight().equals(org1dup2)));
		Assert.assertTrue(duplicates.stream().anyMatch(d -> d.getLeft().equals(org2) && d.getRight().equals(org2dup)));
	}


	@Test
	public void checkFeederAlighting_whenUnknownStop_thenIgnore() {
		Context context = createContext();
		interchangeCheckPoints.checkFeederAlighting(context, noRelationsInterchange, null);
		assertNoErrors(context);
	}

	@Test
	public void checkFeederAlighting_whenAlightingAllowed_thenIgnore() {
		Context context = createContext();
		VehicleJourneyAtStop vJAS = vehicleJourneyAtStop(AlightingPossibilityEnum.request_stop);
		interchangeCheckPoints.checkFeederAlighting(context, noRelationsInterchange, vJAS);
		assertNoErrors(context);
	}

	@Test
	public void checkFeederAlighting_whenNoAlighting_thenAddError() {
		Context context = createContext();
		VehicleJourneyAtStop vJAS = vehicleJourneyAtStop(AlightingPossibilityEnum.forbidden);
		interchangeCheckPoints.checkFeederAlighting(context, noRelationsInterchange, vJAS);
		assertSingleError(context, INTERCHANGE_9_1);
	}


	@Test
	public void checkConsumerBoarding_whenUnknownStop_thenIgnore() {
		Context context = createContext();
		interchangeCheckPoints.checkConsumerBoarding(context, noRelationsInterchange, null);
		assertNoErrors(context);
	}

	@Test
	public void checkConsumerBoarding_whenBoardingAllowed_thenIgnore() {
		Context context = createContext();
		VehicleJourneyAtStop vJAS = vehicleJourneyAtStop(BoardingPossibilityEnum.request_stop);
		interchangeCheckPoints.checkConsumerBoarding(context, noRelationsInterchange, vJAS);
		assertNoErrors(context);
	}

	@Test
	public void checkConsumerBoarding_whenNoBoarding_thenAddError() {
		Context context = createContext();
		VehicleJourneyAtStop vJAS = vehicleJourneyAtStop(BoardingPossibilityEnum.forbidden);
		interchangeCheckPoints.checkConsumerBoarding(context, noRelationsInterchange, vJAS);
		assertSingleError(context, INTERCHANGE_9_2);
	}


	private Context createContext() {
		Context context = new Context();
		context.put(VALIDATION_REPORT, new ValidationReport());
		interchangeCheckPoints.initCheckPoints(context);
		return context;
	}


	private VehicleJourneyAtStop vehicleJourneyAtStop(BoardingPossibilityEnum boarding) {
		VehicleJourneyAtStop vJAS = new VehicleJourneyAtStop();
		StopPoint stopPoint = new StopPoint();
		stopPoint.setForBoarding(boarding);
		vJAS.setStopPoint(stopPoint);
		return vJAS;
	}

	private VehicleJourneyAtStop vehicleJourneyAtStop(AlightingPossibilityEnum alighting) {
		VehicleJourneyAtStop vJAS = new VehicleJourneyAtStop();
		StopPoint stopPoint = new StopPoint();
		stopPoint.setForAlighting(alighting);
		vJAS.setStopPoint(stopPoint);
		return vJAS;
	}

	private void assertNoErrors(Context context) {
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertTrue(validationReport.getCheckPointErrors().isEmpty());
	}

	private void assertSingleError(Context context, String checkPoint) {
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		Assert.assertEquals(1, validationReport.getCheckPointErrors().size());
		Assert.assertEquals(checkPoint.replace("-", "_").toLowerCase(), validationReport.getCheckPointErrors().get(0).getKey());
	}

	private Interchange interchange(String objectId, String feederStopId, String consumerStopId, String feederJourneyId, String consumerJourneyId) {
		Interchange interchange = new Interchange();
		interchange.setObjectId(objectId);
		interchange.setFeederStopPointObjectid(feederStopId);
		interchange.setConsumerStopPointObjectid(consumerStopId);
		interchange.setFeederVehicleJourneyObjectid(feederJourneyId);
		interchange.setConsumerVehicleJourneyObjectid(consumerJourneyId);
		return interchange;
	}


}
