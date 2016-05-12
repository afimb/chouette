package mobi.chouette.exchange.geojson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class JAXBSerializer {

	public static void writeTo(Object target, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		writeTo(target, out);
	}

	public static void writeTo(Object target, OutputStream out)
			throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(
				TypeFactory.defaultInstance());
		mapper.setAnnotationIntrospector(introspector);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		ObjectWriter writer = mapper.writer();
		writer.writeValue(out, target);
		out.close();
	}

}
