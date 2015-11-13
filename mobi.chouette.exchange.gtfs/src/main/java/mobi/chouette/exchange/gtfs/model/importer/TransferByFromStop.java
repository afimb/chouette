package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.Map;

import mobi.chouette.common.HTMLTagValidator;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer;
import mobi.chouette.exchange.gtfs.model.GtfsTransfer.TransferType;

public class TransferByFromStop extends IndexImpl<GtfsTransfer> implements
		GtfsConverter {

	public static enum FIELDS {
		from_stop_id, to_stop_id, transfer_type, min_transfer_time;
	};

	public static final String FILENAME = "transfers.txt";
	public static final String KEY = FIELDS.from_stop_id.name();

	private GtfsTransfer bean = new GtfsTransfer();
	private String[] array = new String[FIELDS.values().length];

	public TransferByFromStop(String name) throws IOException {
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
					// extra fields are tolerated : 1-GTFS-Transfer-6 warning
					getErrors().add(new GtfsException(_path, 1, getIndex(fieldName), fieldName, GtfsException.ERROR.EXTRA_HEADER_FIELD, null, null));
				}
			}
		}

		// checks for ubiquitous header fields : 1-GTFS-Transfer-1 error
		if ( fields.get(FIELDS.from_stop_id.name()) == null ||
				fields.get(FIELDS.to_stop_id.name()) == null ||
				fields.get(FIELDS.transfer_type.name()) == null) {
			
			String name = "";
			if (fields.get(FIELDS.from_stop_id.name()) == null)
				name = FIELDS.from_stop_id.name();
			else if (fields.get(FIELDS.to_stop_id.name()) == null)
				name = FIELDS.to_stop_id.name();
			else if (fields.get(FIELDS.transfer_type.name()) == null)
				name = FIELDS.transfer_type.name();

			throw new GtfsException(_path, 1, name, GtfsException.ERROR.MISSING_REQUIRED_FIELDS, null, null);
		}
	}

	@Override
	protected GtfsTransfer build(GtfsIterator reader, Context context) {
		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		String value = null;
		int id = (int) context.get(Context.ID);
		clearBean();
		bean.setId(id);
		bean.getErrors().clear();
		
		value = array[i++]; testExtraSpace(FIELDS.from_stop_id.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.from_stop_id.name()), FIELDS.from_stop_id.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			bean.setFromStopId(STRING_CONVERTER.from(context, FIELDS.from_stop_id, value, true));
		}
			
		value = array[i++]; testExtraSpace(FIELDS.to_stop_id.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.to_stop_id.name()), FIELDS.to_stop_id.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			bean.setToStopId(STRING_CONVERTER.from(context, FIELDS.to_stop_id, value, true));
		}
		
		value = array[i++]; testExtraSpace(FIELDS.transfer_type.name(), value, bean);
		if (value == null || value.trim().isEmpty()) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.transfer_type.name()), FIELDS.transfer_type.name(), GtfsException.ERROR.MISSING_REQUIRED_VALUES, null, null));
		} else {
			try {
				bean.setTransferType(TRANSFERTYPE_CONVERTER.from(context, FIELDS.transfer_type, value, true));
			} catch(GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, FIELDS.transfer_type.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		}
		
		value = array[i++]; testExtraSpace(FIELDS.min_transfer_time.name(), value, bean);
		if (value != null && !value.trim().isEmpty()) {
			try {
				bean.setMinTransferTime(POSITIVE_INTEGER_CONVERTER.from(context, FIELDS.min_transfer_time, value, false));
			} catch (GtfsException ex) {
				if (withValidation)
					bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.min_transfer_time.name()), FIELDS.min_transfer_time.name(), GtfsException.ERROR.INVALID_FORMAT, null, value));
			}
		} else {
			bean.setMinTransferTime(null);
		}
		
		if (bean.getTransferType() == TransferType.Minimal && bean.getMinTransferTime() == null) {
			if (withValidation)
				bean.getErrors().add(new GtfsException(_path, id, getIndex(FIELDS.transfer_type.name()), FIELDS.transfer_type.name(), GtfsException.ERROR.MISSING_TRANSFER_TIME, null, null));
		}
		return bean;
	}

	@Override
	public boolean validate(GtfsTransfer bean, GtfsImporter dao) {
		boolean result = true;

		String fromStopId = bean.getFromStopId();
		if (dao.getStopById().containsKey(fromStopId)) {
			bean.getOkTests().add(GtfsException.ERROR.UNREFERENCED_ID);
		} else {
			bean.getErrors().add(new GtfsException(_path, bean.getId(), getIndex(FIELDS.from_stop_id.name()), FIELDS.from_stop_id.name(), GtfsException.ERROR.UNREFERENCED_ID, null, fromStopId));
			result = false;
		}

		String toStopId = bean.getToStopId();
		if (dao.getStopById().containsKey(toStopId)) {
			bean.getOkTests().add(GtfsException.ERROR.UNREFERENCED_ID);
		} else {
			bean.getErrors().add(new GtfsException(_path, bean.getId(), getIndex(FIELDS.to_stop_id.name()), FIELDS.to_stop_id.name(), GtfsException.ERROR.UNREFERENCED_ID, null, toStopId));
			result = false;
		}

		return result;
	}

	private void clearBean() {
		//bean.getErrors().clear();
		bean.setId(null);
		bean.setFromStopId(null);
		bean.setMinTransferTime(null);
		bean.setToStopId(null);
		bean.setTransferType(null);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(String name) throws IOException {
			return new TransferByFromStop(name);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(TransferByFromStop.class.getName(), factory);
	}

}
