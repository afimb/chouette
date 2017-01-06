package mobi.chouette.exchange;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.PropertyNames;
import mobi.chouette.common.TransportMode;

/**
 * Classe permettant de gérer les conversions entre les modes de transports des différents format d'échange
 * @author gjamot
 *
 */
@Log4j
public abstract class AbstractTransportModeConverter implements TransportModeConverter{
	
	
	
	@Override
	public TransportMode genericToSpecificMode(TransportMode importMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TransportMode specificToGenericMode(TransportMode specificMode) {
		throw new UnsupportedOperationException();
	}
	
	protected static String getTransportModeUrl(String fileName) {
		String url = null;
		System.setProperty("iev" + PropertyNames.URL_TRANSPORT_MODE, "https://raw.githubusercontent.com/afimb/chouette-projects-i18n/master/data/transport_mode/");
		url = System.getProperty("iev" + PropertyNames.URL_TRANSPORT_MODE);
		url = url + fileName;
		
		return url;
	}
	

}
