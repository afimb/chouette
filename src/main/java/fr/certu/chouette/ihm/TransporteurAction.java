package fr.certu.chouette.ihm;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.PrincipalAware;
import org.apache.struts2.interceptor.PrincipalProxy;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.Validation;

import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.ITransporteurManager;

public class TransporteurAction extends GeneriqueAction implements Preparable
{
	private static ITransporteurManager	manager;

	private final Log					log	= LogFactory.getLog(TransporteurAction.class);

	private List<Transporteur>			transporteurs;

	private Transporteur				transporteur;

	private Long						idTransporteur;
	
	//	Chaine de caractere implémenté pour complété les retours des actions fait par struts
	String CREATEANDEDIT = "createAndEdit";	
	
	public String cancel()
	{
		addActionMessage(getText("transporteur.cancel.ok"));
		return SUCCESS;
	}

	public String delete()
	{
		manager.supprimer(idTransporteur);
		addActionMessage(getText("transporteur.delete.ok"));
		return SUCCESS;
	}	
	
	public String edit()
	{
		return INPUT;
	}	
	
	public Transporteur getTransporteur()
	{
		return transporteur;
	}

	public List<Transporteur> getTransporteurs()
	{
		return transporteurs;
	}

	@SkipValidation
	@Override
	public String input() throws Exception
	{
		return INPUT;
	}

	@SkipValidation
	public String list()
	{
		transporteurs = manager.lire();

		return SUCCESS;
	}

	public void prepare() throws Exception
	{
		if (idTransporteur != null)
		{
			transporteur = manager.lire(Long.valueOf(idTransporteur));
		}
	}

	public void setIdTransporteur(Long idTransporteur)
	{
		this.idTransporteur = idTransporteur;
	}

	public void setManager(ITransporteurManager manager)
	{
		this.manager = manager;
	}

	public void setTransporteur(Transporteur transporteur)
	{
		log.debug("transporteur : " + transporteur);
		this.transporteur = transporteur;
	}

	public String update() throws Exception, ServiceException
	{
		if (transporteur == null) { return INPUT; }	
		
		if ( transporteur.getId()==null)
		{
			manager.creer(transporteur);
			addActionMessage(getText("transporteur.create.ok"));
		}
		else
		{
			manager.modifier(transporteur);
			addActionMessage(getText("transporteur.update.ok"));
		}		

		return INPUT;
	}
	
	public String createAndEdit() throws Exception, ServiceException
	{
		if (transporteur == null) { return INPUT; }	
		
		if ( transporteur.getId()==null)
		{
			manager.creer(transporteur);
			addActionMessage(getText("transporteur.create.ok"));
		}
		else
			return INPUT;		

		return CREATEANDEDIT;
	}	
}
