package mobi.chouette.exchange.gtfs.validation;

import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.TestDescription;

public class TestUtils implements Constant{
	protected List<TestDescription> gtfsTestList = null;
	
	private static TestUtils singleton = null;
	
	private TestUtils() {
			gtfsTestList = new ArrayList<TestDescription>();
			
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_CSV_1, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_CSV_2, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_CSV_3, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_CSV_4, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_CSV_5, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_CSV_6, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_CSV_7, "WARNING"));
			
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_1, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_2, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_3, "WARNING"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_4, "WARNING"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_5, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_6, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_7, "WARNING"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_8, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_9, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_10, "WARNING"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_10, "WARNING"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_11, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_12, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_13, "WARNING"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_14, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Common_15, "ERROR"));
			
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Calendar_1, "WARNING"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Calendar_2, "ERROR"));
			
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Route_1, "ERROR"));
			gtfsTestList.add(new TestDescription(1, GTFS_1_GTFS_Route_2, "ERROR"));
			
			gtfsTestList.add(new TestDescription(2, GTFS_2_GTFS_Common_1, "ERROR"));
			gtfsTestList.add(new TestDescription(2, GTFS_2_GTFS_Common_2, "WARNING"));
			gtfsTestList.add(new TestDescription(2, GTFS_2_GTFS_Common_3, "ERROR"));
			gtfsTestList.add(new TestDescription(2, GTFS_2_GTFS_Common_4, "WARNING"));
			
			gtfsTestList.add(new TestDescription(2, GTFS_2_GTFS_Stop_1, "ERROR"));
			gtfsTestList.add(new TestDescription(2, GTFS_2_GTFS_Stop_2, "WARNING"));
			gtfsTestList.add(new TestDescription(2, GTFS_2_GTFS_Stop_3, "ERROR"));
			gtfsTestList.add(new TestDescription(2, GTFS_2_GTFS_Stop_4, "ERROR"));
			
			gtfsTestList.add(new TestDescription(2, GTFS_2_GTFS_Route_1, "WARNING"));
			gtfsTestList.add(new TestDescription(2, GTFS_2_GTFS_Route_2, "WARNING"));
			gtfsTestList.add(new TestDescription(2, GTFS_2_GTFS_Route_3, "WARNING"));
			gtfsTestList.add(new TestDescription(2, GTFS_2_GTFS_Route_4, "WARNING"));	
	}
	
	
	public List<TestDescription> getTestUtilsList() {
		return gtfsTestList;
	}
	
	public static TestUtils getInstance() {
		if(singleton == null) {
			singleton = new TestUtils();
		}
		
		return singleton;
	}
}
