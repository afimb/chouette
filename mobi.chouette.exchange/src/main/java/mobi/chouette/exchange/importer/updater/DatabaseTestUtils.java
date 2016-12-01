package mobi.chouette.exchange.importer.updater;

import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.TestDescription;

public class DatabaseTestUtils {
protected List<TestDescription> databaseTestList = null;
	
	private static DatabaseTestUtils singleton = null;
	
	private DatabaseTestUtils() {
			databaseTestList = new ArrayList<TestDescription>();
			
			databaseTestList.add(new TestDescription(2, "2-DATABASE-Line-1", "WARNING"));
			databaseTestList.add(new TestDescription(2, "2-DATABASE-Line-1", "WARNING"));
			
			databaseTestList.add(new TestDescription(2, "2-DATABASE-Route-1", "ERROR"));
			databaseTestList.add(new TestDescription(2, "2-DATABASE-JourneyPattern-1", "ERROR"));
			
			databaseTestList.add(new TestDescription(2, "2-DATABASE-VehicleJourney-1", "ERROR"));
			databaseTestList.add(new TestDescription(2, "2-DATABASE-VehicleJourney-2", "WARNING"));
			
			databaseTestList.add(new TestDescription(2, "2-DATABASE-StopPoint-1", "ERROR"));
			databaseTestList.add(new TestDescription(2, "2-DATABASE-StopPoint-2", "ERROR"));
			databaseTestList.add(new TestDescription(2, "2-DATABASE-StopPoint-3", "WARNING"));
			
			databaseTestList.add(new TestDescription(2, "2-DATABASE-StopArea-1", "WARNING"));
			databaseTestList.add(new TestDescription(2, "2-DATABASE-StopArea-2", "ERROR"));
			
			databaseTestList.add(new TestDescription(2, "2-DATABASE-AccessPoint-1", "ERROR"));
			
			databaseTestList.add(new TestDescription(2, "2-DATABASE-ConnectionLink-1", "ERROR"));
// 			databaseTestList.add(new TestDescription(2, "2-DATABASE-ConnectionLink-2", "ERROR"));
	}
	
	
	public List<TestDescription> getTestUtilsList() {
		return databaseTestList;
	}
	
	public static DatabaseTestUtils getInstance() {
		if(singleton == null) {
			singleton = new DatabaseTestUtils();
		}
		
		return singleton;
	}
}
