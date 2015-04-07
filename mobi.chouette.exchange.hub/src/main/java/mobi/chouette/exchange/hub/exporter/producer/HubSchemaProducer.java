/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.hub.exporter.producer;

import mobi.chouette.exchange.hub.model.HubSchema;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;


/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class HubSchemaProducer extends AbstractProducer {
	public HubSchemaProducer(HubExporterInterface exporter) {
		super(exporter);
	}
	
	private HubSchema hubObject = new HubSchema();

	public boolean save(Route neptuneObject, ActionReport report) {

		hubObject.clear();
		hubObject.setCodeLigne(toHubId(neptuneObject.getLine()));
		
		
		hubObject.setSens(toSens(neptuneObject.getWayBack()));
		hubObject.setIdentifiant(Integer.parseInt(toHubId(neptuneObject)));
		
        for (StopPoint point : neptuneObject.getStopPoints()) 
        {
        	HubSchema.ArretSchema arret = hubObject.new ArretSchema();
        	hubObject.getArrets().add(arret);
        	arret.setCode(toHubId(point.getContainedInStopArea()));
        	arret.setIdentifiant(point.getContainedInStopArea().getId());
		}
		
		try {
			getExporter().getSchemaExporter().export(hubObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
