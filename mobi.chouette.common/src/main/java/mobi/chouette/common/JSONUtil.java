package mobi.chouette.common;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;

public class JSONUtil {

	public static <T> T fromJSON(Path path, Class<T> type) throws IOException, JAXBException, JSONException,
			XMLStreamException {
		byte[] bytes = Files.readAllBytes(path);
		String text = new String(bytes, "UTF-8");
		return fromJSON(text, type);
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromJSON(String text, Class<T> type) throws JAXBException, JSONException, XMLStreamException {

		if (text == null || text.isEmpty() ) return null;
		JAXBContext context = JAXBContext.newInstance(type);
		JSONObject object = new JSONObject(text);
		Configuration config = new Configuration();
		MappedNamespaceConvention convention = new MappedNamespaceConvention(config);
		XMLStreamReader reader = new MappedXMLStreamReader(object, convention);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (T) unmarshaller.unmarshal(reader);

	}

	public static <T> String toJSON(T payload) throws JAXBException, JSONException {
		JAXBContext context = JAXBContext.newInstance(payload.getClass());
		Configuration config = new Configuration();
		config.setAttributeKey("");
		MappedNamespaceConvention convention = new MappedNamespaceConvention(config);
		StringWriter out = new StringWriter();
		MappedXMLStreamWriter writer = new MappedXMLStreamWriter(convention, out);

		List<String> lists = getListElements(payload.getClass());
		for (int iCount = 0; iCount < lists.size(); iCount++) {
			writer.serializeAsArray(lists.get(iCount));
		}

		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(payload, writer);
		JSONObject json = new JSONObject(out.toString());
		return json.toString(2);

	}

	public static <T> void toJSON(Path path, T payload) throws JAXBException, IOException, JSONException {
		String data = JSONUtil.toJSON(payload);
		FileUtils.writeStringToFile(path.toFile(), data);
		return;
	}

	/**
	 * Returns all the list elements in the class ... This is called
	 * recursively.
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getListElements(final Class<?> obj) {
		List<String> ret = new ArrayList<String>();

		Field[] fields = obj.getDeclaredFields();

		for (int iCount = 0; iCount < fields.length; iCount++) {

			Annotation[] annotation = fields[iCount].getAnnotations();

			for (int iLoop = 0; iLoop < annotation.length; iLoop++) {

				if (annotation[iLoop] instanceof javax.xml.bind.annotation.XmlElement) {
					javax.xml.bind.annotation.XmlElement xmlAnn = (javax.xml.bind.annotation.XmlElement) annotation[iLoop];

					String value = xmlAnn.name();
					Class<?> returnType = fields[iCount].getType();
					Type genericRetType = fields[iCount].getGenericType();
					if (genericRetType instanceof ParameterizedType
							&& (returnType.isInterface() && returnType.getName().equals("java.util.List"))) {
						ret.add(value);

						ParameterizedType parType = (ParameterizedType) genericRetType;
						Type[] actualType = parType.getActualTypeArguments();
						if (actualType.length == 0) {
							continue;
						}

						if (actualType[0] instanceof Class) {
							Class<?> typeClass = (Class) actualType[0];
							if (!typeClass.isPrimitive() && !typeClass.getName().equals("java.lang.String")
									&& !typeClass.getName().equals(obj.getName())) {
								List<String> names = getListElements(typeClass);
								ret.addAll(names);
							}

						}

					} else if (!returnType.isPrimitive() && !returnType.getName().equals("java.lang.String")
							&& !returnType.getName().equals(obj.getName())) {
						List<String> names = getListElements(fields[iCount].getType());
						ret.addAll(names);
					}

				}
			}

		}

		return ret;
	}

}
