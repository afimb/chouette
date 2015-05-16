package mobi.chouette.exchange.netex.exporter.writer;

import java.text.SimpleDateFormat;
import java.util.Collection;

import org.apache.commons.lang.StringEscapeUtils;

public class AbstractWriter {

	// public static final DatatypeFactory durationFactory =
	// DatatypeFactory.newInstance();

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public static final SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	public static boolean isSet(Object... objects) {
		for (Object val : objects) {
			if (val != null) {
				if (val instanceof String) {
					if (!((String) val).isEmpty())
						return true;
				} else {
					return true;
				}
			}
		}
		return false;

	}

	public static String toXml(Object source) {
		if (source == null)
			return "";
		return StringEscapeUtils.escapeXml(source.toString());
	}
	
	public static boolean nonEmpty(Collection<?> list)
	{
		return list != null && list.size()>0;
	}

}
