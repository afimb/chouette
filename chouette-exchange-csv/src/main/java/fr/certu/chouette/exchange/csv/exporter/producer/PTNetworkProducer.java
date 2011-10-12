package fr.certu.chouette.exchange.csv.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.model.neptune.PTNetwork;

public class PTNetworkProducer extends AbstractCSVNeptuneProducer<PTNetwork> {

	public static final String  PTNETWORK_NAME_TITLE = "Nom du réseau";
	public static final String  CODE_TITLE           = "Code Réseau";
	public static final String  DESCRIPTION_TITLE    = "Description du réseau";
	   
	@Override
	public List<String[]> produce(PTNetwork ptNetwork) {
		List<String[]> csvLinesList = new ArrayList<String[]>();
		csvLinesList.add(createCSVLine(PTNETWORK_NAME_TITLE, ptNetwork.getName()));
		csvLinesList.add(createCSVLine(CODE_TITLE, ptNetwork.getRegistrationNumber()));
		csvLinesList.add(createCSVLine(DESCRIPTION_TITLE, ptNetwork.getDescription()));

		return csvLinesList;
	}

}
