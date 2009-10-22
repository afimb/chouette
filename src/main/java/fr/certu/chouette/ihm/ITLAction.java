package fr.certu.chouette.ihm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.PrincipalAware;
import org.apache.struts2.interceptor.PrincipalProxy;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.modele.InterdictionTraficLocal;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.IITLManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;

public class ITLAction extends GeneriqueAction implements RequestAware, Preparable
{
	private Map request;
	private final Log						log	= LogFactory.getLog(ITLAction.class);
	private static IITLManager				itlManager;
	private static ILigneManager			ligneManager;
	private static IPositionGeographiqueManager	positionGeographiqueManager;
	private List<InterdictionTraficLocal>	itls;
	private InterdictionTraficLocal			itl;
	private Long							idItl;
	private List<Ligne>						lignes;
	private List<PositionGeographique>				arrets;
	private String 							ligneName;			
	
	private List<PositionGeographique> arretsDansITLList = new ArrayList();

	private String saisieNomArretExistant;
	private String saisieNomArretExistantKey;
	private String idAreaStop;
	
	private Long						idLigne;
	
	@SkipValidation
	public String cancel()
	{
		addActionMessage(getText("itl.cancel.ok"));
		return SUCCESS;
	}
	
	@SkipValidation
	public String delete()
	{
		itlManager.supprimer(idItl);
		return SUCCESS;
	}
	
	public List<InterdictionTraficLocal> getItls()
	{
		return itls;
	}

	public void setIdItl(Long idItl)
	{
		this.idItl = idItl;
	}

	public void setManager(IITLManager manager)
	{
		this.itlManager = manager;
	}
	
	public void setLigneManager(ILigneManager ligneManager)
	{
		this.ligneManager = ligneManager;
	}
	
	@SkipValidation
	public String list()
	{
		itls = itlManager.lire();
		return SUCCESS;
	}
	
	@SkipValidation
	public String edit()
	{	
		return INPUT;
	}
	
	@SkipValidation
	public String addStop()
	{
		if (saisieNomArretExistantKey != null && !saisieNomArretExistantKey.isEmpty())
		{
			List<Long> l = this.itl.getArretPhysiqueIds();
			l.add(Long.valueOf(saisieNomArretExistantKey));
			itl.setArretPhysiqueIds(l);
			readArretsDansITL();
			try {
				itlManager.modifier(itl);
				addActionMessage(getText("itl.update.ok"));
			} 
			catch (Exception ex) { 
				addActionError(getText("itl.update.ko"));
			}
		}
		return INPUT;
	}	
	
	@SkipValidation
	public String removeStop()
	{
		if (idAreaStop != null && !idAreaStop.isEmpty())
		{
			List<Long> l = this.itl.getArretPhysiqueIds();
			int idx = l.indexOf(Long.valueOf(idAreaStop));
			if (idx != -1) {
				l.remove(idx);
				try {
					itlManager.modifier(itl);
					addActionMessage(getText("itl.update.ok"));
				} 
				catch (Exception ex) { 
					addActionError(getText("itl.update.ko"));
				}
			}
			readArretsDansITL();
		}
		return INPUT;
	}	
	

	
	@SkipValidation
	public void prepare() throws Exception
	{				
		//readLignes();
		
		if (idItl == null) {
			//if (itl != null && itl.getIdLigne() != null) {				
			//	request.put("jsonArrets", getJsonArrets(itl.getIdLigne()));
			//}
			return;
		}
		itl = itlManager.lire(idItl);
		readArretsDansITL();
		//if (lignes != null)
		//	lignes = ligneManager.lire();
		//request.put("jsonArrets", getJsonArrets(itl.getIdLigne()));
		
		
	}

	public String getJsonArrets(Long idLigne)
	{
		String resultat = "";
		
		List<PositionGeographique> arretsPhysiques = null;
		if (idLigne == null)
				arretsPhysiques = positionGeographiqueManager.lireArretsPhysiques();
		else {
			//arretsPhysiques = arretPhysiqueManager.getArretsPhysiques(idLigne);
			arretsPhysiques = positionGeographiqueManager.lireArretsPhysiques();
		}
		
		resultat += "{";
		for (PositionGeographique arretPhysique : arretsPhysiques)
		{
			if (arretsPhysiques.indexOf(arretPhysique) == arretsPhysiques
					.size() - 1)
			{
				resultat += "\"" + arretPhysique.getName() + "\"" + ": "
						+ arretPhysique.getId();
			} else
			{
				resultat += "\"" + arretPhysique.getName() + "\"" + ": "
						+ arretPhysique.getId() + ",";
			}
		}
		resultat += "}";
		return resultat;
	}
	
	public String update()
	{					
		log.debug("update");
		if (itl == null) { return INPUT; }
						
		if ( itl.getId()==null)
		{
			try {
				itlManager.creer(itl);				
				addActionMessage(getText("itl.create.ok"));
			} 
			catch (Exception ex) { 
				addActionError(getText("itl.create.ko"));
			}
		}
		else
		{
			InterdictionTraficLocal itlOld = itlManager.lire(itl.getId());
			if (!itlOld.getIdLigne().equals(itl.getIdLigne())) {
				//List<ArretPhysique> l = arretPhysiqueManager.getArretsPhysiques(itl.getIdLigne());
				List<PositionGeographique> l = positionGeographiqueManager.lireArretsPhysiques();
				session.put("arrets", l);
			}
			try {
				itlManager.modifier(itl);
				addActionMessage(getText("itl.update.ok"));
			} 
			catch (Exception ex) { 
				addActionError(getText("itl.update.ko"));
			}
		}			
		return INPUT;
	}

	public InterdictionTraficLocal getItl() {
		return itl;
	}

	public void setItl(InterdictionTraficLocal itl) {
		this.itl = itl;
	}

	public List<Ligne> getLignes() {
		readLignes();
		return lignes;
	}
	
	public void setRequest(Map request)
	{
		this.request = request;
	}

	public void setPositionGeographiqueManager(
			IPositionGeographiqueManager positionGeographiqueManager) {
		ITLAction.positionGeographiqueManager = positionGeographiqueManager;
	}

	public String getSaisieNomArretExistant() {
		return saisieNomArretExistant;
	}

	public void setSaisieNomArretExistant(String saisieNomArretExistant) {
		this.saisieNomArretExistant = saisieNomArretExistant;
	}

	public String getSaisieNomArretExistantKey() {
		return saisieNomArretExistantKey;
	}

	public void setSaisieNomArretExistantKey(String saisieNomArretExistantKey) {
		this.saisieNomArretExistantKey = saisieNomArretExistantKey;
	}

	public String getIdAreaStop() {
		return idAreaStop;
	}

	public void setIdAreaStop(String idAreaStop) {
		this.idAreaStop = idAreaStop;
	}

	public Long getIdLigne() {
		return idLigne;
	}

	public void setIdLigne(Long idLigne) {
		this.idLigne = idLigne;
	}

	public String getChaineIdLigne()
	{
		return itl.getIdLigne().toString();
	}

	public Map getRequest() {
		return request;
	}
	
	private void readLignes() {
		lignes = ligneManager.lire();
		/*
		List<Ligne> l = (List<Ligne>)this.getSession().get("lignes");
		if (l == null) {
			lignes = ligneManager.lire();
			this.getSession().put("lignes", lignes);
		}
		else 
			lignes = l;	
		*/	
	}

	public List<PositionGeographique> getArrets() {
		List<PositionGeographique> l = (List<PositionGeographique>)session.get("arrets");
		//if (l == null) {
			arrets = ligneManager.getArretsPhysiques(itl.getIdLigne());
			
			if (arretsDansITLList != null) {
				for (int i=0; i<arretsDansITLList.size(); i++) {
					if (arrets != null) {
						for (int j=0; j<arrets.size(); j++) {
							Long l1 = arretsDansITLList.get(i).getId();
							Long l2 = arrets.get(j).getId();
							if (l1.equals(l2)) {
								arrets.remove(j);
							}
						}
					}
					arrets.remove(arretsDansITLList.get(i));
				}
			}
			
			session.put("arrets", arrets);
		
		return arrets;
	}

	public void setArrets(List<PositionGeographique> arrets) {
		this.arrets = arrets;
	}

	private void readArretsDansITL() {
		arretsDansITLList = new ArrayList();
		if (itl != null) {
			List list_id = itl.getArretPhysiqueIds();
			if (list_id != null) {
				for (int i=0; i<list_id.size();i++) {
					Long id = (Long)list_id.get(i);
					PositionGeographique ap = positionGeographiqueManager.lire(id);
					arretsDansITLList.add(ap);
				}
			}	
		}
	}
	
	public List<PositionGeographique> getArretsDansITLList() {
		
		return arretsDansITLList;
	}

	public void setArretsDansITLList(List arretsDansITLList) {
		itl.setArretPhysiqueIds(arretsDansITLList);
	}

	public String getLigneName() {
		if (itl != null && itl.getIdLigne() != null) {
			Ligne l = ligneManager.lire(itl.getIdLigne());
			return l.getFullName();
		}
		else return null;
		
	}

}

