package mobi.chouette.exchange.gtfs.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.gtfs.model.GtfsTransfer;
import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.exchange.gtfs.model.importer.GtfsConverter;

public class TransferExporter extends ExporterImpl<GtfsTransfer> implements
		GtfsConverter {
	public static enum FIELDS {
		from_stop_id, to_stop_id, transfer_type, min_transfer_time;
	};

	public static final String FILENAME = "transfers.txt";

	public TransferExporter(String name) throws IOException {
		super(name);
	}

	@Override
	public void writeHeader() throws IOException {
		write(FIELDS.values());
	}

	@Override
	public void export(GtfsTransfer bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, GtfsTransfer> CONVERTER = new Converter<String, GtfsTransfer>() {

		@Override
		public GtfsTransfer from(Context context, String input) {
			GtfsTransfer bean = new GtfsTransfer();
			List<String> values = Tokenizer.tokenize(input);

			int i = 0;
			bean.setFromStopId(STRING_CONVERTER.from(context,
					FIELDS.from_stop_id, values.get(i++), true));
			bean.setToStopId(STRING_CONVERTER.from(context, FIELDS.to_stop_id,
					values.get(i++), true));
			bean.setTransferType(TRANSFERTYPE_CONVERTER.from(context,
					FIELDS.transfer_type, values.get(i++), true));
			bean.setMinTransferTime(INTEGER_CONVERTER.from(context,
					FIELDS.min_transfer_time, values.get(i++), false));

			return bean;
		}

		@Override
		public String to(Context context, GtfsTransfer input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.from_stop_id,
					input.getFromStopId(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.to_stop_id,
					input.getToStopId(), true));
			values.add(TRANSFERTYPE_CONVERTER.to(context, FIELDS.transfer_type,
					input.getTransferType(), true));
			values.add(INTEGER_CONVERTER.to(context, FIELDS.min_transfer_time,
					input.getMinTransferTime(), false));

			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected Exporter create(String path) throws IOException {
			return new TransferExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories
				.put(TransferExporter.class.getName(), factory);
	}

}
