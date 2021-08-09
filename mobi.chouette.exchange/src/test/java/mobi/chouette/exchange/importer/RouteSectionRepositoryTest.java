package mobi.chouette.exchange.importer;

import mobi.chouette.model.type.LongLatTypeEnum;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.junit.Assert;
import org.junit.Test;

public class RouteSectionRepositoryTest {

	RouteSectionRepository routeSectionRepository = new RouteSectionRepository(null);
	private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), LongLatTypeEnum.WGS84.getValue());

	private final Coordinate from = new Coordinate(9.747759, 63.168975);
	private final Coordinate to = new Coordinate(9.705659, 63.125455);

	private final LineString validLineString = geometryFactory.createLineString(new Coordinate[]{from, to});


	@Test
	public void isLineStringGoodMatchForQuays_whenLineStringIsNull_returnTrue() {
		Assert.assertTrue(routeSectionRepository.isLineStringGoodMatchForQuays(null, from, to));
	}


	@Test
	public void isLineStringGoodMatchForQuays_whenLineStringEndsAreCloseToFromAndTo_returnsTrue() {
		Assert.assertTrue(routeSectionRepository.isLineStringGoodMatchForQuays(validLineString, from, to));
	}

	@Test
	public void isLineStringGoodMatchForQuays_whenLineStringStartIsTooFarFromFrom_returnFalse() {
		Assert.assertFalse(routeSectionRepository.isLineStringGoodMatchForQuays(validLineString, new Coordinate(10.22, 63.2), to));
	}

	@Test
	public void isLineStringGoodMatchForQuays_whenLineStringEndIsTooFarFromTo_returnFalse() {
		Assert.assertFalse(routeSectionRepository.isLineStringGoodMatchForQuays(validLineString, from, new Coordinate(10.22, 63.2)));
	}
}
