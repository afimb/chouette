package mobi.chouette.exchange.gtfs.model.exporter;

import java.io.IOException;

import mobi.chouette.common.Context;

public interface Exporter<T> {
	void dispose(Context context) throws IOException;

	void writeHeader() throws IOException;

	void export(T bean) throws IOException;

	void write(String text) throws IOException;

}
