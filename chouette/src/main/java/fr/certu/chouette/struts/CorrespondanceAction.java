package fr.certu.chouette.struts;

import com.opensymphony.xwork2.Preparable;
import fr.certu.chouette.critere.AndClause;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.critere.VectorClause;
import fr.certu.chouette.struts.enumeration.ObjetEnumere;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.ICorrespondanceManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

@SuppressWarnings({"serial", "unchecked"})
public class CorrespondanceAction extends GeneriqueAction implements Preparable, RequestAware {
	
	private static final Log				          log	                      = LogFactory.getLog(CorrespondanceAction.class);
	private static       ICorrespondanceManager	      correspondanceManager;
	private static       IPositionGeographiqueManager positionGeographiqueManager;
	private              String                       useHastus;
	private              Map                          request;
	private              Correspondance               connectionlink;
	private              List<Correspondance>         correspondances;
	private              Long                         idCorrespondance;
	private              String                       zoneDepartText;
	private              String                       zoneArriveeText;
	private              String                       saisieZoneExistante;
	private              String                       saisieZoneExistanteKey;
	private              List<PositionGeographique>   zones;
	private              PositionGeographique         criteria;
	private              PositionGeographique         start;
	private              PositionGeographique         end;
	private              String                       actionSuivante;
	private              Long                         idPositionGeographique;
	private              List<PositionGeographique>   positionGeographiquesResultat;
	private              String                       durationsFormat             = "mm:ss";
	private              String                       LOADEDIT                    = "loadedit";
	
	public void setOccasionalTravellerDuration(String s) {
		SimpleDateFormat	sdfHoraire	= new SimpleDateFormat(durationsFormat);
		if (s != null && s.length() > 0) {
			try {
				Date d = sdfHoraire.parse(s);
				connectionlink.setOccasionalTravellerDuration(d);
			}
			catch (Exception ex) {				
				addActionError(ex.getLocalizedMessage());
			}
		}
		else
			connectionlink.setOccasionalTravellerDuration(null);
	}
	
	public String getOccasionalTravellerDuration() {
		if (connectionlink != null && connectionlink.getOccasionalTravellerDuration() != null) {
			Date d = connectionlink.getOccasionalTravellerDuration();
			SimpleDateFormat	sdfHoraire	= new SimpleDateFormat(durationsFormat);
			return sdfHoraire.format(d);
		}
		else
			return null;
	}
	
	public void setMobilityRestrictedTravellerDuration(String s) {
		SimpleDateFormat	sdfHoraire	= new SimpleDateFormat(durationsFormat);
		if (s != null && s.length() > 0) {
			try {
				Date d = sdfHoraire.parse(s);
				connectionlink.setMobilityRestrictedTravellerDuration(d);
			}
			catch (Exception ex) {				
				addActionError(ex.getLocalizedMessage());
			}
		}
		else
			connectionlink.setMobilityRestrictedTravellerDuration(null);
	}
	
	public String getMobilityRestrictedTravellerDuration() {
		if (connectionlink != null && connectionlink.getMobilityRestrictedTravellerDuration() != null) {
			Date d = connectionlink.getMobilityRestrictedTravellerDuration();
			SimpleDateFormat	sdfHoraire	= new SimpleDateFormat(durationsFormat);
			return sdfHoraire.format(d);
		}
		else
			return null;
	}
	
	public void setFrequentTravellerDuration(String s) {
		SimpleDateFormat	sdfHoraire	= new SimpleDateFormat(durationsFormat);
		if (s != null && s.length() > 0) {
			try {
				Date d = sdfHoraire.parse(s);
				connectionlink.setFrequentTravellerDuration(d);
			}
			catch (Exception ex) {				
				addActionError(ex.getLocalizedMessage());
			}
		}
		else
			connectionlink.setFrequentTravellerDuration(null);
	}
	
	public String getFrequentTravellerDuration() {
		if (connectionlink != null && connectionlink.getFrequentTravellerDuration() != null) {
			Date d = connectionlink.getFrequentTravellerDuration();
			SimpleDateFormat	sdfHoraire	= new SimpleDateFormat(durationsFormat);
			return sdfHoraire.format(d);
		}
		else
			return null;
	}
	
	public void setDefaultDuration(String s) {
		SimpleDateFormat	sdfHoraire	= new SimpleDateFormat(durationsFormat);
		if (s != null && s.length() > 0) {
			try {
				Date d = sdfHoraire.parse(s);
				connectionlink.setDefaultDuration(d);
			}
			catch (Exception ex) {				
				addActionError(ex.getLocalizedMessage());
			}
		}
		else
			connectionlink.setDefaultDuration(null);
	}
	
	public String getDefaultDuration() {
		if (connectionlink != null && connectionlink.getDefaultDuration() != null) {
			Date d = connectionlink.getDefaultDuration();
			SimpleDateFormat	sdfHoraire	= new SimpleDateFormat(durationsFormat);
			return sdfHoraire.format(d);
		}
		else
			return null;
	}
	
	public String execute() throws Exception{
		return SUCCESS;
	}
	
	public void setRequest(Map request) {
		this.request = request;
	}
	
	public String cancel() {
		addActionMessage(getText("connectionlink.cancel.ok"));
		return SUCCESS;
	}
	
	@SkipValidation
	public String delete() {
		correspondanceManager.supprimer(idCorrespondance);
		return SUCCESS;
	}
	
	@SkipValidation
	public String edit() {
		return INPUT; 
	}
	
	@Override
	@SkipValidation
	public String input() throws Exception {
		return INPUT;
	}
	
	@SkipValidation
	public String list() {
		correspondances = correspondanceManager.lire();
		return SUCCESS;
	}
	
	@SkipValidation
	public String addStart() {
		if (idPositionGeographique != null && idCorrespondance != null) {
			connectionlink = correspondanceManager.lire(idCorrespondance);
			connectionlink.setIdDepart(idPositionGeographique);
			correspondanceManager.modifier(connectionlink);
		}
		return LOADEDIT;
	}
	
	@SkipValidation
	public String addEnd() {
		if (idPositionGeographique != null && idCorrespondance != null) {
			connectionlink = correspondanceManager.lire(idCorrespondance);
			connectionlink.setIdArrivee(idPositionGeographique);
			correspondanceManager.modifier(connectionlink);
		}
		return LOADEDIT;
	}
	
	public String update() {
		request.put("jsonAreaStops", jsonAreaStops());
		log.debug("update");
		if (connectionlink == null)
			return INPUT;
		if (connectionlink.getId() == null) {
			try {
				correspondanceManager.creer(connectionlink);
				addActionMessage(getText("connectionlink.create.ok"));
			}
			catch (Exception ex) {
				addActionError(getText("connectionlink.create.ko"));
			}
		}
		else {
			try {
				correspondanceManager.modifier(connectionlink);
				addActionMessage(getText("connectionlink.update.ok"));
			}
			catch (Exception ex) {
				addActionError(getText("connectionlink.update.ko"));
			}			
		}
		return INPUT;
	}
	
	public void prepareEdit() throws Exception {
		request.put("jsonAreaStops", jsonAreaStops());
		if (idCorrespondance == null)
			return;
		// Correspondance creation				
		connectionlink = correspondanceManager.lire(idCorrespondance);
		if (connectionlink.getIdDepart() != null)
			this.start = positionGeographiqueManager.lire(connectionlink.getIdDepart());
		if (connectionlink.getIdArrivee() != null)
			this.end = positionGeographiqueManager.lire(connectionlink.getIdArrivee());
	}
	
	public void prepareUpdate() throws Exception {
		if (idCorrespondance == null)
			return;
		// Correspondance creation				
		connectionlink = correspondanceManager.lire(idCorrespondance);
		Long idStart = connectionlink.getIdDepart();
		if (idStart != null)
			start = positionGeographiqueManager.lire(idStart);
		Long idEnd = connectionlink.getIdArrivee();
		if (idEnd != null)
			end = positionGeographiqueManager.lire(idEnd);
	}
	
	@SkipValidation
	public void prepare() throws Exception {		
		//request.put("jsonAreaStops", jsonAreaStops());
	}
	
	public void setCorrespondanceManager(ICorrespondanceManager manager) {
		CorrespondanceAction.correspondanceManager = manager;
	}
	
	public Correspondance getConnectionlink() {
		return connectionlink;
	}
	
	public void setConnectionlink(Correspondance correspondance) {
		this.connectionlink = correspondance;
	}
	
	public List<Correspondance> getCorrespondances() {
		return correspondances;
	}
	
	public void setCorrespondances(List<Correspondance> correspondances) {
		this.correspondances = correspondances;
	}
	
	public Long getIdCorrespondance() {
		return idCorrespondance;
	}
	
	public void setIdCorrespondance(Long idCorrespondance) {
		this.idCorrespondance = idCorrespondance;
	}
	
	private String jsonAreaStops() {
		String resultat = "";
		List<PositionGeographique> zones = null;
		//zones = zoneManager.lire();
		zones = getZones();
		resultat += "{";
		for (PositionGeographique zone : zones)
			if (zones.indexOf(zone) == zones.size() - 1)
				resultat += "\"" + zone.getName() + "\"" + ": " + zone.getId();
			else
				resultat += "\"" + zone.getName() + "\"" + ": " + zone.getId() + ",";
		resultat += "}";
		return resultat;
	}
	
	public String getSaisieZoneExistante() {
		return saisieZoneExistante;
	}
	
	public void setSaisieZoneExistante(String saisieZoneExistante) {
		this.saisieZoneExistante = saisieZoneExistante;
	}
	
	public String getSaisieZoneExistanteKey() {
		return saisieZoneExistanteKey;
	}
	
	public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager) {
		CorrespondanceAction.positionGeographiqueManager = positionGeographiqueManager;
	}
	
	public void setSaisieZoneExistanteKey(String saisieZoneExistanteKey) {
		this.saisieZoneExistanteKey = saisieZoneExistanteKey;
	}
	
	public String getZoneDepartText() {
		Long idStart = connectionlink.getIdDepart();
		zoneDepartText = null;
		if (idStart != null) {
			PositionGeographique z = positionGeographiqueManager.lire(idStart);
			zoneDepartText = z.getName();
		}
		return zoneDepartText;
	}
	
	public void setZoneDepartText(String zoneDepartText) {
		this.zoneDepartText = zoneDepartText;
	}
	
	public String getZoneArriveeText() {
		Long idEnd = connectionlink.getIdArrivee();
		zoneArriveeText = null;
		if (idEnd != null) {
			PositionGeographique z = positionGeographiqueManager.lire(idEnd);
			zoneArriveeText = z.getName();
		}	
		return zoneArriveeText;
	}
	
	public void setZoneArriveeText(String zoneArriveeText) {
		this.zoneArriveeText = zoneArriveeText;
	}
	
	public List<PositionGeographique> getZones() {
		List<PositionGeographique> l = (List<PositionGeographique>)session.get("zones");
		if (l == null) {
			zones = positionGeographiqueManager.lireZones();
			List<PositionGeographique> arrets = positionGeographiqueManager.lireArretsPhysiques();
			zones.addAll(arrets);
			session.put("zones", zones);
		}
		else 
			zones = l;
		return zones;
	}
	
	@SkipValidation
	public String searchResults() {
		Collection<String> areas = new HashSet<String>();
		if (criteria.getAreaType() != null)
			areas.add(criteria.getAreaType().toString());
		else {
			List <ObjetEnumere> areaEnumerations = fr.certu.chouette.struts.enumeration.EnumerationApplication.getArretPhysiqueAreaTypeEnum();
			areaEnumerations.addAll(fr.certu.chouette.struts.enumeration.EnumerationApplication.getZoneAreaTypeEnum());
			for (ObjetEnumere enumeration : areaEnumerations)
				areas.add(enumeration.getEnumeratedTypeAccess().toString());
		}
		if ("".equals(criteria.getName()))
			criteria.setName(null);
		if ("".equals(criteria.getCountryCode()))
			criteria.setCountryCode(null);
		positionGeographiquesResultat = positionGeographiqueManager.select(new AndClause().
				add(ScalarClause.newIlikeClause("name", criteria.getName())).
				add(ScalarClause.newIlikeClause("countryCode", criteria.getCountryCode())).
				add(VectorClause.newInClause("areaType", areas))
		);
		return "results";
	}
	
	public PositionGeographique getCriteria() {
		return criteria;
	}
	
	public void setCriteria(PositionGeographique criteria) {
		this.criteria = criteria;
	}
	
	public List<PositionGeographique> getPositionGeographiquesResultat() {
		return positionGeographiquesResultat;
	}
	
	public void setPositionGeographiquesResultat(List<PositionGeographique> positionGeographiquesResultat) {
		this.positionGeographiquesResultat = positionGeographiquesResultat;
	}
	
	@SkipValidation
	public String search() {
		return SUCCESS;
	}
	
	public Long getIdPositionGeographique() {
		return idPositionGeographique;
	}
	
	public void setIdPositionGeographique(Long idPositionGeographique) {
		this.idPositionGeographique = idPositionGeographique;
	}
	
	public PositionGeographique getStart() {
		return start;
	}
	
	public void setStart(PositionGeographique start) {
		this.start = start;
	}
	
	public PositionGeographique getEnd() {
		return end;
	}
	
	public void setEnd(PositionGeographique end) {
		this.end = end;
	}
	
	public String getActionSuivante() {
		return actionSuivante;
	}
	
	public void setActionSuivante(String actionSuivante) {
		this.actionSuivante = actionSuivante;
	}
	
	public void setUseHastus(String useHastus) {
		this.useHastus = useHastus;
	}
	
	public String getUseHastus() {
		return useHastus;
	}
}
