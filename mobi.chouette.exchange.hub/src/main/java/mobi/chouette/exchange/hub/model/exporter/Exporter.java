package mobi.chouette.exchange.hub.model.exporter;

import java.io.IOException;

public interface Exporter<T> {
	void dispose(mobi.chouette.common.Context context) throws IOException;

	void writeHeader() throws IOException;

	void export(T bean) throws IOException;

	void write(String text) throws IOException;

}
