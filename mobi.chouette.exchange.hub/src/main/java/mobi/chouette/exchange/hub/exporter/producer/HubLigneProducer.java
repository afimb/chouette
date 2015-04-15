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
import mobi.chouette.exchange.hub.model.HubLigne;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Line;


/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubLigneProducer extends AbstractProducer {
	public HubLigneProducer(HubExporterInterface exporter) {
		super(exporter);
	}
	
	private HubLigne hubObject = new HubLigne();

	public boolean save(Line neptuneObject, ActionReport report) {

		hubObject.clear();
		hubObject.setCode(toHubId(neptuneObject));
		
		hubObject.setCodeCommercial(neptuneObject.getNumber());
		hubObject.setNom(neptuneObject.getName());
		
	    hubObject.setCodeTransporteur(toHubId(neptuneObject.getCompany()));
		
	    hubObject.setCodeReseau(toHubId(neptuneObject.getNetwork()));
	    
		if (!neptuneObject.getGroupOfLines().isEmpty())
		{
			hubObject.setCodeGroupeDeLigne(toHubId(neptuneObject.getGroupOfLines().get(0)));
		}

		hubObject.setIdentifiant(toInt(neptuneObject.getRegistrationNumber()));

		try {
			getExporter().getLigneExporter().export(hubObject);
		} catch (IOException e) {
			log.error("fail to save ligne",e);
			return false;
		}
		return true;
	}

}
