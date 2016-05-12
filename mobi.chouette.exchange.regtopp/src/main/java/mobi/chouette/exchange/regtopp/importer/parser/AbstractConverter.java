package mobi.chouette.exchange.regtopp.importer.parser;

public abstract class AbstractConverter {

	public static String composeObjectId(String prefix, String type, String id) {
		return prefix + ":" + type + ":" + id.trim();
	}
	
	public static String extractOriginalId(String chouetteObjectId) {
		return chouetteObjectId.split(":")[2];
	}

}
