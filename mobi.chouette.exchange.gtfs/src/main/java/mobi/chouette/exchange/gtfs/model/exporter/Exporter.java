package mobi.chouette.exchange.gtfs.model.exporter;

import java.io.IOException;

import mobi.chouette.exchange.report.ActionReport;

public interface Exporter<T> {
	void dispose(ActionReport report) throws IOException;

	void writeHeader() throws IOException;

	void export(T bean) throws IOException;

	void write(String text) throws IOException;

}
