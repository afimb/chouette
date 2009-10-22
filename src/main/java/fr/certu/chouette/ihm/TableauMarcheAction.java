package fr.certu.chouette.ihm;

import chouette.schema.types.DayTypeType;
import com.opensymphony.xwork2.Preparable;
import fr.certu.chouette.ihm.converter.JourTypeTMConverter;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.database.ICourseManager;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.database.ITableauMarcheManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TableauMarcheAction extends GeneriqueAction implements Preparable {
	
	private static final Log				log	= LogFactory.getLog(TableauMarcheAction.class);

	private static ITableauMarcheManager	tableauMarcheManager;
	private IReseauManager					reseauManager;
	
	private static ICourseManager			courseManager;
	
	private Course course;

	private TableauMarche					tableauMarche;

	private List<TableauMarche>				tableauxMarche;
	private List<Course>					courses;

	private Long							idTableauMarche;
	
	private Date jour;
	private Date debut;
	private Date fin;
	
	private Integer idxDate;
	private Integer idxPeriod;
	private Integer idxJourney;
	private List<Course> allCourses;
	private Long idJourney;
	private List<Long> idJourneys;	
	
	private List<DayTypeType> joursTypes;
	
	private List<Reseau> reseaux;
	private Map<Long, Reseau> reseauParId;
	
	private String commentaire = null;
	private Long idReseau = null;
	private Date dateDebutPeriode = null;
	private Date dateFinPeriode = null;
	
	//	Chaine de caractere implémenté pour complété les retours des actions fait par struts
	String CREATEANDEDIT = "createAndEdit";		
	
	public String cancel()
	{
		addActionMessage(getText("tableauMarche.cancel.ok"));
		return SUCCESS;
	}	
	
	public String delete()
	{
		tableauMarcheManager.supprimer(idTableauMarche);
		ArrayList args = new ArrayList();
		args.add(tableauMarche.getObjectId());
		addActionMessage(getText("tableauMarche.delete.ok", args));
		return SUCCESS;
	}

	public String edit()
	{	
		return INPUT;
	}

	public TableauMarche getTableauMarche()
	{
		return tableauMarche;
	}

	@Override
	public String input() throws Exception
	{
		
		return INPUT;
	}

	public String list() {
		
		if ("".equals(commentaire)) commentaire = null;
		if ("".equals(dateDebutPeriode)) dateDebutPeriode = null;
		if ("".equals(dateFinPeriode)) dateFinPeriode = null;
		
		tableauxMarche = tableauMarcheManager.lire(dateDebutPeriode, dateFinPeriode, commentaire, idReseau);
		
		return SUCCESS;
	}

	public void prepare() throws Exception
	{
		// Chargement des réseaux
		reseaux = reseauManager.lire();
		reseauParId = new Hashtable<Long, Reseau>();
		for (Reseau reseau : reseaux) 
		{
			reseauParId.put( reseau.getId(), reseau);
		}
		
		//allCourses = courseManager.lire();
		if (idTableauMarche == null) {
			
			return;
		}
		
		//	Création de la liste des types de jours 
		joursTypes = JourTypeTMConverter.getProperties(tableauMarche);
		
		// Création du tableau de marche
		tableauMarche = tableauMarcheManager.lire(idTableauMarche);
		
		//	Création de la liste des courses disponibles	
		//if (courses == null)
		courses =  tableauMarcheManager.getCoursesTableauMarche(idTableauMarche);
		if (idJourneys != null)
			idJourneys.clear();
		else
			idJourneys = new ArrayList<Long>();
		if (courses != null) {
			for (int i=0; i<courses.size(); i++) {
				this.idJourneys.add(courses.get(i).getId());
			}
		}
	}

	public void setIdTableauMarche(Long idTableauMarche)
	{
		this.idTableauMarche = idTableauMarche;
	}

	public void setTableauMarche(TableauMarche tableauMarche)
	{
		this.tableauMarche = tableauMarche;
	}

	public void setTableauMarcheManager(ITableauMarcheManager tableauMarcheManager)
	{
		TableauMarcheAction.tableauMarcheManager = tableauMarcheManager;
	}
	
	public String addPeriode()
	{
		if (debut != null && fin != null) {
			Periode p = new Periode();
			p.setDebut(debut);
			p.setFin(fin);
			tableauMarche.ajoutPeriode(p);
			debut = null;
			fin = null;
			
			if ( tableauMarche.getId()==null)
			{
				tableauMarcheManager.creer(tableauMarche);
				addActionMessage(getText("tableauMarche.addperiod.ok"));
			}
			else
			{
				tableauMarcheManager.modifier(tableauMarche);
				addActionMessage(getText("tableauMarche.addperiod.ok"));
			}	
		}
		return INPUT;
	}
	
	public String deletePeriod()
	{
		if (this.idxPeriod != null) {
			Periode p = tableauMarche.getPeriodes().get(idxPeriod.intValue() - 1);
			tableauMarche.retraitPeriode(p);
			idxPeriod = null;
		}
		if ( tableauMarche.getId()==null)
		{
			tableauMarcheManager.creer(tableauMarche);
			addActionMessage(getText("tableauMarche.deleteperiod.ok"));
		}
		else
		{
			tableauMarcheManager.modifier(tableauMarche);
			addActionMessage(getText("tableauMarche.deleteperiod.ok"));
		}	
		
		return INPUT;
	}
	
	public String deleteJourney()
	{
		//tableauMarcheManager.associerTableauMarcheCourses(this.idTableauMarche, idCourses);
		return INPUT;
	}
	
	public String addJourney()
	{			
		if (idJourneys != null && idJourneys.size() > 0) {
			tableauMarcheManager.associerTableauMarcheCourses(this.idTableauMarche, idJourneys);
			// Refresh the journey list
			courses =  tableauMarcheManager.getCoursesTableauMarche(idTableauMarche);
		}
		
		return INPUT;
	}
	
	public String addDate()
	{
		if (jour != null) {
			tableauMarche.ajoutDate(jour);			
			jour = null;
		}
		
		if ( tableauMarche.getId()==null)
		{
			tableauMarcheManager.creer(tableauMarche);
			addActionMessage(getText("tableauMarche.addcalendarday.ok"));
		}
		else
		{
			tableauMarcheManager.modifier(tableauMarche);
			addActionMessage(getText("tableauMarche.addcalendarday.ok"));
		}			

		return INPUT;
	}
	
	public String deleteDate()
	{
		if (this.idxDate != null) {
			Date d = tableauMarche.getDates().get(idxDate.intValue() - 1);
			tableauMarche.retraitDate(d);
			idxDate = null;
		}
		if ( tableauMarche.getId()==null)
		{
			tableauMarcheManager.creer(tableauMarche);
			addActionMessage(getText("tableauMarche.deletecalendarday.ok"));
		}
		else
		{
			tableauMarcheManager.modifier(tableauMarche);
			addActionMessage(getText("tableauMarche.deletecalendarday.ok"));
		}	
		
		return INPUT;
	}

	public String update()
	{					
		log.debug("update");
		if (tableauMarche == null) { return INPUT; }
		
		if ( tableauMarche.getId()==null)
		{
			tableauMarcheManager.creer(tableauMarche);
			String []args = new String[1];
			args[0] = tableauMarche.getObjectId();
			addActionMessage(getText("tableauMarche.create.ok", args));
		}
		else
		{
			JourTypeTMConverter.setDayTypes(tableauMarche, joursTypes);
			tableauMarcheManager.modifier(tableauMarche);
			String []args = new String[1];
			args[0] = tableauMarche.getObjectId();
			addActionMessage(getText("tableauMarche.update.ok", args));
		}			

		return INPUT;
	}
	
	public String createAndEdit()
	{					
		log.debug("update");
		if (tableauMarche == null) { return INPUT; }
		
		if ( tableauMarche.getId()==null)
		{
			tableauMarcheManager.creer(tableauMarche);
			String []args = new String[1];
			args[0] = tableauMarche.getObjectId();
			addActionMessage(getText("tableauMarche.create.ok", args));
		}
		else
			return INPUT;			

		return CREATEANDEDIT;
	}	
	
	public List<Course> getCourses() {
		return courses;
	}

	public List<TableauMarche> getTableauxMarche()
	{
		return tableauxMarche;
	}

	public void setTableauxMarche(List<TableauMarche> tableauxMarche)
	{
		this.tableauxMarche = tableauxMarche;
	}

	public void setCourseManager(ICourseManager courseManager)
	{
		TableauMarcheAction.courseManager = courseManager;
	}

	public Date getJour()
	{
		return jour;
	}

	public void setJour(Date jour)
	{
		this.jour = jour;
	}
	
	public Date getDebut()
	{
		return debut;
	}

	public void setDebut(Date debut)
	{
		this.debut = debut;
	}
	
	public Date getFin()
	{
		return fin;
	}

	public void setFin(Date fin)
	{
		this.fin = fin;
	}
	
	public void ajouterMarche(TableauMarche tableauMarche)
	{
		tableauxMarche.add(tableauMarche);
	}
	
	public void supprimerMarche(int indexTableauMarche)
	{
		tableauxMarche.remove(indexTableauMarche);
	}

	public Integer getIdxDate() {
		return idxDate;
	}

	public void setIdxDate(Integer idxDate) {
		this.idxDate = idxDate;
	}

	public Integer getIdxPeriod() {
		return idxPeriod;
	}

	public void setIdxPeriod(Integer idxPeriod) {
		this.idxPeriod = idxPeriod;
	}

	public Integer getIdxJourney() {
		return idxJourney;
	}

	public void setIdxJourney(Integer idxJourney) {
		this.idxJourney = idxJourney;
	}

	public List<Course> getAllCourses() {
		return allCourses;
	}

	public void setAllCourses(List<Course> allCourses) {
		this.allCourses = allCourses;
	}

	public Long getIdJourney() {
		return idJourney;
	}

	public void setIdJourney(Long idJourney) {
		this.idJourney = idJourney;
	}

	public List<Long> getIdJourneys() {
		return idJourneys;
	}

	public void setIdJourneys(List<Long> idJourneys) {
		this.idJourneys = idJourneys;
	}
	
	public List<DayTypeType> getJoursTypes()
	{
		return JourTypeTMConverter.getProperties(tableauMarche);
	}

	public void setJoursTypes(List<DayTypeType> joursTypes)
	{
		this.joursTypes = joursTypes;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public Long getIdReseau() {
		return idReseau;
	}

	public void setIdReseau(Long idReseau) {
		this.idReseau = idReseau;
	}

	public Date getDateDebutPeriode() {
		return dateDebutPeriode;
	}

	public void setDateDebutPeriode(Date dateDebutPeriode) {
		this.dateDebutPeriode = dateDebutPeriode;
	}

	public Date getDateFinPeriode() {
		return dateFinPeriode;
	}

	public void setDateFinPeriode(Date dateFinPeriode) {
		this.dateFinPeriode = dateFinPeriode;
	}

	public IReseauManager getReseauManager() {
		return reseauManager;
	}

	public void setReseauManager(IReseauManager reseauManager) {
		this.reseauManager = reseauManager;
	}

	public List<Reseau> getReseaux() {
		return reseaux;
	}

	public void setReseaux(List<Reseau> reseaux) {
		this.reseaux = reseaux;
	}

	public Map<Long, Reseau> getReseauParId() {
		return reseauParId;
	}

	public void setReseauParId(Map<Long, Reseau> reseauParId) {
		this.reseauParId = reseauParId;
	}	
}