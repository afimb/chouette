package fr.certu.chouette.ihm;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;
import fr.certu.chouette.service.database.ChouetteDriverManagerDataSource;

import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.critere.AndClause;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.database.ITransporteurManager;

public class LigneAction extends GeneriqueAction implements Preparable
{
	//	Manager pour l'action
	private static ILigneManager ligneManager;
	private static ITransporteurManager transporteurManager;
	private static IReseauManager reseauManager;

	private final static String CHOUETTE = "CHOUETTE";
	private final static String AMIVIF = "AMIVIF";

	private final Log log = LogFactory.getLog(LigneAction.class);

	private List<Ligne> listeLigne;
	
	private List<Transporteur> listeTransporteur;
	private List<Reseau> listeReseau;
	
	private Map<Long, Reseau> reseauParId;
	private Map<Long, Transporteur> transporteurParId; 
	private String typeLigne;
	
	private Ligne ligne;

	private Long idLigne;
	
	private String useAmivif;
	
	private Long idReseauzz;
	private Long idTransporteurzz;
	private String nomLigne;
	
	private              ChouetteDriverManagerDataSource managerDataSource;
	private              Connection              connexion                = null;
	private              boolean                 detruireAvecTMs          = true;
	private              boolean                 detruireAvecArrets       = true;
	private              boolean                 detruireAvecTransporteur = true;
	private              boolean                 detruireAvecReseau       = true;
	
	//	Chaine de caractere implémenté pour complété les retours des actions fait par struts
	String CREATEANDEDIT = "createAndEdit";
	
	public String detruireLigne() {
		if (idLigne != null) {
			try {
				// ORDRE DE DESTRUCTION DES LIGNES :
				// vehiclejourneyatstop, timetablevehiclejourney, timetable_period, timetable_date, timetable, vehiclejourney,
				// journeypattern, stoppoint, route*, connectionlink, itl_stoparea, itl, stoparea*, line, company, ptnetwork
				
				// vehiclejourneyatstop : vehiclejourney, stoppoint
				// vehiclejourney : route, journeypattern
				// stoppoint : route, stoparea
				// route : line, route
				// journeypattern :
				// stoparea : stoparea
				// line : company, ptnetwork
				// company :
				// ptnetwork : 
				// ptnetwork :
				// timetablevehiclejourney : vehiclejourney, timetable
				// timetable :
				// timetable_date : timetable
				// timetable_periode : timetable
				// itl : line
				// itl_stoparea : itl, stoparea
				// connectionlink : stoparea
				
				Class.forName(managerDataSource.getDriverClassName());
				Properties props = new Properties();
				props.setProperty("user", managerDataSource.getUsername());
				props.setProperty("password", managerDataSource.getPassword());
				props.setProperty("allowEncodingChanges", "true");
				connexion = DriverManager.getConnection(managerDataSource.getUrl(), props);
				connexion.setAutoCommit(false);
				
				String idItinerairesStr = "(";
				String selectItineraires = "SELECT DISTINCT id FROM " + managerDataSource.getDatabaseSchema() + ".route WHERE lineId='"+idLigne.longValue()+"';";
				Statement statementItineraire = connexion.createStatement();
				ResultSet rsItineraire = statementItineraire.executeQuery(selectItineraires);
				boolean drapeau = false;
				while (rsItineraire.next()) {
					String tmp = rsItineraire.getObject(1).toString();
					if (drapeau)
						idItinerairesStr += " , ";
					drapeau = true;
					idItinerairesStr += "'"+tmp+"'";
				}
				idItinerairesStr += ")";
				
				String idCoursesStr = "(";
				if (!idItinerairesStr.equals("()")) {
					String selectCourses = "SELECT DISTINCT id FROM " + managerDataSource.getDatabaseSchema() + ".vehiclejourney WHERE routeId IN "+idItinerairesStr+";";
					Statement statementCourse = connexion.createStatement();
					ResultSet rsCourse = statementCourse.executeQuery(selectCourses);
					drapeau = false;
					while (rsCourse.next()) {
						String tmp = rsCourse.getObject(1).toString();
						if (drapeau)
							idCoursesStr += " , ";
						drapeau = true;
						idCoursesStr += "'"+tmp+"'";
					}
				}
				idCoursesStr += ")";
				
				String idPhysiquesStr = "(";
				if (!idItinerairesStr.equals("()")) {
					String selectPhysiques = "SELECT DISTINCT stopAreaId FROM " + managerDataSource.getDatabaseSchema() + ".stoppoint WHERE routeId IN "+idItinerairesStr+";";
					Statement statementPhysique = connexion.createStatement();
					ResultSet rsPhysique = statementPhysique.executeQuery(selectPhysiques);
					drapeau = false;
					while (rsPhysique.next()) {
						String tmp = rsPhysique.getObject(1).toString();
						if (drapeau)
							idPhysiquesStr += " , ";
						drapeau = true;
						idPhysiquesStr += "'"+tmp+"'";
					}
				}
				idPhysiquesStr += ")";
				
				String idMissionsStr = "(";
				if (!idCoursesStr.equals("()")) {
					String selectMissions = "SELECT DISTINCT journeyPatternId FROM " + managerDataSource.getDatabaseSchema() + ".vehiclejourney WHERE id IN "+idCoursesStr+";";
					Statement statementMission = connexion.createStatement();
					ResultSet rsMission = statementMission.executeQuery(selectMissions);
					drapeau = false;
					while (rsMission.next()) {
						String tmp = rsMission.getObject(1).toString();
						if (drapeau)
							idMissionsStr += " , ";
						drapeau = true;
						idMissionsStr += "'"+tmp+"'";
					}
				}
				idMissionsStr += ")";			
				String idITLsStr = "(";
				String setectITLs = "SELECT DISTINCT id FROM " + managerDataSource.getDatabaseSchema() + ".routingConstraint WHERE lineId='"+idLigne.longValue()+"';";
				Statement statementITL = connexion.createStatement();
				ResultSet rsITL = statementITL.executeQuery(setectITLs);
				drapeau = false;
				while (rsITL.next()) {
					String tmp = rsITL.getObject(1).toString();
					if (drapeau)
						idITLsStr += " , ";
					drapeau = true;
					idITLsStr += "'"+tmp+"'";
				}
				idITLsStr += ")";
				
				String idTMsStr = "(";
				if (detruireAvecTMs && !idCoursesStr.equals("()")) {
					String selectTMs = "SELECT DISTINCT timetableId FROM " + managerDataSource.getDatabaseSchema() + ".timetablevehiclejourney WHERE vehicleJourneyId IN "+idCoursesStr+";";
					Statement statementTM = connexion.createStatement();
					ResultSet rsTM = statementTM.executeQuery(selectTMs);
					drapeau = false;
					while (rsTM.next()) {
						String tmp = rsTM.getObject(1).toString();
						if (drapeau)
							idTMsStr += " , ";
						drapeau = true;
						idTMsStr += "'"+tmp+"'";
					}
				}
				idTMsStr += ")";
				
				if (!idCoursesStr.equals("()")) {
					String netoyageHoraires = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".vehiclejourneyatstop WHERE vehicleJourneyId IN "+idCoursesStr+";";
					Statement statementNetoyageHoraires = connexion.createStatement();
					statementNetoyageHoraires.executeUpdate(netoyageHoraires);
				
					String netoyageTMCourses = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".timetablevehiclejourney WHERE vehicleJourneyId IN "+idCoursesStr+";";
					Statement statementNetoyageTMCourses = connexion.createStatement();
					statementNetoyageTMCourses.executeUpdate(netoyageTMCourses);
				}
				
				if (detruireAvecTMs && (!idTMsStr.equals("()"))) {
					
					String selectTMs = "SELECT DISTINCT timetableId FROM " + managerDataSource.getDatabaseSchema() + ".timetablevehiclejourney WHERE timetableId IN "+idTMsStr+";";
					Statement statementTM = connexion.createStatement();
					ResultSet rsTM = statementTM.executeQuery(selectTMs);
					while (rsTM.next()) {
						String tmp = rsTM.getObject(1).toString();
						if (idTMsStr.startsWith("('"+tmp+"' , "))
							idTMsStr = "(" + idTMsStr.substring(tmp.length()+6);
						else if (idTMsStr.indexOf(" , '"+tmp+"' , ") > 0) {
							int index = idTMsStr.indexOf(" , '"+tmp+"' , ");
							idTMsStr = idTMsStr.substring(0, index) + idTMsStr.substring(index+5+tmp.length());
						}
						else if (idTMsStr.indexOf(" , '"+tmp+"')") > 0) {
							int index = idTMsStr.indexOf(" , '"+tmp+"')");
							idTMsStr = idTMsStr.substring(0, index) + ")";
						}
						else if (idTMsStr.equals("('"+tmp+"')"))
							idTMsStr = "()";
					}
					if (!idTMsStr.equals("()")) {
						String netoyagePeriodes = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".timetable_period WHERE timetableid IN "+idTMsStr+";";
						Statement statementNetoyagePeriodes = connexion.createStatement();
						statementNetoyagePeriodes.executeUpdate(netoyagePeriodes);
						String netoyageDates = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".timetable_Date WHERE timetableid IN "+idTMsStr+";";
						Statement statementNetoyageDates = connexion.createStatement();
						statementNetoyageDates.executeUpdate(netoyageDates);
						String netoyageTMs = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".timetable WHERE id IN "+idTMsStr+";";
						Statement statementNetoyageTMs = connexion.createStatement();
						statementNetoyageTMs.executeUpdate(netoyageTMs);
					}
				}
				
				if (!idCoursesStr.equals("()")) {
					String netoyageCourses = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".vehiclejourney WHERE id IN "+idCoursesStr+";";
					Statement statementNetoyageCourses = connexion.createStatement();
					statementNetoyageCourses.executeUpdate(netoyageCourses);
				}
				
				if (!idMissionsStr.equals("()")) {
					String netoyageMissions = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".journeypattern WHERE id IN "+idMissionsStr+";";
					Statement statementNetoyageMissions = connexion.createStatement();
					statementNetoyageMissions.executeUpdate(netoyageMissions);
				}
				
				if (!idItinerairesStr.equals("()")) {
					String netoyageArretsItineraires = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".stoppoint WHERE routeId IN "+idItinerairesStr+";";
					Statement statementNetoyageArretsItineraires = connexion.createStatement();
					statementNetoyageArretsItineraires.executeUpdate(netoyageArretsItineraires);
					
					String netoyageItineraires1 = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".route WHERE (id IN "+idItinerairesStr+") AND (oppositeRouteId IS NOT NULL);";
					Statement statementNetoyageItineraires1 = connexion.createStatement();
					statementNetoyageItineraires1.executeUpdate(netoyageItineraires1);
					connexion.commit();
					String netoyageItineraires2 = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".route WHERE id IN "+idItinerairesStr+";";
					Statement statementNetoyageItineraires2 = connexion.createStatement();
					statementNetoyageItineraires2.executeUpdate(netoyageItineraires2);
				}
				
				if (!idITLsStr.equals("()")) {
					String netoyageArretsITLs = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".routingConstraint_stoparea WHERE routingConstraintId IN "+idITLsStr+";";
					Statement statementNetoyageArretsITLs = connexion.createStatement();
					statementNetoyageArretsITLs.executeUpdate(netoyageArretsITLs);
					
					String netoyageITLs = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".routingConstraint WHERE id IN "+idITLsStr+";";
					Statement statementNetoyageITLs = connexion.createStatement();
					statementNetoyageITLs.executeUpdate(netoyageITLs);
				}
				
				if (detruireAvecArrets && !idPhysiquesStr.equals("()")) { // arrets, correspondances
					String selectPhysiques2 = "SELECT DISTINCT stopAreaId FROM " + managerDataSource.getDatabaseSchema() + ".stoppoint WHERE stopAreaId IN "+idPhysiquesStr+";";
					Statement statementPhysique2 = connexion.createStatement();
					ResultSet rsPhysique2 = statementPhysique2.executeQuery(selectPhysiques2);
					while (rsPhysique2.next()) {
						String tmp = rsPhysique2.getObject(1).toString();
						if (idPhysiquesStr.startsWith("('"+tmp+"' , "))
							idPhysiquesStr = "(" + idPhysiquesStr.substring(tmp.length()+6);
						else if (idPhysiquesStr.indexOf(" , '"+tmp+"' , ") > 0) {
							int index = idPhysiquesStr.indexOf(" , '"+tmp+"' , ");
							idPhysiquesStr = idPhysiquesStr.substring(0, index) + idPhysiquesStr.substring(index+5+tmp.length());
						}
						else if (idPhysiquesStr.indexOf(" , '"+tmp+"')") > 0) {
							int index = idPhysiquesStr.indexOf(" , '"+tmp+"')");
							idPhysiquesStr = idPhysiquesStr.substring(0, index) + ")";
						}
						else if (idPhysiquesStr.equals("('"+tmp+"')"))
							idPhysiquesStr = "()";
					}
					while (!idPhysiquesStr.equals("()")) { // RECURSIVITE
						
						String selectPhysiques3 = "SELECT DISTINCT parentId FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE parentId IN "+idPhysiquesStr+";";
						Statement statementPhysique3 = connexion.createStatement();
						ResultSet rsPhysique3 = statementPhysique3.executeQuery(selectPhysiques3);
						while (rsPhysique3.next()) {
							String tmp = rsPhysique3.getObject(1).toString();
							if (idPhysiquesStr.startsWith("('"+tmp+"' , "))
								idPhysiquesStr = "(" + idPhysiquesStr.substring(tmp.length()+6);
							else if (idPhysiquesStr.indexOf(" , '"+tmp+"' , ") > 0) {
								int index = idPhysiquesStr.indexOf(" , '"+tmp+"' , ");
								idPhysiquesStr = idPhysiquesStr.substring(0, index) + idPhysiquesStr.substring(index+5+tmp.length());
							}
							else if (idPhysiquesStr.indexOf(" , '"+tmp+"')") > 0) {
								int index = idPhysiquesStr.indexOf(" , '"+tmp+"')");
								idPhysiquesStr = idPhysiquesStr.substring(0, index) + ")";
							}
							else if (idPhysiquesStr.equals("('"+tmp+"')"))
								idPhysiquesStr = "()";
						}
						
						if (!idPhysiquesStr.equals("()")) {
							String idPhysiquesStrTmp = "(";
							String selectPhysiquesTmp = "SELECT DISTINCT parentId FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE id IN "+idPhysiquesStr+";";
							Statement statementPhysiqueTmp = connexion.createStatement();
							ResultSet rsPhysiqueTmp = statementPhysiqueTmp.executeQuery(selectPhysiquesTmp);
							drapeau = false;
							while (rsPhysiqueTmp.next()) {
								if (rsPhysiqueTmp.getObject(1) == null)
									continue;
								String tmp = rsPhysiqueTmp.getObject(1).toString();
								if (drapeau)
									idPhysiquesStrTmp += " , ";
								drapeau = true;
								idPhysiquesStrTmp += "'"+tmp+"'";
							}
							idPhysiquesStrTmp += ")";
						
							String netoyageCorrespondances = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".connectionlink WHERE (departureId IN "+idPhysiquesStr+") OR (arrivalId IN "+idPhysiquesStr+");";
							Statement statementNetoyageCorrespondances = connexion.createStatement();
							statementNetoyageCorrespondances.executeUpdate(netoyageCorrespondances);
							
							String netoyageArrets = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE id IN "+idPhysiquesStr+";";
							Statement statementNetoyageArrets = connexion.createStatement();
							statementNetoyageArrets.executeUpdate(netoyageArrets);
							
							connexion.commit();
							
							idPhysiquesStr = idPhysiquesStrTmp;
						}
					}
				}
				
				String selectTransporteur = "SELECT DISTINCT companyId FROM " + managerDataSource.getDatabaseSchema() + ".line WHERE id='"+idLigne.longValue()+"';";
				Statement satatementTransporteur = connexion.createStatement();
				ResultSet rsTransporteur = satatementTransporteur.executeQuery(selectTransporteur);
				String idTransporteur = null;
				if (rsTransporteur.next())
					idTransporteur = rsTransporteur.getObject(1).toString();
				
				String selectReseau = "SELECT DISTINCT PTNetworkId FROM " + managerDataSource.getDatabaseSchema() + ".line WHERE id='"+idLigne.longValue()+"';";
				Statement satatementReseau = connexion.createStatement();
				ResultSet rsReseau = satatementReseau.executeQuery(selectReseau);
				String idReseau = null;
				if (rsReseau.next())
					idReseau = rsReseau.getObject(1).toString();
				
				String netoyageLigne = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".line WHERE id='"+idLigne.longValue()+"';";
				Statement statementNetoyageLigne = connexion.createStatement();
				statementNetoyageLigne.executeUpdate(netoyageLigne);
				
				if (detruireAvecTransporteur) {
					if (idTransporteur != null) {
						String selectAutres = "SELECT * FROM " + managerDataSource.getDatabaseSchema() + ".line WHERE companyId='"+idTransporteur+"';";
						Statement statementAutres = connexion.createStatement();
						ResultSet rsAutres = statementAutres.executeQuery(selectAutres);
						if (rsAutres.next())
							idTransporteur = null;
					}
					if (idTransporteur != null) {
						String netoyageTransporteur = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".company WHERE id='"+idTransporteur+"';";
						Statement statementNetoyageTransporteur = connexion.createStatement();
						statementNetoyageTransporteur.executeUpdate(netoyageTransporteur);
					}
				}
				
				if (detruireAvecReseau) {
					if (idReseau != null) {
						String selectAutres = "SELECT * FROM " + managerDataSource.getDatabaseSchema() + ".line WHERE PTNetworkId='"+idReseau+"';";
						Statement statementAutres = connexion.createStatement();
						ResultSet rsAutres = statementAutres.executeQuery(selectAutres);
						if (rsAutres.next())
							idReseau = null;
					}
					if (idReseau != null) {
						String netoyageReseau = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".ptnetwork WHERE id='"+idReseau+"';";
						Statement statementNetoyageReseau = connexion.createStatement();
						statementNetoyageReseau.executeUpdate(netoyageReseau);
					}
				}
				
				connexion.commit();
			}
			catch(Exception e) {
				try {
					if (connexion != null)
						connexion.rollback();
				}
				catch(Exception ex) {
					addActionError("Erreur de rollback de la ligne : "+ex.getMessage());
				}
				addActionError("Erreur de destruction de la ligne : "+e.getMessage());
				for (int i = 0; i < e.getStackTrace().length; i++)
					addActionError(e.getStackTrace()[i].toString());
				return INPUT;
				//throw new RuntimeException(e);
			}
			finally {
				try {
					if (connexion != null)
						connexion.close();
				}
				catch(Exception e) {
					addActionError("Erreur de fermeture de connexion : "+e.getMessage());
				}
			}
		}
		addActionMessage("Ligne détruite.");
		return SUCCESS;
	}
	
	public String cancel()
	{
		addActionMessage(getText("ligne.cancel.ok"));
		return SUCCESS;
	}

	public String delete() throws Exception
	{
		//	Enregistrement du fichier CHOUETTE ou AMIVIF
		if(typeLigne == CHOUETTE)
			return "chouette";
		else
			return "amivif";
	}

	public String edit()
	{
		return INPUT;
	}

	public Long getIdLigne()
	{
		return idLigne;
	}

	public Long getIdReseau()
	{
		return (Long)session.get("idReseau");
	}

	public Long getIdTransporteur()
	{
		return (Long)session.get("idTransporteur");
	}

	public Ligne getLigne()
	{
		return ligne;
	}

	public List<Ligne> getLignes()
	{
		return listeLigne;
	}

	public Reseau getReseauParIdReseau(Long idReseau)
	{
		return reseauParId.get( idReseau);
	}

	public List<Reseau> getReseaux()
	{
		return listeReseau;
	}

	public Transporteur getTransporteurParIdTransporteur(Long idTransporteur)
	{
		return transporteurParId.get( idTransporteur);
	}

	public List<Transporteur> getTransporteurs()
	{
		return listeTransporteur;
	}

	@Override
	public String input() throws Exception
	{
		return INPUT;
	}

	@SkipValidation
	public String list() {
		
		if ("".equals(nomLigne)) nomLigne = null;
		
		listeLigne = 
			ligneManager.select (
				new AndClause()
					.add(ScalarClause.newEqualsClause("PTNetworkId", idReseauzz))
					.add(ScalarClause.newEqualsClause("companyId", idTransporteurzz))
					.add(ScalarClause.newIlikeClause("name", nomLigne)));
		
		return SUCCESS;
	}

	public void prepare() throws Exception
	{
		
		// Chargement de la ligne au besoin
		if (idLigne != null)
		{
			ligne = ligneManager.lire(idLigne);
		}
		
		// Chargement des réseaux
		listeReseau = reseauManager.lire();
		reseauParId = new Hashtable<Long, Reseau>();
		for (Reseau reseau : listeReseau) 
		{
			reseauParId.put( reseau.getId(), reseau);
		}
		
		//	Chargement des transporteurs
		listeTransporteur = transporteurManager.lire();
		transporteurParId = new Hashtable<Long, Transporteur>();
		for (Transporteur transporteur : listeTransporteur) 
		{
			transporteurParId.put( transporteur.getId(), transporteur);
		}
	}

	public void setIdLigne(Long idLigne)
	{
		this.idLigne = idLigne;
	}

	public void setIdReseau(Long idReseau)
	{
		session.put("idReseau", idReseau);
	}

	public void setIdTransporteur(Long idTransporteur)
	{
		session.put("idTransporteur", idTransporteur);
	}

	public void setLigne(Ligne ligne)
	{
		this.ligne = ligne;
	}

	public void setLigneManager(ILigneManager ligneManager)
	{
		this.ligneManager = ligneManager;
	}

	public void setLignes(List<Ligne> lignes)
	{
		this.listeLigne = lignes;
	}

	public void setReseauManager(IReseauManager reseauManager)
	{
		this.reseauManager = reseauManager;
	}

	public void setReseaux(List<Reseau> reseaux)
	{
		this.listeReseau = reseaux;
	}

	public void setTransporteurManager(ITransporteurManager transporteurManager)
	{
		this.transporteurManager = transporteurManager;
	}

	public void setTransporteurs(List<Transporteur> transporteurs)
	{
		this.listeTransporteur = transporteurs;
	}

	public void setTypeLigne(String typeLigne)
	{
		this.typeLigne = typeLigne;
	}

	public String update()
	{
		if (ligne == null)
		{
			return INPUT;
		}

		if(ligneManager.nomConnu(ligne.getName()))
			addActionMessage(getText("ligne.homonyme"));		
		
		// 	Prise en compte du cas où il n'y a pas de reéseau ou de transporteur sélectionné
		if(ligne.getIdReseau().equals(new Long(-1)))
			ligne.setIdReseau(null);
		if(ligne.getIdTransporteur().equals(new Long(-1)))
			ligne.setIdTransporteur(null);
		
		if (ligne.getId() == null)
		{
			ligneManager.creer(ligne);
			addActionMessage(getText("ligne.create.ok"));
		} 
		else {
			ligneManager.modifier(ligne);
			addActionMessage(getText("ligne.update.ok"));
		}

		return INPUT;
	}
	
	public String createAndEdit()
	{
		if (ligne == null)
		{
			return INPUT;
		}

		if(ligneManager.nomConnu(ligne.getName()))
			addActionMessage(getText("ligne.homonyme"));		
		
		// 	Prise en compte du cas où il n'y a pas de reéseau ou de transporteur sélectionné
		if(ligne.getIdReseau().equals(new Long(-1)))
			ligne.setIdReseau(null);
		if(ligne.getIdTransporteur().equals(new Long(-1)))
			ligne.setIdTransporteur(null);
		
		if (ligne.getId() == null)
		{
			ligneManager.creer(ligne);
			addActionMessage(getText("ligne.create.ok"));
		} 
		else
			return INPUT;

		return CREATEANDEDIT;
	}

	public String getUseAmivif() {
		return useAmivif;
	}
	
	public void setUseAmivif(String useAmivif) {
		this.useAmivif = useAmivif;
	}

	public String getNomLigne() {
		return nomLigne;
	}

	public void setNomLigne(String nomLigne) {
		this.nomLigne = nomLigne;
	}

	public Long getIdReseauzz() {
		return idReseauzz;
	}

	public void setIdReseauzz(Long idReseauzz) {
		this.idReseauzz = idReseauzz;
	}

	public Long getIdTransporteurzz() {
		return idTransporteurzz;
	}

	public void setIdTransporteurzz(Long idTransporteurzz) {
		this.idTransporteurzz = idTransporteurzz;
	}
	
	public void setManagerDataSource(ChouetteDriverManagerDataSource managerDataSource) {
		this.managerDataSource = managerDataSource;
	}
}
