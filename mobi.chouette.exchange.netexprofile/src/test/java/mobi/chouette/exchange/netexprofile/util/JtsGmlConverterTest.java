package mobi.chouette.exchange.netexprofile.util;

import com.vividsolutions.jts.geom.LineString;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LineStringType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JtsGmlConverterTest {

    @Test
    public void testIgnoreEmptyLineString() {
        LineStringType lineStringType = new LineStringType();
        LineString lineString = JtsGmlConverter.fromGmlToJts(lineStringType);
        Assert.assertNull(lineString);
    }

    @Test
    public void testIgnoreLineStringWithEmptyPosList() {
        LineStringType lineStringType = new LineStringType();
        lineStringType.setPosList(new DirectPositionListType());
        LineString lineString = JtsGmlConverter.fromGmlToJts(lineStringType);
        Assert.assertNull(lineString);
    }

    @Test
    public void testIgnoreLineStringWithUnrecognizedPosOrPoint() {
        LineStringType lineStringType = new LineStringType();
        lineStringType.withPosOrPointProperty(new Object());
        LineString lineString = JtsGmlConverter.fromGmlToJts(lineStringType);
        Assert.assertNull(lineString);
    }
}
