package mobi.chouette.exchange.gtfs.model.exporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import mobi.chouette.exchange.gtfs.model.importer.Context;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;

public abstract class ExporterImpl<T> implements Exporter<T> {

	public static final char DELIMITER = ',';

	private BufferedWriter _writer;

	protected Context _context;

	private int _total;

	public ExporterImpl(String name) throws IOException {
		_context = new Context();
		_context.put(Context.PATH, name);
		Path path = Paths.get(name);
		_writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
		writeHeader();
		_total = 1;
	}

	@Override
	public void write(String text) throws IOException {
		_writer.write(text);
		_writer.write("\r\n");
		_context.put(Context.ID, _total++);
	}

	@Override
	public void dispose(mobi.chouette.common.Context context) throws IOException {
		_writer.close();
		// add file info
		
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		reporter.addFileReport(context, Paths.get((String) _context.get(Context.PATH)).getFileName().toString(), IO_TYPE.OUTPUT);
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
