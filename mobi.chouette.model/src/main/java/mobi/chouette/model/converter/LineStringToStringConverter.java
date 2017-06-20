package mobi.chouette.model.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ByteOrderValues;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;

@Converter
public class LineStringToStringConverter implements AttributeConverter<LineString, String> {

	@Override
	public String convertToDatabaseColumn(LineString attribute) {
		if (attribute == null)
			return null;
		WKBWriter w = new WKBWriter(2, ByteOrderValues.LITTLE_ENDIAN,true);
		return WKBWriter.toHex(w.write(attribute));
	}

	@Override
	public LineString convertToEntityAttribute(String dbData) {
		if (dbData == null)
			return null;
		if (dbData.isEmpty())
			return null;
		WKBReader r = new WKBReader(new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326));
		try {
			Geometry geom = r.read(WKBReader.hexToBytes(dbData));
			if (geom instanceof LineString)
				return (LineString) geom;
			throw new IllegalArgumentException(geom.getClass().getName() + "is not a LineString");
		} catch (ParseException e) {
			throw new IllegalArgumentException("parse exception on " + dbData, e);
		}
	}

}
