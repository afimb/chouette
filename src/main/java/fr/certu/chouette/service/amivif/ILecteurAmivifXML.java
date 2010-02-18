package fr.certu.chouette.service.amivif;

import java.io.File;

import amivif.schema.RespPTDestrLineTypeType;
import amivif.schema.RespPTLineStructTimetableTypeType;
import amivif.schema.RespPTLineStructTimetable;

public interface ILecteurAmivifXML 
{

	RespPTLineStructTimetable lire(String fileName);
	void ecrire(RespPTLineStructTimetableTypeType amivif, File file);
	void ecrire(RespPTDestrLineTypeType amivif, File file);
}