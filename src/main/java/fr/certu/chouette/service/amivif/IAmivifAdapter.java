package fr.certu.chouette.service.amivif;

import amivif.schema.RespPTDestrLineTypeType;
import amivif.schema.RespPTLineStructTimetableTypeType;
import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRemoveLineTypeType;

public interface IAmivifAdapter 
{
	ChouettePTNetworkTypeType getATC(RespPTLineStructTimetableTypeType amivif);

	RespPTLineStructTimetableTypeType getCTA(ChouettePTNetworkTypeType chouette);

	RespPTDestrLineTypeType getCTA(ChouetteRemoveLineTypeType chouette);
}