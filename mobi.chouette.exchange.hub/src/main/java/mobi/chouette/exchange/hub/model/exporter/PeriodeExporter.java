package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubPeriode;

public class PeriodeExporter extends ExporterImpl<HubPeriode> implements
		HubConverter {

	public static enum FIELDS {
		code, nom, date_debut, date_fin, calendrier, identifiant;
	};

	public static final String FILENAME = "PERIODE.TXT";

	public PeriodeExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("PERIODE");
	}

	@Override
	public void export(HubPeriode bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubPeriode> CONVERTER = new Converter<String, HubPeriode>() {


		@Override
		public String to(Context context, HubPeriode input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(STRING_CONVERTER.to(context, FIELDS.code,
					input.getCode(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.nom,
					input.getNom(), true));
			values.add(DATE_CONVERTER.to(context, FIELDS.date_debut,
					input.getDateDebut(), true));
			values.add(DATE_CONVERTER.to(context, FIELDS.date_fin,
					input.getDateFin(), true));
			StringBuffer list = new StringBuffer();
			for (Boolean jour : input.getCalendrier()) {
				list.append(BOOLEAN_CONVERTER.to(context, FIELDS.calendrier,
					jour, true));
			}
			values.add(list.toString());
			values.add(NUMBER_CONVERTER.to(context, FIELDS.identifiant,
					input.getIdentifiant(), true));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubPeriode> create(String path) throws IOException {
			return new PeriodeExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(PeriodeExporter.class.getName(), factory);
	}

}