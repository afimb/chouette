/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.hub.exporter.producer;

import mobi.chouette.exchange.hub.model.HubLigne;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Line;


/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
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

		hubObject.setIdentifiant(neptuneObject.getId());

		try {
			getExporter().getLigneExporter().export(hubObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
