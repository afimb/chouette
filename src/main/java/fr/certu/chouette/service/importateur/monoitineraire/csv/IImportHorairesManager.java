package fr.certu.chouette.service.importateur.monoitineraire.csv;

import java.util.List;

import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.service.database.ICourseManager;
import fr.certu.chouette.service.database.IHoraireManager;

public interface IImportHorairesManager {

	public abstract void importer(List<String[]> donnees);

	public abstract ICourseManager getCourseManager();

	public abstract void setCourseManager(ICourseManager courseManager);

	public abstract IHoraireManager getHoraireManager();

	public abstract void setHoraireManager(IHoraireManager horaireManager);

	public abstract IModificationSpecifique getModificationSpecifique();

	public abstract void setModificationSpecifique(
			IModificationSpecifique modificationSpecifique);
}