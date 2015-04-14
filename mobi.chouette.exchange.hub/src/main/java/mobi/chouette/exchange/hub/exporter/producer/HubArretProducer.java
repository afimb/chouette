/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.hub.exporter.producer;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.hub.model.HubArret;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.Coordinate;

/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubArretProducer extends AbstractProducer {
	public HubArretProducer(HubExporterInterface exporter) {
		super(exporter);
	}


	private static final String TYPE_GENERIQUE = "ONNNNNNNNNNNNNNNNN"; 
	private static final String TYPE_GENERIQUE_PMR = "ONNNNNNNNNNNNONNNN"; 
	private static final String TYPE_PHYSIQUE = "NNNNNNNNNNNNNNNNNN"; 
	private static final String TYPE_PHYSIQUE_PMR = "NNNNNNNNNNNNNONNNN"; 


	private HubArret hubObject = new HubArret();

	public boolean save(StopArea neptuneObject, ActionReport report) {
		// ignore StopPlaces or ITL area
		if (neptuneObject.getAreaType().equals(ChouetteAreaEnum.StopPlace)
				|| neptuneObject.getAreaType().equals(ChouetteAreaEnum.ITL))
			return true;

		hubObject.clear();
		hubObject.setCode(toHubId(neptuneObject));

		String name = neptuneObject.getName();
		if (name.trim().isEmpty()) {
			log.error("no name for " + neptuneObject.getObjectId());
			// HubReportItem item = new HubReportItem(
			// HubReportItem.KEY.MISSING_DATA, STATE.ERROR, "Nom",
			// neptuneObject.getObjectId(), "Name");
			// report.addItem(item);
			return false;
		}

		hubObject.setNom(name);
		hubObject.setDescription(neptuneObject.getNearestTopicName());

		String type = TYPE_PHYSIQUE;
		if (neptuneObject.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint)) {
			if (isTrue(neptuneObject.getMobilityRestrictedSuitable())) {
				type = TYPE_GENERIQUE_PMR;
			} else {
				type = TYPE_GENERIQUE;
			}
		} else if (isTrue(neptuneObject.getMobilityRestrictedSuitable())) {
			type = TYPE_PHYSIQUE_PMR;
		}
		hubObject.setType(type);

		// parent id
		if (neptuneObject.getAreaType().equals(ChouetteAreaEnum.BoardingPosition)
				|| neptuneObject.getAreaType().equals(ChouetteAreaEnum.Quay)) {
			hubObject.setNomReduit(toHubId(neptuneObject.getParent()));
		}

		// X et Y sur arrêt physique uniquement
		if (neptuneObject.getAreaType().equals(ChouetteAreaEnum.BoardingPosition) 
				|| neptuneObject.getAreaType().equals(ChouetteAreaEnum.Quay))
		{
			if (neptuneObject.hasCoordinates())
			{
				neptuneObject.toProjection(Coordinate.LAMBERT);
				if (neptuneObject.hasProjection()) {
					hubObject.setX(neptuneObject.getX().intValue());
					hubObject.setY(neptuneObject.getY().intValue());
				}
			}
			else
			{
				hubObject.setX(-1);
				hubObject.setY(-1);
			}
		}
		hubObject.setCommune(neptuneObject.getCityName());
		if (neptuneObject.getCountryCode() != null) {
			hubObject.setCodeInsee(Integer.decode(neptuneObject.getCountryCode()));
		}
		hubObject.setCommentaire(neptuneObject.getComment());

		if (neptuneObject.getAreaType().equals(ChouetteAreaEnum.BoardingPosition)
				|| neptuneObject.getAreaType().equals(ChouetteAreaEnum.Quay)) {

			hubObject.setIdentifiant(toInt(neptuneObject.getRegistrationNumber()));

		}
		try {
			getExporter().getArretExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save arret",e);
			return false;
		}
		return true;
	}

}
