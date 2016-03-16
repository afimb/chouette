package mobi.chouette.exchange.kml.exporter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.generic.EscapeTool;

@Log4j
public class KmlFileWriterVelocity implements Constant {
	
	public static String LOGGER_NAME = "velocityiev";
	
	private VelocityEngine velocityEngine;
	// Prepare the model for velocity
	private Map<String, Object> model = new HashMap<String, Object>();

	public KmlFileWriterVelocity() {
		Logger.getLogger( LOGGER_NAME );
		velocityEngine = new VelocityEngine();
		velocityEngine.addProperty("resource.loader", "classpath");
		velocityEngine.addProperty("classpath.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
			      "org.apache.velocity.runtime.log.Log4JLogChute" );
		velocityEngine.setProperty("runtime.log.logsystem.log4j.logger",
                LOGGER_NAME);
	}

	private void prepareModel(KmlData data) throws DatatypeConfigurationException {
		model.clear();
		model.put("esc", new EscapeTool());

		model.put("data", data);
	}

	public File writeXmlFile(KmlData data, File file) throws IOException, DatatypeConfigurationException {
		// Prepare the model for velocity
		prepareModel(data);

		StringWriter output = new StringWriter();
		VelocityContext velocityContext = new VelocityContext(model);
		velocityContext.put("esc", new EscapeTool());

		velocityEngine.mergeTemplate("templates/placeholder.vm", "UTF-8", velocityContext, output);

		FileUtils.write(file, output.toString(), "UTF-8");

		log.debug("File : " + file.getName() + "created");

		return file;
	}


}
