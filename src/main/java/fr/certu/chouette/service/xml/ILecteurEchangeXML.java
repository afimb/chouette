package fr.certu.chouette.service.xml;

import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.echange.ILectureEchange;

public interface ILecteurEchangeXML 
{
	public ILectureEchange lire(ChouettePTNetworkTypeType chouettePTNetwork);
}