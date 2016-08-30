package mobi.chouette.exchange.importer.updater;

import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.Test;

public class TestUtils {
protected List<Test> databaseTestList = null;
	
	private static TestUtils singleton = null;
	
	private TestUtils() {
			databaseTestList = new ArrayList<Test>();
			
			databaseTestList.add(new Test(2, "2-DATABASE-Line-1", "WARNING"));
			databaseTestList.add(new Test(2, "2-DATABASE-Line-1", "WARNING"));
			
			databaseTestList.add(new Test(2, "2-DATABASE-Route-1", "ERROR"));
			databaseTestList.add(new Test(2, "2-DATABASE-JourneyPattern-1", "ERROR"));
			
			databaseTestList.add(new Test(2, "2-DATABASE-VehicleJourney-1", "ERROR"));
			databaseTestList.add(new Test(2, "2-DATABASE-VehicleJourney-2", "WARNING"));
			
			databaseTestList.add(new Test(2, "2-DATABASE-StopPoint-1", "ERROR"));
			databaseTestList.add(new Test(2, "2-DATABASE-StopPoint-2", "ERROR"));
			databaseTestList.add(new Test(2, "2-DATABASE-StopPoint-3", "WARNING"));
			
			databaseTestList.add(new Test(2, "2-DATABASE-StopArea-1", "WARNING"));
			databaseTestList.add(new Test(2, "2-DATABASE-StopArea-2", "ERROR"));
			
			databaseTestList.add(new Test(2, "2-DATABASE-AccessPoint-1", "ERROR"));
			
			databaseTestList.add(new Test(2, "2-DATABASE-ConnectionLink-1-1", "ERROR"));
			databaseTestList.add(new Test(2, "2-DATABASE-ConnectionLink-1-2", "ERROR"));
	}
	
	
	public List<Test> getTestUtilsList() {
		return databaseTestList;
	}
	
	public static TestUtils getInstance() {
		if(singleton == null) {
			singleton = new TestUtils();
		}
		
		return singleton;
	}
}
