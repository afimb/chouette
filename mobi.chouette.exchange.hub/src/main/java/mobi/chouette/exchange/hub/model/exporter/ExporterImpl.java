package mobi.chouette.exchange.hub.model.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;


public abstract class ExporterImpl<T> implements Exporter<T> {

	public static final char DELIMITER = ';';

	private BufferedWriter _writer;

	protected HubContext _context;

	private int _total;

	public ExporterImpl(String name) throws IOException {
		_context = new HubContext();
		_context.put(HubContext.PATH, new File(name).getName());
		Path path = Paths.get(name);
		_writer = Files.newBufferedWriter(path, Charset.forName("Cp1252")); // ISO8859_15 ?
		writeHeader();
		_total = 1;
	}

	@Override
	public void write(String text) throws IOException {
		_writer.write(text);
		_writer.write("\r\n");
		_context.put(HubContext.ID, _total++);
	}

	@Override
	public void dispose(mobi.chouette.common.Context context) throws IOException {
		_writer.close();
		// add file info
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		reporter.addFileReport(context, Paths.get((String) _context.get(HubContext.PATH)).getFileName().toString(), IO_TYPE.OUTPUT);
	}

	@SuppressWarnings("rawtypes")
	void write(Enum[] values) throws IOException {
		StringBuilder builder = new StringBuilder();
		final int length = values.length;
		for (int i = 0; i < length; i++) {
			Enum field = values[i];
			builder.append(field.name());
			if (i + 1 < length) {
				builder.append(DELIMITER);
			}
		}
		write(builder.toString());

	}

}
