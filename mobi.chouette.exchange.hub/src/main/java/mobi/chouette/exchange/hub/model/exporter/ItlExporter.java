package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubItl;

public class ItlExporter extends ExporterImpl<HubItl> implements
		HubConverter {

	public static enum FIELDS {
		code_ligne, sens,  code_arret,
		identifiant_arret, ordre, type, identifiant;
	};

	public static final String FILENAME = "ITL.TXT";

	public ItlExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("ITL");
	}

	@Override
	public void export(HubItl bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubItl> CONVERTER = new Converter<String, HubItl>() {


		@Override
		public String to(Context context, HubItl input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.code_ligne,
					input.getCodeLigne(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.sens,
					input.getSens(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_arret,
					input.getCodeArret(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant_arret,
					input.getIdentifiantArret(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.ordre,
					input.getOrdre(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.type,
					input.getType(), true));
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant,
					input.getIdentifiant(), false));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubItl> create(String path) throws IOException {
			return new ItlExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(ItlExporter.class.getName(), factory);
	}

}