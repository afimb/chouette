package mobi.chouette.exchange.kml.exporter.writer;


public class AbstractWriter {

	public static String toXml(Object source) {
		if (source == null)
			return "";
		// return StringEscapeUtils.escapeXml(source.toString());
		return source.toString().replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
	
	public static boolean nonEmpty(String data)
	{
		return data != null && !data.isEmpty();
	}

}
