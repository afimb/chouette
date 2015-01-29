package mobi.chouette.common;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import lombok.extern.log4j.Log4j;

@Log4j
public class JSONUtils {

	public static Object fromJSON(String text, Class type) {
		Object result = null;

		try {
			JAXBContext context = JAXBContext.newInstance(type.getPackage()
					.getName());
			Unmarshaller unmarshaller = context.createUnmarshaller();
			result = unmarshaller.unmarshal(new StringReader(text));
		} catch (JAXBException e) {
			log.error(e);
		}

		return result;
	}

	public static String toJSON(Object payload) {
		String result = null;
		try {
			JAXBContext context = JAXBContext.newInstance(payload.getClass().getPackage().getName());
			
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
			 
			
			StringWriter out = new StringWriter();
			marshaller.marshal(payload, out);
			result = out.toString();
		} catch (JAXBException e) {
			log.error(e);
		}
		return result;
		
	}
}
