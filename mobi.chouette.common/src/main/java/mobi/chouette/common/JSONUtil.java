package mobi.chouette.common;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;

@Log4j
public class JSONUtil {

	public static <T> T fromJSON(Path path, Class<T> type) throws IOException, JAXBException, JSONException, XMLStreamException {
			byte[] bytes = Files.readAllBytes(path);
			String text = new String(bytes, "UTF-8");
			return fromJSON(text, type);
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromJSON(String text, Class<T> type) throws JAXBException, JSONException, XMLStreamException {

		
			JAXBContext context = JAXBContext.newInstance(type);
			JSONObject object = new JSONObject(text);
			Configuration config = new Configuration();
			MappedNamespaceConvention convention = new MappedNamespaceConvention(
					config);
			XMLStreamReader reader = new MappedXMLStreamReader(object,
					convention);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (T) unmarshaller.unmarshal(reader);

	}

	public static <T> String toJSON(T payload) throws JAXBException {
			JAXBContext context = JAXBContext.newInstance(payload.getClass());
			Configuration config = new Configuration();
			config.setAttributeKey("");
			MappedNamespaceConvention convention = new MappedNamespaceConvention(
					config);
			StringWriter out = new StringWriter();
			XMLStreamWriter writer = new MappedXMLStreamWriter(convention, out);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(payload, writer);
			return  out.toString();

	}

	public static <T> void toJSON(Path path, T payload) throws JAXBException, IOException {
		String data = JSONUtil.toJSON(payload);
			FileUtils.writeStringToFile(path.toFile(), data);
			return ;
	}


}
