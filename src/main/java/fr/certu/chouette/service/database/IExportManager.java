package fr.certu.chouette.service.database;

import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRemoveLineTypeType;

public interface IExportManager 
{

	ChouettePTNetworkTypeType getExportParRegistration(final String registrationNumber);
	ChouettePTNetworkTypeType getExportParIdLigne(final Long idLigne);
	ChouetteRemoveLineTypeType getSuppressionParRegistration(final String registrationNumber);
	ChouetteRemoveLineTypeType getSuppressionParIdLigne(final Long idLigne);	
}