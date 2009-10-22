package fr.certu.chouette.service.amivif;

import amivif.schema.RespPTDestrLineType;
import amivif.schema.RespPTLineStructTimetableType;
import chouette.schema.ChouettePTNetworkType;
import chouette.schema.ChouetteRemoveLineType;

public interface IAmivifAdapter {

	ChouettePTNetworkType getATC(RespPTLineStructTimetableType amivif);

	RespPTLineStructTimetableType getCTA(ChouettePTNetworkType chouette);

	RespPTDestrLineType getCTA(ChouetteRemoveLineType chouette);
}