package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;

import mobi.chouette.exchange.gtfs.model.GtfsTransfer;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException.ERROR;

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
	protected GtfsTransfer build(GtfsIterator reader, Context context) {
		int i = 0;
		for (FIELDS field : FIELDS.values()) {
			array[i++] = getField(reader, field.name());
		}

		i = 0;
		int id = (int) context.get(Context.ID);
		bean.setId(id);
		bean.setFromStopId(STRING_CONVERTER.from(context, FIELDS.from_stop_id,
				array[i++], true));
		bean.setToStopId(STRING_CONVERTER.from(context, FIELDS.to_stop_id,
				array[i++], true));
		bean.setTransferType(TRANSFERTYPE_CONVERTER.from(context,
				FIELDS.transfer_type, array[i++], true));
		bean.setMinTransferTime(POSITIVE_INTEGER_CONVERTER.from(context,
				FIELDS.min_transfer_time, array[i++], false));

		return bean;
	}

	@Override
	public boolean validate(GtfsTransfer bean, GtfsImporter dao) {
		boolean result = true;

		String fromStopId = bean.getFromStopId();
		if (!dao.getStopById().containsKey(fromStopId)) {
			throw new GtfsException(getPath(), bean.getId(),
					FIELDS.from_stop_id.name(), ERROR.MISSING_FOREIGN_KEY,
					"TODO", bean.getFromStopId());
		}

		String toStopId = bean.getToStopId();
		if (!dao.getStopById().containsKey(toStopId)) {
			throw new GtfsException(getPath(), bean.getId(),
					FIELDS.to_stop_id.name(), ERROR.MISSING_FOREIGN_KEY,
					"TODO", bean.getToStopId());
		}

		return result;
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
