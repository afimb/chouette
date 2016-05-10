package mobi.chouette.exchange.gtfs.model.exporter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.gtfs.model.GtfsShape;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.exchange.gtfs.model.importer.GtfsConverter;

public class ShapeExporter extends ExporterImpl<GtfsShape> implements
		GtfsConverter {
	public static enum FIELDS {
		shape_id, shape_pt_lat, shape_pt_lon, shape_pt_sequence, shape_dist_traveled;
	};

	public static final String FILENAME = "shapes.txt";

	public ShapeExporter(String name) throws IOException {
		super(name);
	}

	@Override
	public void writeHeader() throws IOException {
		write(FIELDS.values());
	}

	@Override
	public void export(GtfsShape bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, GtfsShape> CONVERTER = new Converter<String, GtfsShape>() {

		@Override
		public GtfsShape from(Context context, String input) {
			GtfsShape bean = new GtfsShape();
			List<String> values = Tokenizer.tokenize(input);

			int i = 0;
			bean.setShapeId(STRING_CONVERTER.from(context, FIELDS.shape_id,
					values.get(i++), true));
			bean.setShapePtLat(BigDecimal.valueOf(FLOAT_CONVERTER.from(context,
					FIELDS.shape_pt_lat, values.get(i++), true)));
			bean.setShapePtLon(BigDecimal.valueOf(FLOAT_CONVERTER.from(context,
					FIELDS.shape_pt_lon, values.get(i++), true)));
			bean.setShapePtSequence(INTEGER_CONVERTER.from(context, FIELDS.shape_pt_sequence, values.get(i++), true));
			
			return bean;
		}

		@Override
		public String to(Context context, GtfsShape input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.shape_id,
					input.getShapeId(), true));
			values.add(FLOAT_CONVERTER.to(context, FIELDS.shape_pt_lat, input
					.getShapePtLat().floatValue(), true));
			values.add(FLOAT_CONVERTER.to(context, FIELDS.shape_pt_lon, input
					.getShapePtLon().floatValue(), true));
			values.add(INTEGER_CONVERTER.to(context, FIELDS.shape_pt_sequence, input.getShapePtSequence(), true));
			values.add(FLOAT_CONVERTER.to(context, FIELDS.shape_dist_traveled, input
					.getShapeDistTraveled().floatValue(), false));
			
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected Exporter create(String path) throws IOException {
			return new ShapeExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(ShapeExporter.class.getName(), factory);
	}
}
