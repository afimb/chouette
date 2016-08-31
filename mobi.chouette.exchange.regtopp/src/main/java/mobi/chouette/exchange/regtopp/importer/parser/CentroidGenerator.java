package mobi.chouette.exchange.regtopp.importer.parser;

import com.vividsolutions.jts.algorithm.CentroidPoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import mobi.chouette.model.NeptuneLocalizedObject;
import mobi.chouette.model.type.LongLatTypeEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * Generate centroid based on multiple geometry points.
 * Typically, generate centroid for a parent stop place from coordinates from multiple boarding positions.
 */
public class CentroidGenerator {

    private final GeometryFactory geometryFactory;
    private final LongLatTypeEnum longLatType;

    public CentroidGenerator(LongLatTypeEnum longLatType) {
        PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.maximumPreciseValue);
        geometryFactory = new GeometryFactory(precisionModel, longLatType.getValue());
        this.longLatType = longLatType;
    }

    public CentroidGenerator() {
        this(LongLatTypeEnum.WGS84);
    }


    public void generate(List<? extends NeptuneLocalizedObject> localizedObjects, NeptuneLocalizedObject destination) {
        CentroidPoint centroidPoint = new CentroidPoint();
        int pointsAdded = 0;
        for (NeptuneLocalizedObject localizedObject : localizedObjects) {
            if(localizedObject.hasCoordinates()) {
                centroidPoint.add(geometryFactory.createPoint(new Coordinate(localizedObject.getLongitude().doubleValue(), localizedObject.getLatitude().doubleValue())));
                pointsAdded++;
            }
        }

        if(pointsAdded >0) {
            Point point = geometryFactory.createPoint(centroidPoint.getCentroid());

            destination.setLatitude(BigDecimal.valueOf(point.getY()));
            destination.setLongitude(BigDecimal.valueOf(point.getX()));
            destination.setLongLatType(longLatType);
        }
    }
}
