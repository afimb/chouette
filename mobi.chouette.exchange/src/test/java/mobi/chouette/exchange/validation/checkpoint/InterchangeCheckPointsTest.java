package mobi.chouette.exchange.validation.checkpoint;

import java.util.Arrays;
import java.util.List;

import mobi.chouette.model.Interchange;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

public class InterchangeCheckPointsTest {


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

		List<Pair<Interchange, Interchange>> duplicates = new InterchangeCheckPoints().findDuplicates(interchanges);

		Assert.assertEquals(3, duplicates.size());


		Assert.assertTrue(duplicates.stream().anyMatch(d -> d.getLeft().equals(org1) && d.getRight().equals(org1dup1)));
		Assert.assertTrue(duplicates.stream().anyMatch(d -> d.getLeft().equals(org1) && d.getRight().equals(org1dup2)));
		Assert.assertTrue(duplicates.stream().anyMatch(d -> d.getLeft().equals(org2) && d.getRight().equals(org2dup)));
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
