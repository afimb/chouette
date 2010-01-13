package fr.certu.chouette.service.amivif;

import java.io.File;

import amivif.schema.RespPTDestrLineType;
import amivif.schema.RespPTLineStructTimetable;
import amivif.schema.RespPTLineStructTimetableType;

public interface ILecteurAmivifXML {

	RespPTLineStructTimetable lire(String fileName);
	void ecrire(RespPTLineStructTimetableType amivif, File file);
	void ecrire(RespPTDestrLineType amivif, File file);
}