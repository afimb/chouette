package mobi.chouette.exchange.validator;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.Referential;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;

public class TestsUtils implements Constant, ReportConstant{

	

	protected static final String path = "src/test/data";
	public static  void copyFile(String fileName) throws IOException {
		File srcFile = new File(path, fileName);
		File destFile = new File("target/referential/test", fileName);
		FileUtils.copyFile(srcFile, destFile);
	}

	public static void checkLine(Context context)
	{
		
		// checl line content before save (cause connection links could not be saved
		Referential referential = (Referential) context.get(REFERENTIAL);
		Assert.assertNotNull(referential, "referential");
		Assert.assertEquals(referential.getLines().size(), 1, "lines size");
		Line line = referential.getLines().get("NINOXE:Line:15574334");
		Assert.assertNotNull(line, "line");

		// comptage des objets :
		Assert.assertNotNull(line.getNetwork(), "line must have a network");
		Assert.assertNotNull(line.getGroupOfLines(), "line must have groupOfLines");
		Assert.assertEquals(line.getGroupOfLines().size(), 1, "line must have 1 groupOfLine");
		Assert.assertNotNull(line.getCompany(), "line must have a company");
		Assert.assertNotNull(line.getRoutes(), "line must have routes");
		Assert.assertEquals(line.getRoutes().size(), 4, "line must have 4 routes");
		Set<StopArea> bps = new HashSet<StopArea>();
		Set<StopArea> comms = new HashSet<StopArea>();

		for (Route route : line.getRoutes()) {
			Assert.assertNotNull(route.getJourneyPatterns(), "line routes must have journeyPattens");
			for (JourneyPattern jp : route.getJourneyPatterns()) {
				Assert.assertNotNull(jp.getStopPoints(), "line journeyPattens must have stoppoints");
				for (StopPoint point : jp.getStopPoints()) {

					Assert.assertNotNull(point.getContainedInStopArea(), "stoppoints must have StopAreas");
					bps.add(point.getContainedInStopArea());

					Assert.assertNotNull(point.getContainedInStopArea().getParent(), "StopAreas must have parent : "
							+ point.getContainedInStopArea().getObjectId());
					comms.add(point.getContainedInStopArea().getParent());
				}
			}
		}
		Assert.assertEquals(bps.size(), 18, "line must have 18 boarding positions");
		Assert.assertEquals(comms.size(), 9, "line must have 9 commercial stop points");

		Set<ConnectionLink> clinks = new HashSet<ConnectionLink>();
		Set<AccessLink> alinks = new HashSet<AccessLink>();

		for (StopArea comm : comms) {

			if (comm.getConnectionEndLinks() != null) {
				clinks.addAll(comm.getConnectionEndLinks());
			}
			if (comm.getConnectionStartLinks() != null) {
				clinks.addAll(comm.getConnectionStartLinks());
			}
			if (comm.getAccessLinks() != null) {
				alinks.addAll(comm.getAccessLinks());
			}
		}
		Assert.assertEquals(clinks.size(), 2, "line must have 2 connection link");
		Calendar c = Calendar.getInstance();
		for (ConnectionLink connectionLink : clinks) {

			c.setTimeInMillis(connectionLink.getDefaultDuration().getTime());
			int minutes = c.get(Calendar.MINUTE);
			int hours = c.get(Calendar.HOUR_OF_DAY);
			int seconds = c.get(Calendar.SECOND) + minutes * 60 + hours * 3600;

			Assert.assertEquals(seconds, 4200, "line must have links duration of 1 hour and 10 minutes");
			// Reporter.log(connectionLink.toString("\t", 1));

		}
		Assert.assertEquals(alinks.size(), 1, "line must have 1 access link");

		Set<AccessPoint> apoints = new HashSet<AccessPoint>();

		for (AccessLink accessLink : alinks) {
			c.setTimeInMillis(accessLink.getDefaultDuration().getTime());
			int minutes = c.get(Calendar.MINUTE);
			int hours = c.get(Calendar.HOUR_OF_DAY);
			int seconds = c.get(Calendar.SECOND) + minutes * 60 + hours * 3600;

			Assert.assertEquals(seconds, 60, "line must have links duration of 1 minutes");
			// Reporter.log(accessLink.toString("\t", 1));
			apoints.add(accessLink.getAccessPoint());

		}
		Assert.assertEquals(apoints.size(), 1, "line must have 1 access point");
		for (AccessPoint accessPoint : apoints) {
			c.setTimeInMillis(accessPoint.getOpeningTime().getTime());
			int minutes = c.get(Calendar.MINUTE);
			int hours = c.get(Calendar.HOUR_OF_DAY);
			int seconds = c.get(Calendar.SECOND) + minutes * 60 + hours * 3600;

			Assert.assertEquals(seconds, 6 * 3600, "line must have opening time of 6 hours");
			c.setTimeInMillis(accessPoint.getClosingTime().getTime());
			minutes = c.get(Calendar.MINUTE);
			hours = c.get(Calendar.HOUR_OF_DAY);
			seconds = c.get(Calendar.SECOND) + minutes * 60 + hours * 3600;

			Assert.assertEquals(seconds, 22 * 3600 + 600, "line must have closing time of 22 hours 10");

		}

	}

}
