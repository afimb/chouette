package mobi.chouette.exchange.importer;

import mobi.chouette.model.type.LongLatTypeEnum;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.junit.Assert;
import org.junit.Test;

public class GenerateRouteSectionsCommandTest {

	GenerateRouteSectionsCommand generateRouteSectionsCommand = new GenerateRouteSectionsCommand();
	private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), LongLatTypeEnum.WGS84.getValue());

	private final Coordinate from = new Coordinate(9.747759, 63.168975);
	private final Coordinate to = new Coordinate(9.705659, 63.125455);

	private final LineString validLineString = geometryFactory.createLineString(new Coordinate[]{from, to});


	@Test
	public void sanityCheckLineString_whenLineStringIsNullReturnNull() {
		Assert.assertNull(generateRouteSectionsCommand.sanityCheckedLineString(null, from, to));
	}


	@Test
	public void sanityCheckLineString_whenLineStringEndsAreCloseToFromAndTo_returnLineString() {
		Assert.assertEquals(validLineString, generateRouteSectionsCommand.sanityCheckedLineString(validLineString, from, to));
	}

	@Test
	public void sanityCheckLineString_whenLineStringStartIsTooFarFromFrom_returnNull() {
		Assert.assertNull(generateRouteSectionsCommand.sanityCheckedLineString(validLineString, new Coordinate(10.22, 63.2), to));
	}

	@Test
	public void sanityCheckLineString_whenLineStringEndIsTooFarFromTo_returnNull() {
		Assert.assertNull(generateRouteSectionsCommand.sanityCheckedLineString(validLineString, from, new Coordinate(10.22, 63.2)));
	}
}
