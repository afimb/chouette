package fr.certu.chouette.ihm;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.IMissionManager;

public class MissionAction extends GeneriqueAction implements Preparable
{
	private final Log				log	= LogFactory.getLog(MissionAction.class);

	private static IMissionManager	missionManager;

	private List<Mission>			missions;

	private Mission					mission;

	//	Identifiants
	private Long idMission;
	private Long idLigne;
	private Long idItineraire;
	private Long idTableauMarche;
	private Date seuilDateDepartCourse;
	private Long page;
	
	public Mission getMission()
	{
		return mission;
	}

	public void setMission(Mission mission)
	{
		this.mission = mission;
	}

	public List<Mission> getMissions()
	{
		return missions;
	}

	public void setIdMission(Long idMission)
	{
		this.idMission = idMission;
	}

	public void setMissionManager(IMissionManager missionManager)
	{
		this.missionManager = missionManager;
	}
	
	public String cancel()
	{
		addActionMessage(getText("mission.cancel.ok"));
		return SUCCESS;
	}
	
	@SkipValidation
	public String delete()
	{
		//missionManager.supprimer(idMission);
		addActionMessage(getText("mission.delete.ok"));
		return SUCCESS;
	}

	@SkipValidation
	public String edit()
	{
		return INPUT;
	}

	public String update()
	{
		if (mission == null) { return INPUT; }
		
		if ( mission.getId()==null)
		{
			try {
				missionManager.creer(mission);
				addActionMessage(getText("mission.create.ok"));
			} catch( ServiceException e) {
				addActionError(getText( getKey( e, "error.mission.registration", "error.mission.create")));
				return INPUT;
			}
		}
		else
		{
			try {
				missionManager.modifier(mission);
				addActionMessage(getText("mission.update.ok"));
			} catch( ServiceException e) {
				addActionError(getText( getKey( e, "error.mission.registration", "error.mission.update")));
				return INPUT;
			}
		}		
		
		return SUCCESS;
	}
	
	private String getKey( ServiceException e, String special, String general)
	{
		return ( CodeIncident.CONTRAINTE_INVALIDE.equals( e.getCode()))?
			special:general;
	}
	
	@SkipValidation
	public String list()
	{
		missions = missionManager.lire();

		return SUCCESS;
	}

	@Override
	@SkipValidation
	public String input() throws Exception
	{
		return INPUT;
	}

	@SkipValidation
	public void prepare() throws Exception
	{
		if (idMission != null)
		{
			mission = missionManager.lire(idMission);
		}
	}

	public Long getIdItineraire()
	{
		return idItineraire;
	}

	public void setIdItineraire(Long idItineraire)
	{
		this.idItineraire = idItineraire;
	}

	public Long getIdLigne()
	{
		return idLigne;
	}

	public void setIdLigne(Long idLigne)
	{
		this.idLigne = idLigne;
	}
	

	public void setIdTableauMarche(Long idTableauMarche) {
		this.idTableauMarche = idTableauMarche;
	}

	public Long getIdTableauMarche() {
		return idTableauMarche;
	}

	public Date getSeuilDateDepartCourse() {
		return seuilDateDepartCourse;
	}

	public void setSeuilDateDepartCourse(Date seuilDateDepartCourse) {
		this.seuilDateDepartCourse = seuilDateDepartCourse;
	}

	public Long getPage() {
		return page;
	}

	public void setPage(Long page) {
		this.page = page;
	}

	public Long getIdMission() {
		return idMission;
	}

}
