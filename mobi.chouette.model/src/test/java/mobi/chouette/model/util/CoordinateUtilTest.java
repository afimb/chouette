package mobi.chouette.model.util;

import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CoordinateUtilTest {
	@Test(groups = { "coordinate" }, description = "transform from WGS84")
	public void testTransformFromWGS84() throws Exception 
	{
		Coordinate c = new Coordinate(BigDecimal.valueOf(45), BigDecimal.valueOf(0)) ;
		
		Coordinate c2 = CoordinateUtil.transform(Coordinate.WGS84, Coordinate.LAMBERT, c);
		Exception e = CoordinateUtil.getLastException();
		if (e != null)  throw e;
		Assert.assertNotNull(c2," a value must be returned");
		Assert.assertTrue(c2.x.doubleValue() < 415729,"x must be arround 415729 : "+c2.x);
		Assert.assertTrue(c2.y.doubleValue() < 2002669,"y must be arround 2002669 : "+c2.y);
		Assert.assertTrue(c2.x.doubleValue() > 415728,"x must be arround 415728 : "+c2.x);
		Assert.assertTrue(c2.y.doubleValue() > 2002668,"y must be arround 2002668 : "+c2.y);
	}

	@Test(groups = { "coordinate" }, description = "transform to WGS84")
	public void testTransformToWGS84() throws Exception 
	{
		Coordinate c = new Coordinate(BigDecimal.valueOf(415729), BigDecimal.valueOf(2002669)) ;
		
		Coordinate c2 = CoordinateUtil.transform(Coordinate.LAMBERT, Coordinate.WGS84, c);
		Exception e = CoordinateUtil.getLastException();
		if (e != null)  throw e;
		Assert.assertNotNull(c2," a value must be returned");
		Assert.assertTrue(c2.x.doubleValue() < 46,"x must be arround 46 : "+c2.x);
		Assert.assertTrue(c2.y.doubleValue() < 1,"y must be arround 1 : "+c2.y);
		Assert.assertTrue(c2.x.doubleValue() > 44,"x must be arround 44 : "+c2.x);
		Assert.assertTrue(c2.y.doubleValue() > -1,"y must be arround -1 : "+c2.y);
	}

}
