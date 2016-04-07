package mobi.chouette.exchange.kml.exporter;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import javax.xml.datatype.DatatypeConfigurationException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.exchange.kml.exporter.writer.KmlDataWriter;

import org.apache.commons.io.output.FileWriterWithEncoding;

@Log4j
public class KmlFileWriter implements Constant {
	

	public KmlFileWriter() {
	}

	public File writeXmlFile(KmlData data, File file) throws IOException, DatatypeConfigurationException {
		Writer output = new FileWriterWithEncoding(file, "UTF-8");
		KmlDataWriter.write(output, data);
		output.close();
		log.debug("File : " + file.getName() + "created");
		return file;
	}


}
