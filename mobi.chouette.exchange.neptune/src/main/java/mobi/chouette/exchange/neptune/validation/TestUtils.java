package mobi.chouette.exchange.neptune.validation;

import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.TestDescription;

public class TestUtils{
	protected List<TestDescription> neptuneTestList = null;
	
	private static TestUtils singleton = null;
	
	private TestUtils() {
			neptuneTestList = new ArrayList<TestDescription>();
			
			neptuneTestList.add(new TestDescription(1, "1-NEPTUNE-XML-1", "ERROR"));
			neptuneTestList.add(new TestDescription(1, "1-NEPTUNE-XML-2", "WARNING"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Common-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Common-2", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Network-1", "WARNING"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Network-2", "WARNING"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-GroupOfLine-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-StopArea-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-StopArea-2", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-StopArea-3", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-StopArea-4", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-StopArea-5", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-StopArea-6", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-ITL-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-ITL-2", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-ITL-3", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-ITL-4", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-ITL-5", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-AreaCentroid-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-AreaCentroid-2", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-ConnectionLink-1", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-AccessPoint-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-AccessPoint-2", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-AccessPoint-3", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-AccessPoint-4", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-AccessPoint-5", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-AccessPoint-6", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-AccessPoint-7", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-AccessLink-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-AccessLink-2", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Line-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Line-2", "WARNING"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Line-3", "WARNING"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Line-4", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Line-5", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Line-6", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Route-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Route-2", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Route-3", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Route-4", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Route-5", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Route-6", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Route-7", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Route-8", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Route-9", "WARNING"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Route-10", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Route-11", "WARNING"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Route-12", "WARNING"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-PtLink-1", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-JourneyPattern-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-JourneyPattern-2", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-JourneyPattern-3", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-StopPoint-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-StopPoint-2", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-StopPoint-3", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-StopPoint-4", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Timetable-1", "WARNING"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Timetable-2", "WARNING"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Timetable-3", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-VehicleJourney-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-VehicleJourney-2", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-VehicleJourney-3", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-VehicleJourney-4", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-VehicleJourney-5", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-VehicleJourney-6", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-VehicleJourney-7", "WARNING"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-VehicleJourney-8", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-VehicleJourneyAtStop-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-VehicleJourneyAtStop-2", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-VehicleJourneyAtStop-3", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-VehicleJourneyAtStop-4", "ERROR"));
			
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Facility-1", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Facility-2", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Facility-3", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Facility-4", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Facility-5", "ERROR"));
			neptuneTestList.add(new TestDescription(2, "2-NEPTUNE-Facility-6", "ERROR"));
	}
	
	
	public List<TestDescription> getTestUtilsList() {
		return neptuneTestList;
	}
	
	public static TestUtils getInstance() {
		if(singleton == null) {
			singleton = new TestUtils();
		}
		
		return singleton;
	}
}

