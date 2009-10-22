package fr.certu.chouette.service.database;

import chouette.schema.ChouettePTNetworkType;
import chouette.schema.ChouetteRemoveLineType;

public interface IExportManager {

	ChouettePTNetworkType getExportParRegistration(
			final String registrationNumber);
	ChouettePTNetworkType getExportParIdLigne(
			final Long idLigne);
	ChouetteRemoveLineType getSuppressionParRegistration( 
			final String registrationNumber);
	ChouetteRemoveLineType getSuppressionParIdLigne( 
			final Long idLigne);

}