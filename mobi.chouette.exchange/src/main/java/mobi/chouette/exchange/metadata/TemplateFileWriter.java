package mobi.chouette.exchange.metadata;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.datatype.DatatypeConfigurationException;

import lombok.Getter;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.generic.EscapeTool;

public abstract class TemplateFileWriter {

	public static String LOGGER_NAME = "velocityiev";

	private static final Logger logger = Logger.getLogger(TemplateFileWriter.class);
	@Getter
	private VelocityEngine velocityEngine;
	// Prepare the model for velocity
	@Getter
	protected Map<String, Object> model = new HashMap<String, Object>();

	public TemplateFileWriter() {
		Logger.getLogger( LOGGER_NAME );
		velocityEngine = new VelocityEngine();
		velocityEngine.addProperty("resource.loader", "classpath");
		velocityEngine.addProperty("classpath.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
				"org.apache.velocity.runtime.log.Log4JLogChute");
		velocityEngine.setProperty("runtime.log.logsystem.log4j.logger",
                LOGGER_NAME);
	}

	protected ZipEntry writeZipEntry(String entryName, String templateName, ZipOutputStream zipFile)
			throws IOException, DatatypeConfigurationException {
		// Prepare the model for velocity

		StringWriter output = new StringWriter();

		VelocityContext velocityContext = new VelocityContext(model);
		velocityContext.put("esc", new EscapeTool());

		velocityEngine.mergeTemplate(templateName, "UTF-8", velocityContext, output);

		// Add ZIP entry to zipFileput stream.
		ZipEntry entry = new ZipEntry(entryName);
		zipFile.putNextEntry(entry);

		zipFile.write(output.toString().getBytes("UTF-8"));

		// Complete the entry
		zipFile.closeEntry();

		return entry;
	}

	protected File writePlainFile(File directory, String filename, String templateName) throws IOException,
			DatatypeConfigurationException {

		StringWriter output = new StringWriter();
		VelocityContext velocityContext = new VelocityContext(model);
		velocityContext.put("esc", new EscapeTool());

		velocityEngine.mergeTemplate(templateName, "UTF-8", velocityContext, output);

		File file = new File(directory, filename);
		FileUtils.write(file, output.toString(), "UTF-8");

		logger.debug("File : " + filename + "created");

		return file;
	}

}
