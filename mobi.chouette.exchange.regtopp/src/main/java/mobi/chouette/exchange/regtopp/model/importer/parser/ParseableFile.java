package mobi.chouette.exchange.regtopp.model.importer.parser;

import java.io.File;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mobi.chouette.exchange.report.FileInfo;

@AllArgsConstructor
public class ParseableFile {
	@Getter
	private File file;
	
	@Getter
	private List<Class> regtoppClasses;
	
	@Getter
	private FileInfo fileInfo;
	
	
}
