package mobi.chouette.exchange.validation.checkpoint;

import java.math.BigDecimal;

import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.type.StopAreaTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.TransportSubModeNameEnum;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;

public class StopAreaCheckPointsTest {
	@Test(groups = { "StopAreaCheckPoint" }, description = "near", priority = 101)
	public void verifyNear() throws Exception {
       ValidationParameters parameters = new ValidationParameters();
		parameters.setInterStopAreaDistanceMin(125);
		
	StopArea area1 = new StopArea();
	StopArea area2 = new StopArea();
	// 48.858514, 2.342421
	area1.setLatitude(new BigDecimal(48.858514));
	area1.setLongitude(new BigDecimal(2.342421));
	area1.setLongLatType(LongLatTypeEnum.WGS84);
	StopAreaCheckPoints checkPoint = new StopAreaCheckPoints();
	checkPoint.updateSquare(area1, parameters);
	
	area2.setLongLatType(LongLatTypeEnum.WGS84);
	area2.setLatitude(new BigDecimal(48.8598));
	area2.setLongitude(new BigDecimal(2.342421));
	Reporter.log("Lat distance S"+ StopAreaCheckPoints.quickDistance(area1, area2),true);
	Reporter.log("Lat distance W"+ StopAreaCheckPoints.distance(area1, area2),true);
	Assert.assertTrue(checkPoint.near(area2));
	area2.setLatitude(new BigDecimal(48.8599));
	Reporter.log("Lat distance S"+ StopAreaCheckPoints.quickDistance(area1, area2),true);
	Reporter.log("Lat distance W"+ StopAreaCheckPoints.distance(area1, area2),true);
	Assert.assertFalse(checkPoint.near(area2));
	area2.setLatitude(new BigDecimal(48.8572));
	Reporter.log("Lat distance S"+ StopAreaCheckPoints.quickDistance(area1, area2),true);
	Reporter.log("Lat distance W"+ StopAreaCheckPoints.distance(area1, area2),true);
	Assert.assertTrue(checkPoint.near(area2));
	area2.setLatitude(new BigDecimal(48.8570));
	Reporter.log("Lat distance S"+ StopAreaCheckPoints.quickDistance(area1, area2),true);
	Reporter.log("Lat distance W"+ StopAreaCheckPoints.distance(area1, area2),true);
	Assert.assertFalse(checkPoint.near(area2));
	
	area2.setLatitude(new BigDecimal(48.858514));
	area2.setLongitude(new BigDecimal(2.3405));
	Reporter.log("Lon distance S"+ StopAreaCheckPoints.quickDistance(area1, area2),true);
	Reporter.log("Lon distance W"+ StopAreaCheckPoints.distance(area1, area2),true);
	Assert.assertTrue(checkPoint.near(area2));
	area2.setLongitude(new BigDecimal(2.3402));
	Reporter.log("Lon distance S"+ StopAreaCheckPoints.quickDistance(area1, area2),true);
	Reporter.log("Lon distance W"+ StopAreaCheckPoints.distance(area1, area2),true);
	Assert.assertFalse(checkPoint.near(area2));
	area2.setLongitude(new BigDecimal(2.3444));
	Reporter.log("Lon distance S"+ StopAreaCheckPoints.quickDistance(area1, area2),true);
	Reporter.log("Lon distance W"+ StopAreaCheckPoints.distance(area1, area2),true);
	Assert.assertTrue(checkPoint.near(area2));
	area2.setLongitude(new BigDecimal(2.3446));
	Reporter.log("Lon distance S"+ StopAreaCheckPoints.quickDistance(area1, area2),true);
	Reporter.log("Lon distance W"+ StopAreaCheckPoints.distance(area1, area2),true);
	Assert.assertFalse(checkPoint.near(area2));

	
	area1.setLatitude(new BigDecimal(0));
	area2.setLatitude(new BigDecimal(0));
	Reporter.log("Equat distance S"+ StopAreaCheckPoints.quickDistance(area1, area2),true);
	Reporter.log("Equat distance W"+ StopAreaCheckPoints.distance(area1, area2),true);

	area1.setLatitude(new BigDecimal(70));
	area2.setLatitude(new BigDecimal(70));
	Reporter.log("Norge lat distance S"+ StopAreaCheckPoints.quickDistance(area1, area2),true);
	Reporter.log("Norge lat distance W"+ StopAreaCheckPoints.distance(area1, area2),true);

	area1.setLatitude(new BigDecimal(48.855));
	area2.setLatitude(new BigDecimal(48.86));
	area1.setLongitude(new BigDecimal(0));
	area2.setLongitude(new BigDecimal(0.01));
	Reporter.log("Big distance S"+ StopAreaCheckPoints.quickDistance(area1, area2),true);
	Reporter.log("Big distance W"+ StopAreaCheckPoints.distance(area1, area2),true);
	}


	@Test
	public void testCheck3StopArea6Fail() {
		StopAreaCheckPoints checkPoints = new StopAreaCheckPoints();
		Context context = createValidationContext(AbstractValidation.STOP_AREA_6);
		checkPoints.check3StopArea6(context, createStopArea(StopAreaTypeEnum.Airport, TransportModeNameEnum.Bus), new ValidationParameters());
		checkPoints.check3StopArea6(context, createStopArea(StopAreaTypeEnum.TramStation, TransportModeNameEnum.Bus), new ValidationParameters());
		checkPoints.check3StopArea6(context, createStopArea(StopAreaTypeEnum.MetroStation, TransportModeNameEnum.Bus), new ValidationParameters());
		checkPoints.check3StopArea6(context, createStopArea(StopAreaTypeEnum.FerryPort, TransportModeNameEnum.Bus), new ValidationParameters());
		checkPoints.check3StopArea6(context, createStopArea(StopAreaTypeEnum.HarbourPort, TransportModeNameEnum.Bus), new ValidationParameters());
		checkPoints.check3StopArea6(context, createStopArea(StopAreaTypeEnum.RailStation, TransportModeNameEnum.Bus), new ValidationParameters());
		Assert.assertEquals(((ValidationReport)context.get(Constant.VALIDATION_REPORT)).getCheckPointErrors().size(), 6);
	}
	
	@Test
	public void testCheck3StopArea6OK() {
		StopAreaCheckPoints checkPoints = new StopAreaCheckPoints();
		Context context = createValidationContext(AbstractValidation.STOP_AREA_6);
		checkPoints.check3StopArea6(context, createStopArea(StopAreaTypeEnum.Airport, TransportModeNameEnum.Air), new ValidationParameters());
		checkPoints.check3StopArea6(context, createStopArea(StopAreaTypeEnum.TramStation, TransportModeNameEnum.Tram), new ValidationParameters());
		checkPoints.check3StopArea6(context, createStopArea(StopAreaTypeEnum.MetroStation, TransportModeNameEnum.Metro), new ValidationParameters());
		checkPoints.check3StopArea6(context, createStopArea(StopAreaTypeEnum.FerryPort, TransportModeNameEnum.Water), new ValidationParameters());
		checkPoints.check3StopArea6(context, createStopArea(StopAreaTypeEnum.HarbourPort, TransportModeNameEnum.Water), new ValidationParameters());
		checkPoints.check3StopArea6(context, createStopArea(StopAreaTypeEnum.RailStation, TransportModeNameEnum.Rail), new ValidationParameters());
		Assert.assertEquals(((ValidationReport)context.get(Constant.VALIDATION_REPORT)).getCheckPointErrors().size(), 0);
	}
	
	@Test
	public void testCheck3StopArea7Fail() {
		StopAreaCheckPoints checkPoints = new StopAreaCheckPoints();
		Context context = createValidationContext(AbstractValidation.STOP_AREA_7);
		checkPoints.check3StopArea7(context, createStopArea(TransportModeNameEnum.Bus,TransportSubModeNameEnum.DomesticFlight), new ValidationParameters());
		checkPoints.check3StopArea7(context, createStopArea(TransportModeNameEnum.Air,TransportSubModeNameEnum.RegionalBus ), new ValidationParameters());
		checkPoints.check3StopArea7(context, createStopArea(TransportModeNameEnum.Ferry,TransportSubModeNameEnum.HelicopterService), new ValidationParameters());
		checkPoints.check3StopArea7(context, createStopArea(TransportModeNameEnum.Water,TransportSubModeNameEnum.SchoolBus), new ValidationParameters());
		checkPoints.check3StopArea7(context, createStopArea(TransportModeNameEnum.Water,null), new ValidationParameters());
		Assert.assertEquals(((ValidationReport)context.get(Constant.VALIDATION_REPORT)).getCheckPointErrors().size(), 4);
	}
	
	private StopArea createStopArea(StopAreaTypeEnum type, TransportModeNameEnum transportMode) {
		StopArea sa = new StopArea();
		sa.setStopAreaType(type);
		sa.setTransportModeName(transportMode);
		return sa;
	}

	private StopArea createStopArea(TransportModeNameEnum transportMode, TransportSubModeNameEnum subMode) {
		StopArea sa = new StopArea();
		
		sa.setTransportModeName(transportMode);
		sa.setTransportSubMode(subMode);
		return sa;
	}


	protected Context createValidationContext(String checkPointName) {
		Context context = new Context();
		ValidationReport validationReport = new ValidationReport();
		context.put(Constant.VALIDATION_REPORT, validationReport);
		ValidationReporter reporter = ValidationReporter.Factory.getInstance();
		reporter.addItemToValidationReport(context, checkPointName,"E");
		return context;
	}
	
}
