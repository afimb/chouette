package mobi.chouette.common;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import lombok.extern.log4j.Log4j;

import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;

@Log4j
public class JSONUtils {

	public static <T> T fromJSON(Path path, Class<T> type) {
		T result = null;

		try {
			byte[] bytes = Files.readAllBytes(path);
			String text = new String(bytes, "UTF-8");
			return fromJSON(text, type);
		} catch (Exception e) {
			log.error(e);
		}

		return result;
	}

	public static <T> T fromJSON(String text, Class<T> type) {
		T result = null;

		try {
			JAXBContext context = JAXBContext.newInstance(type);
			JSONObject object = new JSONObject(text);
			Configuration config = new Configuration();
			MappedNamespaceConvention convention = new MappedNamespaceConvention(
					config);
			XMLStreamReader reader = new MappedXMLStreamReader(object,
					convention);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			result = (T) unmarshaller.unmarshal(reader);
		} catch (Exception e) {
			log.error(e);
		}

		return result;
	}

	public static <T> String toJSON(T payload) {
		String result = null;
		try {
			JAXBContext context = JAXBContext.newInstance(payload.getClass());
			Configuration config = new Configuration();
			config.setAttributeKey("");
			MappedNamespaceConvention convention = new MappedNamespaceConvention(
					config);
			StringWriter out = new StringWriter();
			XMLStreamWriter writer = new MappedXMLStreamWriter(convention, out);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(payload, writer);
			result = out.toString();
		} catch (JAXBException e) {
			log.error(e);
		}
		return result;

	}

}
