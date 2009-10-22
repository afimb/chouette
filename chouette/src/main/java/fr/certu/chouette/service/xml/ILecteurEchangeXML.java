package fr.certu.chouette.service.xml;

import chouette.schema.ChouettePTNetworkType;
import fr.certu.chouette.echange.ILectureEchange;

public interface ILecteurEchangeXML {

	public ILectureEchange lire(ChouettePTNetworkType chouettePTNetwork);
}