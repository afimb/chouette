package fr.certu.chouette.service.database;

import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRemoveLineTypeType;

public interface IExportManager 
{
	public static enum ExportMode  {CHOUETTE, NEPTUNE, GTFS, GEOPORTAIL};
	
	ChouettePTNetworkTypeType getExportParRegistration(final String registrationNumber);
	ChouettePTNetworkTypeType getExportParIdLigne(final Long idLigne);
	ChouettePTNetworkTypeType getExportParRegistration(final String registrationNumber, ExportMode exportMode);
	ChouettePTNetworkTypeType getExportParIdLigne(final Long idLigne, ExportMode exportMode);
	ChouetteRemoveLineTypeType getSuppressionParRegistration(final String registrationNumber);
	ChouetteRemoveLineTypeType getSuppressionParIdLigne(final Long idLigne);	
}