package mobi.chouette.exchange;

import mobi.chouette.common.TransportMode;

/**
 * Classe permettant de gérer les conversions entre les modes de transports des différents format d'échange
 * @author gjamot
 *
 */
public abstract class AbstractTransportModeConverter implements TransportModeConverter{
	
	
	
	@Override
	public TransportMode genericToSpecificMode(TransportMode importMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TransportMode specificToGenericMode(TransportMode specificMode) {
		throw new UnsupportedOperationException();
	}

}
