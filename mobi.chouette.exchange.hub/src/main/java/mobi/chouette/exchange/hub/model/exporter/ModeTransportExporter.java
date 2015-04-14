package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubModeTransport;

public class ModeTransportExporter extends ExporterImpl<HubModeTransport> implements
		HubConverter {

	public static enum FIELDS {
		code, commentaire, code_ligne;
	};

	public static final String FILENAME = "MODETRANSPORT.TXT";

	public ModeTransportExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("MODETRANSPORT");
	}

	@Override
	public void export(HubModeTransport bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubModeTransport> CONVERTER = new Converter<String, HubModeTransport>() {


		@Override
		public String to(Context context, HubModeTransport input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(ENUM_CONVERTER.to(context, FIELDS.code,
					input.getCode(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.commentaire,
					input.getCommentaire(), false));
			StringBuffer list = new StringBuffer();
			for (String code : input.getCodesLigne()) {
				list.append("|");
				list.append(STRING_CONVERTER.to(context, FIELDS.code_ligne,
						code, true));
			}
			if (list.length() > 0) list.deleteCharAt(0);
			values.add(list.toString());
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubModeTransport> create(String path) throws IOException {
			return new ModeTransportExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(ModeTransportExporter.class.getName(), factory);
	}

}