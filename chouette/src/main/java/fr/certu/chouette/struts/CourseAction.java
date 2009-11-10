package fr.certu.chouette.struts;

import com.opensymphony.xwork2.Preparable;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.database.ICourseManager;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.ITableauMarcheManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

@SuppressWarnings({"serial", "unused", "unchecked"})
public class CourseAction extends GeneriqueAction implements Preparable, RequestAware {
	
	private static final Log                   log = LogFactory.getLog(CourseAction.class);
	//	Managers
	private              ICourseManager        courseManager;
	private              IItineraireManager    itineraireManager;
	private              ITableauMarcheManager tableauMarcheManager;
	private              ILigneManager         ligneManager;	
	//	Identifiants 
	private              Long                  idCourse;
	private              Long                  idLigne;
	private              Long                  idItineraire;
	private              Long                  idTableauMarche;
	private              Date                  seuilDateDepartCourse;
	private              Long                  page;
	//	Liste des courses et course sélectionnée
	private              List<Course>          courses;
	private              Course                course;
	private              String                saisieTableauMarche;
	private              Long                  saisieTableauMarcheKey;
	private              List<TableauMarche>   tableauxMarcheAssocieCourse;
	private              List<TableauMarche>   tableauxMarchePasAssocieCourse;
	//	Requete
	private              Map                   request;
	
	public String cancel() {
		addActionMessage(getText("course.cancel.ok"));
		return SUCCESS;
	}
	
	public Ligne getLigne() {
		return ligneManager.lire(idLigne);
	}
	
	public Itineraire getItineraire() {
		return itineraireManager.lire(idItineraire);
	}
	
	public Course getCourse() {
		return course;
	}
	
	public void setCourse(Course course) {
		this.course = course;
	}
	
	public List<Course> getCourses() {
		return courses;
	}
	
	public void setIdCourse(Long idCourse) {
		this.idCourse = idCourse;
	}
	
	public void setCourseManager(ICourseManager courseManager) {
		this.courseManager = courseManager;
	}
	
	@SkipValidation
	public String delete() {
		courseManager.supprimer(idCourse);
		addActionMessage(getText("course.delete.ok"));
		return SUCCESS;
	}
	
	public String update() {
		if (course == null)
			return INPUT;
		// ré-affecter l'identifiant de la ligne sur l'itinéraire
		course.setIdItineraire(idItineraire);
		if (course.getId() == null) {
			courseManager.creer(course);
			addActionMessage(getText("course.create.ok"));
		}
		else {
			courseManager.modifier(course);
			addActionMessage(getText("course.update.ok"));
		}
		return SUCCESS;
	}
	
	@SkipValidation
	public String list() {
		// Récupération des itinéraires pour un identifiant de
		// Itineraire donnée
		courses = itineraireManager.getCoursesItineraire(idItineraire);
		return SUCCESS;
	}
	
	@Override
	@SkipValidation
	public String input() throws Exception {
		return INPUT;
	}
	
	public void setItineraireManager(IItineraireManager itineraireManager) {
		this.itineraireManager = itineraireManager;
	}
	
	@SkipValidation
	public void prepare() throws Exception {
		if (idCourse != null) {
			course = courseManager.lire(idCourse);
			//	Création d'une map idTableauMarche -> TableauMarche
			List<TableauMarche> tableauxMarches= tableauMarcheManager.lireSansDateNiPeriode();
			//	Récupération des tableaux de marche associés à la course
			tableauxMarcheAssocieCourse = courseManager.getTableauxMarcheCourse(idCourse);			
			// Récupération de la liste des ids de tous les tableaux de marche
			Map<Long, TableauMarche> tableauxMarcheParId = new HashMap<Long, TableauMarche>();
			for (TableauMarche tableauMarche : tableauxMarches)
				tableauxMarcheParId.put(tableauMarche.getId(), tableauMarche);
			//	Elimination dans la liste des tableaux de marche ceux déjà associés à la course
			for (TableauMarche tableauMarche : tableauxMarcheAssocieCourse)
				if (tableauxMarcheParId.containsKey(tableauMarche.getId()))
					tableauxMarcheParId.remove(tableauMarche.getId());
			//	Récupération des tableaux de marche non associés à la course
			tableauxMarchePasAssocieCourse = new ArrayList<TableauMarche>();
			tableauxMarchePasAssocieCourse.addAll(tableauxMarcheParId.values());
			//	Place en requête la liste des tableaux de marche pas associé à la course
			request.put("jsonTableauMarches", getJsonTableauMarches());
		}
	}
	
	@SkipValidation
	public String edit() {
		return INPUT;
	}
	
	public List<TableauMarche> getTableauxMarche() {
		return tableauxMarcheAssocieCourse;
	}
	
	public List<TableauMarche> getTableauxMarchePasAssocieCourse() {
		return tableauxMarchePasAssocieCourse;
	}
	
	@SkipValidation
	public String creerAssociationTableauMarche() {
		//	Récupération des id des tableau de marche associés à la course
		List<Long> idsTableauxMarcheAssocieCourse = new ArrayList<Long>();
		for (TableauMarche tableauMarche : tableauxMarcheAssocieCourse)
			idsTableauxMarcheAssocieCourse.add(tableauMarche.getId());
		//	Récupération des id des tableau de marche non associés à la course
		List<Long> idsTableauxMarchePasAssocieCourse = new ArrayList<Long>();
		for (TableauMarche tableauMarche : tableauxMarchePasAssocieCourse)
			idsTableauxMarchePasAssocieCourse.add(tableauMarche.getId());
		//	Ajout de l'id du tableau de marche sélectionné dans la liste déroulante à la liste
		if(saisieTableauMarcheKey != null && idsTableauxMarchePasAssocieCourse.contains(saisieTableauMarcheKey)) {
			idsTableauxMarcheAssocieCourse.add(saisieTableauMarcheKey);
			tableauMarcheManager.associerCourseTableauxMarche(idCourse, idsTableauxMarcheAssocieCourse);
			addActionMessage(getText("course.associationTableauMarche.ok"));
		}
		else
			addActionError(getText("course.associationTableauMarche.ko"));
		return SUCCESS;
	}
	
	@SkipValidation
	public String supprimerAssociationTableauMarche() {
		List<Long> idTableauxMarche = new ArrayList<Long>();
		for (TableauMarche tableauMarche : tableauxMarcheAssocieCourse)
			idTableauxMarche.add(tableauMarche.getId());
		idTableauxMarche.remove(idTableauMarche);
		tableauMarcheManager.associerCourseTableauxMarche(idCourse, idTableauxMarche);
		return SUCCESS;
	}
	
	public void setTableauMarcheManager(ITableauMarcheManager tableauMarcheManager) {
		this.tableauMarcheManager = tableauMarcheManager;
	}
	
	public void setIdTableauMarche(Long idTableauMarche) {
		this.idTableauMarche = idTableauMarche;
	}
	
	public Long getIdTableauMarche(){
		return idTableauMarche;
	}

	public Long getIdCourse() {
		return idCourse;
	}

	public Long getIdItineraire() {
		return idItineraire;
	}

	public void setIdItineraire(Long idItineraire) {
		this.idItineraire = idItineraire;
	}
	
	public Long getIdLigne() {
		return idLigne;
	}
	
	public void setIdLigne(Long idLigne) {
		this.idLigne = idLigne;
	}
	
	public String getJsonTableauMarches() {
		StringBuffer resultat = new StringBuffer( "{");
		TableauMarche dernier = null;
		List<TableauMarche> tms = tableauxMarchePasAssocieCourse;
		if (tms.size() > 0)
			dernier = tms.remove( tms.size()-1);
		for (TableauMarche tm : tms) {
			resultat.append( "\"");
			resultat.append( tm.getComment());
			resultat.append( "(");
			resultat.append( tm.getObjectId());
			resultat.append( ")\": ");
			resultat.append( tm.getId());
			resultat.append( ",");
		}
		if ( dernier!=null) {
			resultat.append( "\"");
			resultat.append( dernier.getComment());
			resultat.append( "(");
			resultat.append( dernier.getObjectId());
			resultat.append( ")\": ");
			resultat.append( dernier.getId());
		}
		resultat.append( "}");
		// bien penser remettre élément dans la liste pour qu'elle demeure inchangée
		tableauxMarchePasAssocieCourse.add( dernier);
		return resultat.toString();
	}

	public void setSaisieTableauMarcheKey(Long saisieTableauMarcheKey) {
		log.debug("saisieTableauMarcheKey : " + saisieTableauMarcheKey);
		this.saisieTableauMarcheKey = saisieTableauMarcheKey;
	}
	
	public void setSaisieTableauMarche(String saisieTableauMarche) {
		this.saisieTableauMarche = saisieTableauMarche;
	}
	
	public void setRequest(Map request) {
		this.request = request;
	}
	
	public String getModeTransportLigne() {
		Ligne ligne = ligneManager.lire(idLigne);
		return ligne.getTransportModeName().toString();
	}
	
	public void setLigneManager(ILigneManager ligneManager) {
		this.ligneManager = ligneManager;
	}
	
	public void setPage(Long page) {
		this.page = page;
	}
	
	public Long getPage() {
		return page;
	}
	
	public void setSeuilDateDepartCourse(Date seuilDateDepartCourse) {
		this.seuilDateDepartCourse = seuilDateDepartCourse;
	}
	
	public Date getSeuilDateDepartCourse() {
		return seuilDateDepartCourse;
	}
}
