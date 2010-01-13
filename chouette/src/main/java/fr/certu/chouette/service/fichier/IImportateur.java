package fr.certu.chouette.service.fichier;

import fr.certu.chouette.echange.ILectureEchange;

public interface IImportateur {
	
	public abstract void importer(final boolean majIdentification, final ILectureEchange lectureEchange);
	public abstract void importer(final boolean majIdentification, final ILectureEchange lectureEchange, boolean incremental);
}