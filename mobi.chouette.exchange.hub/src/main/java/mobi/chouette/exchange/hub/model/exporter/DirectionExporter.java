package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubDirection;

public class DirectionExporter extends ExporterImpl<HubDirection> implements
		HubConverter {

	public static enum FIELDS {
		direction, code_ligne, sens, code_chemin;
	};

	public static final String FILENAME = "DIRECTION.TXT";

	public DirectionExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("DIRECTION");
	}

	@Override
	public void export(HubDirection bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubDirection> CONVERTER = new Converter<String, HubDirection>() {


		@Override
		public String to(Context context, HubDirection input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.direction,
					input.getDirection(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_ligne,
					input.getCodeLigne(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.sens,
					input.getSens(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_chemin,
					input.getCodeChemin(), false));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubDirection> create(String path) throws IOException {
			return new DirectionExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(DirectionExporter.class.getName(), factory);
	}

}