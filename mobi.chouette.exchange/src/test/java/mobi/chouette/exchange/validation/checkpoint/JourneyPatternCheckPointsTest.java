package mobi.chouette.exchange.validation.checkpoint;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import static mobi.chouette.model.type.TransportModeNameEnum.*;
import static mobi.chouette.model.type.TransportSubModeNameEnum.*;
import mobi.chouette.model.type.*;

public class JourneyPatternCheckPointsTest {

	@Test
	public void check3JourneyPattern3Fail() {
		JourneyPatternCheckPoints checkPoints = new JourneyPatternCheckPoints();
		Context context = createValidationContext(AbstractValidation.JOURNEY_PATTERN_3);
		
		checkPoints.check3JourneyPattern3(context, createJourneyPattern(Bus,null,Tram,null));
		checkPoints.check3JourneyPattern3(context, createJourneyPattern(Bus,null,Bus,RailReplacementBus));
		Assert.assertEquals(((ValidationReport)context.get(Constant.VALIDATION_REPORT)).getCheckPointErrors().size(), 2);
	}

	@Test
	public void check3JourneyPattern3OK() {
		JourneyPatternCheckPoints checkPoints = new JourneyPatternCheckPoints();
		Context context = createValidationContext(AbstractValidation.JOURNEY_PATTERN_3);
		
		checkPoints.check3JourneyPattern3(context, createJourneyPattern(Bus,null,Coach,null));
		checkPoints.check3JourneyPattern3(context, createJourneyPattern(Bus,RailReplacementBus,Bus,RailReplacementBus));
		checkPoints.check3JourneyPattern3(context, createJourneyPattern(Bus,RailReplacementBus,Bus,null));
		Assert.assertEquals(((ValidationReport)context.get(Constant.VALIDATION_REPORT)).getCheckPointErrors().size(), 0);
	}

	
	
	protected Context createValidationContext(String checkPointName) {
		Context context = new Context();
		ValidationReport validationReport = new ValidationReport();
		context.put(Constant.VALIDATION_REPORT, validationReport);
		ValidationReporter reporter = ValidationReporter.Factory.getInstance();
		reporter.addItemToValidationReport(context, checkPointName,"E");
		return context;
	}
	
	private JourneyPattern createJourneyPattern(TransportModeNameEnum lineMode, TransportSubModeNameEnum lineSubMode, TransportModeNameEnum stopMode, TransportSubModeNameEnum stopSubMode) {
		StopArea sa = new StopArea();
		
		sa.setTransportModeName(stopMode);
		sa.setTransportSubMode(stopSubMode);

		JourneyPattern jp = new JourneyPattern();
		StopPoint sp = new StopPoint();
		ScheduledStopPoint ssp=new ScheduledStopPoint();
		sp.setScheduledStopPoint(ssp);
		ssp.setContainedInStopArea(sa);
		jp.getStopPoints().add(sp);
		
		Route r = new Route();
		jp.setRoute(r);
		
		
		Line l = new Line();
		r.setLine(l);
		
		l.setTransportModeName(lineMode);
		l.setTransportSubModeName(lineSubMode);

		
		return jp;
	}
	
}
