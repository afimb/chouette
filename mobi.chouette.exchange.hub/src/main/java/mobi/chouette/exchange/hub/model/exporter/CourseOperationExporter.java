package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.hub.model.HubCourseOperation;

public class CourseOperationExporter extends ExporterImpl<HubCourseOperation> implements
		HubConverter {

	public static enum FIELDS {
		numero_course, code_operation, code_lot,
		code_activite, mode_transport, libre1, libre2, libre3 ;
	};

	public static final String FILENAME = "COURSE_OPERATION.TXT";

	public CourseOperationExporter(String path) throws IOException {
		super(path);
	}

	@Override
	public void writeHeader() throws IOException {
		write("COURSE_OPERATION");
	}

	@Override
	public void export(HubCourseOperation bean) throws IOException {
		write(CONVERTER.to(_context, bean));
	}

	public static Converter<String, HubCourseOperation> CONVERTER = new Converter<String, HubCourseOperation>() {


		@Override
		public String to(Context context, HubCourseOperation input) {
			String result = null;
			List<String> values = new ArrayList<String>();
			values.add(NUMBER_CONVERTER.to(context, FIELDS.numero_course,
					input.getNumeroCourse(), true));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_operation,
					input.getCodeOperation(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_lot,
					input.getCodeLot(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.code_activite,
					input.getCodeActivite(), false));
			values.add(ENUM_CONVERTER.to(context, FIELDS.mode_transport,
					input.getModeTransport(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.libre1,
					input.getLibre1(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.libre2,
					input.getLibre2(), false));
			values.add(STRING_CONVERTER.to(context, FIELDS.libre3,
					input.getLibre3(), false));
			result = Tokenizer.untokenize(values);
			return result;
		}

	};

	public static class DefaultExporterFactory extends ExporterFactory {

		@Override
		protected Exporter<HubCourseOperation> create(String path) throws IOException {
			return new CourseOperationExporter(path);
		}
	}

	static {
		ExporterFactory factory = new DefaultExporterFactory();
		ExporterFactory.factories.put(CourseOperationExporter.class.getName(), factory);
	}

}