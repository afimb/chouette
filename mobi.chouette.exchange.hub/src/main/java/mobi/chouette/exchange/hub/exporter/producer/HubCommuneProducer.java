/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.hub.exporter.producer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.hub.model.HubCommune;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.StopArea;


/**
 * convert Timetable to Hub Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class HubCommuneProducer extends AbstractProducer {

	private Set<HubCommune> communes = new HashSet<>();

	public HubCommuneProducer(HubExporterInterface exporter) {
		super(exporter);
	}
	

	public boolean addCity(StopArea neptuneObject) {

		HubCommune hubObject = new HubCommune();
		
		hubObject.setNom(neptuneObject.getCityName());
		if (neptuneObject.getCountryCode() != null) {
			hubObject.setCodeInsee(Integer.decode(neptuneObject.getCountryCode()));
		}
		hubObject.setNom(neptuneObject.getCityName());
		communes.add(hubObject);
		return true;
	}
	
	public boolean saveAll(ActionReport report)
	{

		try {
			List<HubCommune> listCommunes = new ArrayList<>(communes);
			Collections.sort(listCommunes,new Sorter());
			for (HubCommune hubObject : listCommunes) {
				getExporter().getCommuneExporter().export(hubObject);
				
			}
		} catch (IOException e) {
			log.error("fail to save commune",e);
			return false;
		}
		return true;
	}
	
	public class Sorter implements Comparator<HubCommune> {
		@Override
		public int compare(HubCommune arg0, HubCommune arg1) {
			return arg0.getCodeInsee()- arg1.getCodeInsee();
		}
	}

}
