package fr.certu.chouette.service.exportateur.monoitineraire.csv;

import java.util.List;

import fr.certu.chouette.service.database.IItineraireManager;

public interface IExportHorairesManager {

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.exportateur.monoitineraire.csv.impl.IItineraireManager#export(long)
	 */
	public abstract List<String[]> exporter(Long idItineraire);

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.exportateur.monoitineraire.csv.impl.IItineraireManager#getItineraireManager()
	 */
	public abstract IItineraireManager getItineraireManager();

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.exportateur.monoitineraire.csv.impl.IItineraireManager#setItineraireManager(fr.certu.chouette.service.database.IItineraireManager)
	 */
	public abstract void setItineraireManager(
			IItineraireManager itineraireManager);

}