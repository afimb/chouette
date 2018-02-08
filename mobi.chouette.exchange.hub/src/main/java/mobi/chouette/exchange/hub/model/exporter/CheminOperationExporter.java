package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubCheminOperation;

public class CheminOperationExporter extends ExporterImpl<HubCheminOperation> implements
		HubConverter {

	public static enum FIELDS {
		code_chemin, type, un, code_girouette;
	};

	public static final String FILENAME = "CHEMIN_OPERATION.TXT";

	public CheminOperationExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("CHEMIN_OPERATION");
	}

	@Override
	public void export(HubCheminOperation bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubCheminOperation> CONVERTER = new Converter<String, HubCheminOperation>() {


		@Override
		public String to(HubContext hubContext, HubCheminOperation input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(hubContext, FIELDS.code_chemin,
					input.getCodeChemin(), true));
			values.add(STRING_CONVERTER.to(hubContext, FIELDS.type,
					input.getType(), true));
			values.add(NUMBER_CONVERTER.to(hubContext, FIELDS.un,
					input.getUn(), false));
			values.add(STRING_CONVERTER.to(hubContext, FIELDS.code_girouette,
					input.getCodeGirouette(), false));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubCheminOperation> create(String path) throws IOException {
			return new CheminOperationExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(CheminOperationExporter.class.getName(), factory);
	}

}