package mobi.chouette.exchange.validation.checkpoint;

import java.math.BigDecimal;

import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.LongLatTypeEnum;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

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
	Reporter.log("distance S"+ StopAreaCheckPoints.computeQuickDistance(area1, area2),true);
	Reporter.log("distance W"+ StopAreaCheckPoints.distance(area1, area2, 0),true);
	Assert.assertTrue(checkPoint.near(area2));
	area2.setLatitude(new BigDecimal(48.8599));
	Reporter.log("distance S"+ StopAreaCheckPoints.computeQuickDistance(area1, area2),true);
	Reporter.log("distance W"+ StopAreaCheckPoints.distance(area1, area2, 0),true);
	Assert.assertFalse(checkPoint.near(area2));
	area2.setLatitude(new BigDecimal(48.8572));
	Reporter.log("distance S"+ StopAreaCheckPoints.computeQuickDistance(area1, area2),true);
	Reporter.log("distance W"+ StopAreaCheckPoints.distance(area1, area2, 0),true);
	Assert.assertTrue(checkPoint.near(area2));
	area2.setLatitude(new BigDecimal(48.8570));
	Reporter.log("distance S"+ StopAreaCheckPoints.computeQuickDistance(area1, area2),true);
	Reporter.log("distance W"+ StopAreaCheckPoints.distance(area1, area2, 0),true);
	Assert.assertFalse(checkPoint.near(area2));
	
	area2.setLatitude(new BigDecimal(48.858514));
	area2.setLongitude(new BigDecimal(2.3405));
	Reporter.log("distance S"+ StopAreaCheckPoints.computeQuickDistance(area1, area2),true);
	Reporter.log("distance W"+ StopAreaCheckPoints.distance(area1, area2, 0),true);
	Assert.assertTrue(checkPoint.near(area2));
	area2.setLongitude(new BigDecimal(2.3402));
	Reporter.log("distance S"+ StopAreaCheckPoints.computeQuickDistance(area1, area2),true);
	Reporter.log("distance W"+ StopAreaCheckPoints.distance(area1, area2, 0),true);
	Assert.assertFalse(checkPoint.near(area2));
	area2.setLongitude(new BigDecimal(2.3444));
	Reporter.log("distance S"+ StopAreaCheckPoints.computeQuickDistance(area1, area2),true);
	Reporter.log("distance W"+ StopAreaCheckPoints.distance(area1, area2, 0),true);
	Assert.assertTrue(checkPoint.near(area2));
	area2.setLongitude(new BigDecimal(2.3446));
	Reporter.log("distance S"+ StopAreaCheckPoints.computeQuickDistance(area1, area2),true);
	Reporter.log("distance W"+ StopAreaCheckPoints.distance(area1, area2, 0),true);
	Assert.assertFalse(checkPoint.near(area2));

	area1.setLatitude(new BigDecimal(0));
	area2.setLatitude(new BigDecimal(0));
	Reporter.log("distance S"+ StopAreaCheckPoints.computeQuickDistance(area1, area2),true);
	Reporter.log("distance W"+ StopAreaCheckPoints.distance(area1, area2, 0),true);

	}


}
