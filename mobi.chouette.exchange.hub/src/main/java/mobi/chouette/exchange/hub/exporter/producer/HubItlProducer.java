/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.hub.exporter.producer;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.hub.model.HubItl;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;

/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubItlProducer extends AbstractProducer {
	
	private int compteur = 1;
	
	public HubItlProducer(HubExporterInterface exporter) {
		super(exporter);
	}


	private HubItl hubObject = new HubItl();

	public boolean save(StopPoint neptuneObject, ActionReport report) {
		// ignore StopPoint without itl information
		if (neptuneObject.getForAlighting() == null && neptuneObject.getForBoarding() == null)
			return true;

		boolean itlValid = false;
		hubObject.clear();
		if (neptuneObject.getForAlighting() != null && neptuneObject.getForAlighting().equals(AlightingPossibilityEnum.forbidden))
		{
			itlValid = true;
			hubObject.setType(HubItl.ITL_DESCENTE);
		}
		else if (neptuneObject.getForBoarding() != null && neptuneObject.getForBoarding().equals(BoardingPossibilityEnum.forbidden))
		{
			itlValid = true;
			hubObject.setType(HubItl.ITL_MONTEE);
		}
		
		// pas d'ITL, rien à sauvegarder
		if (!itlValid) return true;

		hubObject.setCodeLigne(toHubId(neptuneObject.getRoute().getLine()));

		hubObject.setSens(toSens(neptuneObject.getRoute().getWayBack()));
		hubObject.setCodeArret(toHubId(neptuneObject.getContainedInStopArea()));
		hubObject.setIdentifiantArret(toInt(neptuneObject.getContainedInStopArea().getRegistrationNumber()));
		

		hubObject.setOrdre(neptuneObject.getPosition().intValue());
		
		hubObject.setIdentifiant(compteur++);
		
		try {
			getExporter().getItlExporter().export(hubObject);
		} catch (Exception e) {
	          log.error("fail to produce ITL "+e.getClass().getName()+" "+e.getMessage());
			return false;
		}
		return true;
	}

}
