package mobi.chouette.exchange.netexprofile;

import mobi.chouette.common.Context;
import mobi.chouette.model.*;
import mobi.chouette.model.util.Referential;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class NetexTestUtils  {

	protected static final String path = "src/test/data";

	public static  void copyFile(String fileName) throws IOException {
		File srcFile = new File(path, fileName);
		File destFile = new File("target/referential/test", fileName);
		FileUtils.copyFile(srcFile, destFile);
	}

	public static void checkLine(Context context) {
		// check line content before save (cause connection links could not be saved
		Referential referential = (Referential) context.get(Constant.REFERENTIAL);
		Assert.assertNotNull(referential, "referential");
		Assert.assertEquals(referential.getLines().size(), 1, "lines size");
		Line line = referential.getLines().get("AVI:Line:DY280");
		Assert.assertNotNull(line, "line");

		Assert.assertNotNull(line.getNetwork(), "line must have a network");

		//Assert.assertNotNull(line.getGroupOfLines(), "line must have groupOfLines");
		//Assert.assertEquals(line.getGroupOfLines().size(), 1, "line must have 1 groupOfLine");
		Assert.assertNotNull(line.getCompany(), "line must have a company");
		Assert.assertNotNull(line.getRoutes(), "line must have routes");
		Assert.assertEquals(line.getRoutes().size(), 1, "line must have 1 route");

		Set<StopArea> bps = new HashSet<StopArea>();
		Set<StopArea> comms = new HashSet<StopArea>();

		for (Route route : line.getRoutes()) {
			Assert.assertNotEquals(route.getJourneyPatterns().size(), 0, "line routes must have journeyPattens");

			for (JourneyPattern jp : route.getJourneyPatterns()) {
				Assert.assertNotEquals(jp.getStopPoints().size(), 0, "line journeyPattens must have stoppoints");

				for (StopPoint point : jp.getStopPoints()) {

					Assert.assertNotNull(point.getContainedInStopArea(), "stoppoints must have StopAreas");
					bps.add(point.getContainedInStopArea());

/*
					Assert.assertNotNull(point.getContainedInStopArea().getParent(), "StopAreas must have a parent : "
							+ point.getContainedInStopArea().getObjectId());
					comms.add(point.getContainedInStopArea().getParent());
*/
				}

				Assert.assertNotEquals(jp.getVehicleJourneys().size(), 0, " journeyPattern should have VehicleJourneys");

				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					Assert.assertNotEquals(vj.getTimetables().size(), 0, " vehicleJourney should have timetables");
					Assert.assertEquals(vj.getVehicleJourneyAtStops().size(), jp.getStopPoints().size(), " vehicleJourney should have correct vehicleJourneyAtStop count");
				}
			}
		}

		//Assert.assertEquals(bps.size(), 2, "line must have 2 boarding positions");
		Assert.assertEquals(bps.size(), 2, "line must have 2 stop areas");
		//Assert.assertEquals(comms.size(), 2, "line must have 2 commercial stop points"); // connected to parent stop areas, disabled for aviation data
	}

}
