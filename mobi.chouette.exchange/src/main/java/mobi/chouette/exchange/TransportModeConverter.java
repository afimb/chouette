package mobi.chouette.exchange;

import mobi.chouette.common.TransportMode;
/**
 * Interface permettant de gérer les conversions entre les modes de transports des différents format d'échange
 * @author gjamot
 *
 */
public interface TransportModeConverter {
	public final static String CODE ="code";
	public final static String MODE ="mode";
	public final static String SUBMODE ="submode";
	public final static String PIVOT ="pivot";
	public final static String PIVOT_SUBMODE ="pivot-submode";
	
	TransportMode genericToSpecificMode(TransportMode importMode);
	TransportMode specificToGenericMode(TransportMode specificMode);
}
