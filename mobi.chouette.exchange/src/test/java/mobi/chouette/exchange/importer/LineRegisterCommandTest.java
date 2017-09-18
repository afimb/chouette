package mobi.chouette.exchange.importer;

import java.io.StringWriter;

import javax.naming.InitialContext;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LineRegisterCommandTest implements Constant {
	
	
	private LineRegisterCommand lineRegister = null;
	
	
	
	@Test (groups = { "write" }, description = "write command")
	public void testLineRegisterWrite() throws Exception 
	{
		InitialContext initialContext = new InitialContext();
		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		
		StringWriter buffer = new StringWriter(); 
		VehicleJourney neptuneObject = new VehicleJourney();
	    neptuneObject.setId(4321L);
	    StopPoint sp = new StopPoint();
	    sp.setId(1001L);
	    
	    VehicleJourneyAtStop vjas = new VehicleJourneyAtStop();
	    vjas.setObjectId("ObjectID");
	    vjas.setObjectVersion(1);
	    vjas.setCreationTime(new LocalDateTime(2000,2,1,0,0));
	    vjas.setCreatorId("creatorId");
        vjas.setStopPoint(sp);
	    
        vjas.setArrivalTime(new LocalTime(23,59,0));
        
        vjas.setDepartureTime(new LocalTime(0,5,0));
        vjas.setArrivalDayOffset(0);
        
        vjas.setDepartureDayOffset(1);
        
		lineRegister = new LineRegisterCommand();
		
		lineRegister.write(buffer, neptuneObject, sp, vjas);
		
		
		Assert.assertEquals(buffer.toString(), "'ObjectID'|1|2000-02-01T00:00:00|'creatorId'|4321|1001|23:59:00|00:05:00|0|1\n", "Invalid data entry for buffer");
	
		
	}
}
