package mobi.chouette.exchange.regtopp.importer.parser;

import java.io.File;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.report.FileInfo;

@AllArgsConstructor
public class ParseableFile<T> {
	@Override
	public String toString() {
		return "ParseableFile [file=" + file.getAbsolutePath() + ", regtoppClasses=" + regtoppClasses + ", fileInfo=" + fileInfo
				+ "]";
	}

	@Getter
	private File file;

	@Getter
	private List<Class<T>> regtoppClasses;

	// Error to be returned when parsing detects an error
	@Getter
	private RegtoppException.ERROR invalidFieldValue;

	@Getter
	private FileInfo fileInfo;

}
