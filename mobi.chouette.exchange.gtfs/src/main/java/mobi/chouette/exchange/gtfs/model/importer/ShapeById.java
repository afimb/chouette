package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import mobi.chouette.common.HTMLTagValidator;
import mobi.chouette.exchange.gtfs.model.GtfsShape;

public class ShapeById extends IndexImpl<GtfsShape> implements GtfsConverter {
	
	public static enum FIELDS {
		shape_id, shape_pt_lat, shape_pt_lon, shape_pt_sequence, shape_dist_traveled;
	};

	public static final String FILENAME = "shapes.txt";
	public static final String KEY = FIELDS.shape_id.name();

	private GtfsShape bean = new GtfsShape();
	private String[] array = new String[FIELDS.values().length];

	public ShapeById(String name) throws IOException {
		super(name, KEY, false);
	}
	
	@Override
	protected void checkRequiredFields(Map<String, Integer> fields) {
		for (String fieldName : fields.keySet()) {
			if (fieldName != null) {
				if (!fieldName.equals(fieldName.trim())) {
					// extra spaces in end fields are tolerated : 1-GTFS-CSV-7 warning
					getErrors().add(new GtfsException(_path, 1, getIndex(fieldName), fieldName.trim(), GtfsException.ERROR.EXTRA_SPACE_IN_HEADER_FIELD, null, fieldName));
				}
				
				if (HTMLTagValidator.validate(fieldName.trim())) {
					getErrors().add(new GtfsException(_path, 1, getIndex(fieldName), fieldName.trim(), GtfsException.ERROR.HTML_TAG_IN_HEADER_FIELD, null, null));
				}
				
				boolean fieldNameIsExtra = true;
				for (FIELDS field : FIELDS.values()) {
					if (fieldName.trim().equals(field.name())) {
						fieldNameIsExtra = false;
						break;
					}
				}
				if (fieldNameIsExtra) {
					// extra fields are tolerated : 1-GTFS-Shape-8 warning
					getErrors().add(new GtfsException(_path, 1, getIndex(fieldName), fieldName, GtfsException.ERROR.EXTRA_HEADER_FIELD, null, null));
				}
			}
		}

		// checks for ubiquitous header fields : 1-GTFS-Transfer-1 error
		if ( fields.get(FIELDS.shape_id.name()) == null ||
				fields.get(FIELDS.shape_pt_lat.name()) == null ||
				fields.get(FIELDS.shape_pt_lon.name()) == null ||
				fields.get(FIELDS.shape_pt_sequence.name()) == null) {
			
			String name = "";
			if (fields.get(FIELDS.shape_id.name()) == null)
				name = FIELDS.shape_id.name();
			else if (fields.get(FIELDS.shape_pt_lat.name()) == null)
				name = FIELDS.shape_pt_lat.name();
			else if (fields.get(FIELDS.shape_pt_lon.name()) == null)
				name = FIELDS.shape_pt_lon.name();
			else if (fields.get(FIELDS.shape_pt_sequence.name()) == null)
				name = FIELDS.shape_pt_sequence.name();
			throw new GtfsException(_path, 1, name, GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null);
		}
	}

	@Override
	protected GtfsShape build(GtfsIterator reader, Context context) {
		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		String value = null;
		clearBean();
		int id = (int) context.get(Context.ID);
		bean.setId(id);
		bean.getErrors().clear();
		
		value = array[i++]; testExtraSpace(FIELDS.shape_id.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.shape_id.name()), FIELDS.shape_id.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			bean.setShapeId(STRING_CONVERTER.from(context, FIELDS.shape_id, value, true));
		}
			
		value = array[i++]; testExtraSpace(FIELDS.shape_pt_lat.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.shape_pt_lat.name()), FIELDS.shape_pt_lat.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			boolean validLat = true;
			try {
				double lat = Double.parseDouble(value);
				if (lat < -90 || lat > 90)
					validLat = false;
			} catch(Exception e) {
				validLat = false;
			}
			if (validLat)
				bean.setShapePtLat(BigDecimal.valueOf(FLOAT_CONVERTER.from(context, FIELDS.shape_pt_lat, value, true)));
			else {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.shape_pt_lat.name()), FIELDS.shape_pt_lat.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.shape_pt_lon.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.shape_pt_lon.name()), FIELDS.shape_pt_lon.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));

		} else {
			boolean validLon = true;
			try {
				double lon = Double.parseDouble(value);
				if (lon < -180 || lon > 180)
					validLon = false;
			} catch(Exception e) {
				validLon = false;
			}
			if (validLon)
				bean.setShapePtLon(BigDecimal.valueOf(FLOAT_CONVERTER.from(context, FIELDS.shape_pt_lon, value, true)));
			else {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.shape_pt_lon.name()), FIELDS.shape_pt_lon.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.shape_pt_sequence.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.shape_pt_sequence.name()), FIELDS.shape_pt_sequence.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				int shapePtSequence = INTEGER_CONVERTER.from(context, FIELDS.shape_pt_sequence, value, true);
				if (shapePtSequence >= 0)
					bean.setShapePtSequence(shapePtSequence);
				else
					if (withValidation)
						bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.shape_pt_sequence.name()), FIELDS.shape_pt_sequence.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			} catch(GtfsException e) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.shape_pt_sequence.name()), FIELDS.shape_pt_sequence.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.shape_dist_traveled.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				float shapeDistTraveled = FLOAT_CONVERTER.from(context, FIELDS.shape_dist_traveled, value, false);
				if (shapeDistTraveled >= 0)
					bean.setShapeDistTraveled(shapeDistTraveled);
				else
					if (withValidation)
						bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.shape_dist_traveled.name()), FIELDS.shape_dist_traveled.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			} catch(GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.shape_dist_traveled.name()), FIELDS.shape_dist_traveled.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}			
		}
		return bean;
	}

	private void clearBean() {
		//bean.getErrors().clear();
		bean.setId(null);
		bean.setShapeDistTraveled(null);
		bean.setShapeId(null);
		bean.setShapePtLat(null);
		bean.setShapePtLon(null);
		bean.setShapePtSequence(null);
	}

	@Override
	public boolean validate(GtfsShape bean, GtfsImporter dao) {
		boolean result = true;
		
		return result;
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(String name) throws IOException {
			return new ShapeById(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(ShapeById.class.getName(), factory);
	}

}
