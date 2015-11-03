package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubMissionOperation;

public class MissionOperationExporter extends ExporterImpl<HubMissionOperation> implements
		HubConverter {

	public static enum FIELDS {
	    numero_mission, code_operation, code_lot, code_activite,
	    champ_libre_1, champ_libre_2, champ_libre_3;
	};

	public static final String FILENAME = "MISSION_OPERATION.TXT";

	public MissionOperationExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("MISSION_OPERATION");
	}

	@Override
	public void export(HubMissionOperation bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubMissionOperation> CONVERTER = new Converter<String, HubMissionOperation>() {


		@Override
		public String to(Context context, HubMissionOperation input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(NUMBER_CONVERTER.to(context, FIELDS.numero_mission, input.getNumeroMission(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_operation, input.getCodeOperation(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_lot, input.getCodeLot(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_activite, input.getCodeActivite(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.champ_libre_1, input.getChampLibre1(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.champ_libre_2, input.getChampLibre2(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.champ_libre_3, input.getChampLibre3(), false));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubMissionOperation> create(String path) throws IOException {
			return new MissionOperationExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(MissionOperationExporter.class.getName(), factory);
	}

}
