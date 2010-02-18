package fr.certu.chouette.service.database.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.Ordre;
import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.database.ITableauMarcheManager;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class TableauMarcheManager implements ITableauMarcheManager
{
    private static final Logger logger = Logger.getLogger( TableauMarcheManager.class);
	private ITemplateDao<TableauMarche> tableauMarcheDao;
	private ISelectionSpecifique selectionSpecifique;
	private IModificationSpecifique modificationSpecifique;
	private IIdentificationManager identificationManager;

	public void modifier( TableauMarche tableauMarche)
	{
		tableauMarcheDao.update( tableauMarche);
	}
	
	public void creer( TableauMarche tableauMarche)
	{
		tableauMarcheDao.save( tableauMarche);
		String objectId = identificationManager.getIdFonctionnel("Timetable", tableauMarche);
		tableauMarche.setObjectId(objectId);
		tableauMarche.setCreationTime( new Date());
		tableauMarche.setObjectVersion( 1);
		tableauMarcheDao.update( tableauMarche);
	}

	public List<TableauMarche> select(IClause clause) {
		return tableauMarcheDao.select(clause);
	}

	public void supprimer( Long idTableauMarche) {
		modificationSpecifique.associerTableauMarcheCourses(idTableauMarche, new ArrayList<Long>());
		tableauMarcheDao.remove( idTableauMarche);
	}
	
	
	public void associerCourseTableauxMarche(Long idCourse, List<Long> idTMs) {
		modificationSpecifique.associerCourseTableauxMarche(idCourse, idTMs);
	}

	public List<Course> getCoursesTableauMarche(Long idTableauMarche) {
		return selectionSpecifique.getCoursesTableauMarche(idTableauMarche);
	}


	public void associerTableauMarcheCourses(Long idTM, List<Long> idCourses) {
		modificationSpecifique.associerTableauMarcheCourses(idTM, idCourses);
	}

	public TableauMarche lire( Long idTableauMarche)
	{
		return tableauMarcheDao.get( idTableauMarche);
	}

	public List<TableauMarche> lireSansDateNiPeriode()
	{
		return selectionSpecifique.getTableauxMarcheLazy();
	}

	public List<TableauMarche> lire()
	{
		List<Ordre> ordres = new ArrayList<Ordre>();
		ordres.add( new Ordre( "comment", true));
		ordres.add( new Ordre( "objectid", true));
		return tableauMarcheDao.select( null, ordres);
	}
	
	public List<TableauMarche> lire(Date dateDebutPeriode, Date dateFinPeriode, String commentaire, Long idReseau) {
		return selectionSpecifique.getCalendriersFiltres(dateDebutPeriode, dateFinPeriode, commentaire, idReseau);
	}

	public ITemplateDao<TableauMarche> getTableauMarcheDao() {
		return tableauMarcheDao;
	}

	public void setTableauMarcheDao(ITemplateDao<TableauMarche> tableauMarcheDao) {
		this.tableauMarcheDao = tableauMarcheDao;
	}

	public IModificationSpecifique getModificationSpecifique() {
		return modificationSpecifique;
	}

	public void setModificationSpecifique(
			IModificationSpecifique modificationSpecifique) {
		this.modificationSpecifique = modificationSpecifique;
	}

	public ISelectionSpecifique getSelectionSpecifique() {
		return selectionSpecifique;
	}

	public void setSelectionSpecifique(ISelectionSpecifique selectionSpecifique) {
		this.selectionSpecifique = selectionSpecifique;
	}

	public void setIdentificationManager(
			IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
}
