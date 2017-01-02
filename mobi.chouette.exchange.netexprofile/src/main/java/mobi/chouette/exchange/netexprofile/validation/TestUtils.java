package mobi.chouette.exchange.netexprofile.validation;

import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.TestDescription;
import mobi.chouette.exchange.netexprofile.Constant;

public class TestUtils implements Constant{
	protected List<TestDescription> netexProfileTestList = null;
	
	private static TestUtils singleton = null;
	
	private TestUtils() {
			netexProfileTestList = new ArrayList<TestDescription>();
			
			// TODO add validation tests here
	}
	
	
	public List<TestDescription> getTestUtilsList() {
		return netexProfileTestList;
	}
	
	public static TestUtils getInstance() {
		if(singleton == null) {
			singleton = new TestUtils();
		}
		
		return singleton;
	}
}
