package mobi.chouette.common;

import java.util.HashMap;

import lombok.extern.log4j.Log4j;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
@Log4j
public class JSONUtils {
	
	
	
	public static Object fromJSON(String text, Class type) {
		Object result = null;
		try {
			ObjectMapper mapper = createObjectMapper();
			result = mapper.readValue(text, type);
			// result = mapper.convertValue(text, type);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	public static String toJSON(Object payload) {
		String result = null;
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(payload.getClass().getSimpleName(), payload);
			ObjectMapper mapper = createObjectMapper();
			result = mapper.writeValueAsString(map);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	public static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
		mapper.setAnnotationIntrospector(introspector);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.setSerializationInclusion(Include.NON_NULL);

		return mapper;

	}
	

//	public static Object fromJSON(String text, Class type) {
//		Object result = null;
//
//		try {
//			JAXBContext context = JAXBContext.newInstance(type);
//			Unmarshaller unmarshaller = context.createUnmarshaller();
//			result = unmarshaller.unmarshal(new StringReader(text));
//		} catch (JAXBException e) {
//			log.error(e);
//		}
//
//		return result;
//	}
//
//	public static String toJSON(Object payload) {
//		String result = null;
//		try {
//			JAXBContext context = JAXBContext.newInstance(payload.getClass());
//			
//			Marshaller marshaller = context.createMarshaller();
//			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
//			 
//			
//			StringWriter out = new StringWriter();
//			marshaller.marshal(payload, out);
//			result = out.toString();
//		} catch (JAXBException e) {
//			log.error(e);
//		}
//		return result;
//		
//	}
	
	
}
