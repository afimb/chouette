package mobi.chouette.exchange.regtopp.importer.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;

import java.io.File;
import java.util.List;

@AllArgsConstructor
public class ParseableFile<T> {
	@Override
	public String toString() {
		return "ParseableFile [file=" + file.getAbsolutePath()+"]";
	}

	@Getter
	private File file;

	@Getter
	private List<Class<T>> regtoppClasses;

	// Error to be returned when parsing detects an error
	@Getter
	private RegtoppException.ERROR invalidFieldValue;

}
