package fr.certu.chouette.service.database.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.certu.chouette.critere.Ordre;
import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.database.ITransporteurManager;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class TransporteurManager implements ITransporteurManager 
{
	private ITemplateDao<Transporteur> transporteurDao;
	private ISelectionSpecifique selectionSpecifique;
	private IModificationSpecifique modificationSpecifique;
	private IIdentificationManager identificationManager;

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.ITransporteurManager#lireParObjectId(java.lang.Long)
	 */
	public Transporteur lireParObjectId( String objectId)
	{
		return transporteurDao.getByObjectId(objectId);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.TransporteurManager#getLignesTransporteur(java.lang.Long)
	 */
	public List<Ligne> getLignesTransporteur(Long idTransporteur) 
	{
		return selectionSpecifique.getLignesTransporteur(idTransporteur);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.TransporteurManager#modifier(fr.certu.chouette.modele.Transporteur)
	 */
	public void modifier( Transporteur transporteur)
	{
		transporteurDao.update( transporteur);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.TransporteurManager#creer(fr.certu.chouette.modele.Transporteur)
	 */
	public void creer( Transporteur transporteur)
	{
		transporteurDao.save( transporteur);
		String objectId = identificationManager.getIdFonctionnel("Company", transporteur);
		transporteur.setObjectId(objectId);
		transporteur.setCreationTime( new Date());
		transporteur.setObjectVersion(1);
		transporteurDao.update( transporteur);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.TransporteurManager#lire(java.lang.Long)
	 */
	public Transporteur lire( Long idTransporteur)
	{
		return transporteurDao.get( idTransporteur);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.TransporteurManager#lire()
	 */
	public List<Transporteur> lire()
	{
		List<Ordre> ordres = new ArrayList<Ordre>();
		ordres.add( new Ordre( "name", true));
		return transporteurDao.select(null, ordres);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.TransporteurManager#supprimer(java.lang.Long)
	 */
	public void supprimer( Long idTransporteur)
	{
		modificationSpecifique.supprimerTransporteur( idTransporteur);
	}

	public ITemplateDao<Transporteur> getTransporteurDao() 
	{
		return transporteurDao;
	}

	public void setTransporteurDao(ITemplateDao<Transporteur> transporteurDao) 
	{
		this.transporteurDao = transporteurDao;
	}

	public ISelectionSpecifique getSelectionSpecifique() 
	{
		return selectionSpecifique;
	}

	public void setSelectionSpecifique(ISelectionSpecifique selectionSpecifique) 
	{
		this.selectionSpecifique = selectionSpecifique;
	}

	public IModificationSpecifique getModificationSpecifique() 
	{
		return modificationSpecifique;
	}

	public void setModificationSpecifique(
			IModificationSpecifique modificationSpecifique) 
	{
		this.modificationSpecifique = modificationSpecifique;
	}

	public void setIdentificationManager(
			IIdentificationManager identificationManager) 
	{
		this.identificationManager = identificationManager;
	}
}
