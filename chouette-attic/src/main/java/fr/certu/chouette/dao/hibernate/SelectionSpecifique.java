package fr.certu.chouette.dao.hibernate;

import chouette.schema.types.ChouetteAreaType;
import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.Ordre;
import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.InterdictionTraficLocal;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.LienTMCourse;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.util.TableauMarcheUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.HibernateQueryException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

@SuppressWarnings("unchecked")
public class SelectionSpecifique extends HibernateDaoSupport implements ISelectionSpecifique, IModificationSpecifique
{

  private final static String SENS_ALLER = "A";
  private final static String SENS_RETOUR = "R";
  private static final Logger logger = Logger.getLogger(SelectionSpecifique.class);
  private String databaseSchema;

  public void supprimerArretItineraire(final Long idArretItineraire)
  {
    final ArretItineraire arret = (ArretItineraire) getHibernateTemplate().get(ArretItineraire.class, idArretItineraire);
    if (arret == null)
    {
      throw new ServiceException(CodeIncident.IDENTIFIANT_INCONNU, CodeDetailIncident.STOPPOINT_ID, idArretItineraire);
    }
    final Session session = getSession();

    String sqlRequeteHoraire = "DELETE Horaire h ";
    sqlRequeteHoraire += "WHERE h.idArret = " + idArretItineraire;
    String sqlRequetePosition = "UPDATE ArretItineraire SET position = position - 1 ";
    sqlRequetePosition += "WHERE idItineraire = " + arret.getIdItineraire() + " ";
    sqlRequetePosition += "AND position > " + arret.getPosition();
    session.createQuery(sqlRequeteHoraire).executeUpdate();
    getHibernateTemplate().delete(arret);
    session.createQuery(sqlRequetePosition).executeUpdate();
  }

  public void supprimerCourse(final Long idCourse)
  {
    final Session session = getSession();
    String sqlRequeteLienTM = "DELETE LienTMCourse l ";
    sqlRequeteLienTM += "WHERE l.idCourse=" + idCourse;
    String sqlRequeteHoraire = "DELETE Horaire h ";
    sqlRequeteHoraire += "WHERE h.idCourse=" + idCourse;
    String sqlRequeteCourse = "DELETE Course c ";
    sqlRequeteCourse += "WHERE c.id=" + idCourse;
    session.createQuery(sqlRequeteLienTM).executeUpdate();
    session.createQuery(sqlRequeteHoraire).executeUpdate();
    session.createQuery(sqlRequeteCourse).executeUpdate();
  }

  public void supprimerGeoPositions(final Collection<Long> idsGeoPositions)
  {
    if (isCollectionVide(idsGeoPositions))
    {
      return;
    }
    final String selectionArretsLogiques = "FROM ArretItineraire as a WHERE idPhysique in (:listParam)";
    final String[] params = new String[]
    {
      "listParam"
    };
    final Object[] values =
    {
      idsGeoPositions
    };
    List<ArretItineraire> arretsLogiques = getHibernateTemplate().findByNamedParam(selectionArretsLogiques, params, values);
    if (!arretsLogiques.isEmpty())
    {
      List<Long> idLogiques = new ArrayList<Long>();
      for (ArretItineraire arretLogique : arretsLogiques)
      {
        idLogiques.add(arretLogique.getId());
      }
      supprimerArretsItineraire(idLogiques);
    }
    String sqlSuppression = "DELETE PositionGeographique p ";
    sqlSuppression += "WHERE p.id in (" + getSQLlist(idsGeoPositions) + ")";
    getSession().createQuery(sqlSuppression).executeUpdate();
  }

  public void supprimerArretsItineraire(Collection<Long> idsArretsItineraire)
  {
    if (isCollectionVide(idsArretsItineraire))
    {
      return;
    }
    final String sqlList = getSQLlist(idsArretsItineraire);
    final Session session = getSession();
    String sqlSuppressionHoraire = "DELETE Horaire h ";
    sqlSuppressionHoraire += "WHERE h.idArret in (" + sqlList + ")";
    String sqlSuppressionALogique = "DELETE ArretItineraire a ";
    sqlSuppressionALogique += "WHERE a.id in (" + sqlList + ")";
    session.createQuery(sqlSuppressionHoraire).executeUpdate();
    session.createQuery(sqlSuppressionALogique).executeUpdate();
  }

  public void supprimerHorairesItineraire(final Long idItineraire)
  {
    final Session session = getSession();
    String clause = "select course.id from Course as course where course.idItineraire=" + idItineraire;
    String sqlRequeteHoraire = "DELETE Horaire horaire ";
    sqlRequeteHoraire += "WHERE horaire.idCourse in (" + clause + ")";
    session.createQuery(sqlRequeteHoraire).executeUpdate();
  }

  public void supprimerItineraire(final Long idItineraire)
  {
    supprimerItineraire(idItineraire, false, false);
  }

  public void supprimerItineraire(final Long idItineraire, boolean detruireAvecTMs, boolean detruireAvecArrets)
  {
    //	TODO
    //}
    //public void supprimerItineraire(final List<Long> idItineraire, boolean detruireAvecTMs, boolean detruireAvecArrets) {
    final Session session = getSession();
    String clause = "select vv.id from Course as vv where vv.idItineraire=" + idItineraire;
    String sqlRequeteLienTM = "DELETE LienTMCourse l WHERE l.idCourse in (" + clause + ")";
    String sqlRequeteHoraire = "DELETE Horaire h WHERE h.idCourse in (" + clause + ")";
    String sqlRequeteCourse = "DELETE Course c WHERE c.id in (" + clause + ")";
    List<Long> missionIds = null;
    try
    {
      missionIds = getHibernateTemplate().find("SELECT DISTINCT new java.lang.Long(vv.idMission) FROM Course vv WHERE vv.idItineraire=" + idItineraire + " AND vv.idMission IS NOT NULL");
    } catch (HibernateQueryException e)
    {
      missionIds = null;
    }
    String sqlRequeteMission = "DELETE Mission m WHERE m.id IN (" + getSQLlist(missionIds) + ")";
    List<Long> tableauMarcheIds = null;
    if (detruireAvecTMs)
    {
      tableauMarcheIds = null;
      try
      {
        tableauMarcheIds = getHibernateTemplate().find("SELECT DISTINCT new java.lang.Long(tv.idTableauMarche) FROM LienTMCourse tv WHERE tv.idCourse IN (" + clause + ")");
      } catch (HibernateQueryException e)
      {
        tableauMarcheIds = null;
      }
      if ((tableauMarcheIds != null) && (tableauMarcheIds.size() > 0))
      {
        try
        {
          List<Long> otherTableauMarcheIds = getHibernateTemplate().find("SELECT DISTINCT new java.lang.Long(tv.idTableauMarche) FROM LienTMCourse tv WHERE tv.idCourse NOT IN (" + clause + ")");
          tableauMarcheIds.removeAll(otherTableauMarcheIds);
        } catch (HibernateQueryException e)
        {
        }
      }
    }
    session.createQuery(sqlRequeteLienTM).executeUpdate();
    if (detruireAvecTMs)
    {
      String tableauMarcheIdsList = getSQLlist(tableauMarcheIds);
      if ((tableauMarcheIdsList != null) && (tableauMarcheIdsList.length() > 0))
      {
        String sqlRequetePeriode = "DELETE FROM " + getDatabaseSchema() + ".timetable_period WHERE timetableid IN (" + tableauMarcheIdsList + ")";
        String sqlRequeteDate = "DELETE FROM " + getDatabaseSchema() + ".timetable_date WHERE timetableid IN (" + tableauMarcheIdsList + ")";
        String sqlRequeteTM = "DELETE TableauMarche WHERE id IN (" + tableauMarcheIdsList + ")";
        session.createSQLQuery(sqlRequetePeriode).executeUpdate();
        session.createSQLQuery(sqlRequeteDate).executeUpdate();
        session.createQuery(sqlRequeteTM).executeUpdate();
      }
    }
    session.createQuery(sqlRequeteHoraire).executeUpdate();
    session.createQuery(sqlRequeteCourse).executeUpdate();
    if ((missionIds != null) && (missionIds.size() > 0))
    {
      session.createQuery(sqlRequeteMission).executeUpdate();
    }
    List<Long> arretIds = null;
    if (detruireAvecArrets)
    {
      try
      {
        arretIds = getHibernateTemplate().find("SELECT DISTINCT new java.lang.Long(sp.idPhysique) FROM ArretItineraire sp WHERE sp.idItineraire=" + idItineraire + " AND sp.idPhysique IS NOT NULL");
        arretIds.removeAll(getHibernateTemplate().find("SELECT DISTINCT new java.lang.Long(sp.idPhysique) FROM ArretItineraire sp WHERE sp.idItineraire!=" + idItineraire + " AND sp.idPhysique IS NOT NULL"));
      } catch (HibernateQueryException e)
      {
      }
    }
    String sqlRequeteArret = "DELETE ArretItineraire a WHERE a.idItineraire=" + idItineraire;
    String sqlRequeteItineraireTmp = "UPDATE Itineraire i SET i.idRetour=NULL WHERE i.idRetour=" + idItineraire;
    String sqlRequeteItineraire = "DELETE Itineraire i WHERE i.id=" + idItineraire;
    session.createQuery(sqlRequeteArret).executeUpdate();
    session.createQuery(sqlRequeteItineraireTmp).executeUpdate();
    session.createQuery(sqlRequeteItineraire).executeUpdate();
    if (detruireAvecArrets)
    {
      if ((arretIds != null) && (arretIds.size() == 0))
      {
        arretIds = null;
      }
      while (arretIds != null)
      {
        String arretIdsList = getSQLlist(arretIds);
        List<Long> tmpArretIds = null;
        try
        {
          tmpArretIds = getHibernateTemplate().find("SELECT DISTINCT new java.lang.Long(sa.idParent) FROM PositionGeographique sa WHERE sa.id IN (" + arretIdsList + ")");
        } catch (HibernateQueryException e)
        {
        }
        String sqlRequeteCorrespondance = "DELETE Correspondance c WHERE c.idDepart IN (" + arretIdsList + ") OR c.idArrivee IN (" + arretIdsList + ")";
        String sqlRequeteITL = "DELETE FROM " + getDatabaseSchema() + ".routingConstraint_stoparea WHERE stopareaId IN (" + arretIdsList + ")";
        String sqlRequeteArretPhysique = "DELETE PositionGeographique pg WHERE pg.id IN (" + arretIdsList + ")";
        session.createQuery(sqlRequeteCorrespondance).executeUpdate();
        if ((arretIdsList != null) && (arretIdsList.length() > 0))
        {
          session.createSQLQuery(sqlRequeteITL).executeUpdate();
        }
        session.createQuery(sqlRequeteArretPhysique).executeUpdate();
        if ((tmpArretIds != null) && (tmpArretIds.size() > 0))
        {
          String _arretIdsList = getSQLlist(tmpArretIds);
          try
          {
            List<Long> _tmpArretIds = getHibernateTemplate().find("SELECT DISTINCT new java.lang.Long(sa.idParent) FROM PositionGeographique sa WHERE sa.idParent IN (" + _arretIdsList + ")");
            tmpArretIds.removeAll(_tmpArretIds);
          } catch (HibernateQueryException e)
          {
          }
        }
        if ((tmpArretIds != null) && (tmpArretIds.size() > 0))
        {
          arretIds = tmpArretIds;
        } else
        {
          arretIds = null;
        }
      }
    }
  }

  public void supprimerLigne(final Long idLigne, boolean detruireAvecTMs, boolean detruireAvecArrets, boolean detruireAvecTransporteur, boolean detruireAvecReseau)
  {
    final Session session = getSession();
    long timeTo = System.currentTimeMillis();
    List<Long> idReseaux = null;
    if (detruireAvecReseau)
    {
      try
      {
        idReseaux = getHibernateTemplate().find("SELECT new java.lang.Long(l.idReseau) FROM Ligne l WHERE l.id=? AND l.idReseau IS NOT NULL", idLigne);
      } catch (HibernateQueryException e)
      {
      }
    }
    List<Long> idTransporteurs = null;
    if (detruireAvecTransporteur)
    {
      try
      {
        idTransporteurs = getHibernateTemplate().find("SELECT new java.lang.Long(l.idTransporteur) FROM Ligne l WHERE l.id=? AND l.idTransporteur IS NOT NULL", idLigne);
      } catch (HibernateQueryException e)
      {
      }
    }
    List<Long> itineraireIds = null;
    try
    {
      itineraireIds = getHibernateTemplate().find("SELECT DISTINCT new java.lang.Long(i.id) FROM Itineraire i WHERE i.idLigne=? AND i.idRetour IS NOT NULL", idLigne);
    } catch (HibernateQueryException e)
    {
    }
    for (Long itineraireId : itineraireIds)
    {
      supprimerItineraire(itineraireId, detruireAvecTMs, detruireAvecArrets);
    }
    try
    {
      itineraireIds = getHibernateTemplate().find("SELECT DISTINCT new java.lang.Long(i.id) FROM Itineraire i WHERE i.idLigne=?", idLigne);
    } catch (HibernateQueryException e)
    {
    }
    for (Long itineraireId : itineraireIds)
    {
      supprimerItineraire(itineraireId, detruireAvecTMs, detruireAvecArrets);
    }
    supprimerITL(idLigne);
    String sqlRequete = "DELETE Ligne l WHERE l.id=" + idLigne;
    getSession().createQuery(sqlRequete).executeUpdate();
    if (detruireAvecReseau)
    {
      if ((idReseaux != null) && (idReseaux.size() > 0))
      {
        try
        {
          idReseaux.removeAll(getHibernateTemplate().find("SELECT new java.lang.Long(l.idReseau) FROM Ligne l WHERE l.idReseau IS NOT NULL"));
        } catch (HibernateQueryException e)
        {
        }
        for (Long idReseau : idReseaux)
        {
          String sqlSuppression = "DELETE Reseau r WHERE r.id=" + idReseau;
          session.createQuery(sqlSuppression).executeUpdate();
        }
      }
    }
    if (detruireAvecTransporteur)
    {
      if ((idTransporteurs != null) && (idTransporteurs.size() > 0))
      {
        try
        {
          idTransporteurs.removeAll(getHibernateTemplate().find("SELECT new java.lang.Long(l.idTransporteur) FROM Ligne l WHERE l.idTransporteur IS NOT NULL"));
        } catch (HibernateQueryException e)
        {
        }
        for (Long idTransporteur : idTransporteurs)
        {
          String sqlSuppression = "DELETE Transporteur t WHERE t.id=" + idTransporteur;
          session.createQuery(sqlSuppression).executeUpdate();
        }
      }
    }
    logger.debug("SUPPRESSION DE LA LIGNE " + idLigne.longValue() + " EN " + (System.currentTimeMillis() - timeTo) + " ms.");
  }

  public void supprimerLigne(final Long idLigne)
  {
    supprimerLigne(idLigne, false, false, false, false);
  }

  private void supprimerITL(final Long idLigne)
  {
    final List<InterdictionTraficLocal> itls = getITLLigne(idLigne);
    if (itls != null && !itls.isEmpty())
    {
      Set<Long> itlIds = new HashSet<Long>();
      for (InterdictionTraficLocal itl : itls)
      {
        itlIds.add(itl.getId());
      }
      final Session session = getSession();
      String sqlLienItlArretPhysique = "DELETE FROM " + getDatabaseSchema() + ".routingConstraint_stoparea ";
      sqlLienItlArretPhysique += "WHERE routingConstraintId in (" + getSQLlist(itlIds) + ")";
      session.createSQLQuery(sqlLienItlArretPhysique).executeUpdate();
      String sqlItl = "DELETE FROM " + getDatabaseSchema() + ".routingConstraint ";
      sqlItl += "WHERE id in (" + getSQLlist(itlIds) + ")";
      session.createSQLQuery(sqlItl).executeUpdate();
    }
  }

  public void supprimerTransporteur(final Long idTransporteur)
  {
    String sqlMajLien = "UPDATE Ligne SET idTransporteur=NULL ";
    sqlMajLien += "WHERE idTransporteur=" + idTransporteur;
    String sqlSuppression = "DELETE Transporteur r ";
    sqlSuppression += "WHERE r.id=" + idTransporteur;
    final Session session = getSession();
    session.createQuery(sqlMajLien).executeUpdate();
    session.createQuery(sqlSuppression).executeUpdate();
  }

  public void supprimerReseau(final Long idReseau)
  {
    String sqlMajLien = "UPDATE Ligne SET idReseau=NULL WHERE idReseau=" + idReseau;
    String sqlSuppression = "DELETE Reseau r WHERE r.id=" + idReseau;
    final Session session = getSession();
    session.createQuery(sqlMajLien).executeUpdate();
    session.createQuery(sqlSuppression).executeUpdate();
  }

  public List<Horaire> getHorairesItineraire(final Long idItineraire)
  {
    String clause = "SELECT c.id FROM Course c WHERE c.idItineraire=" + idItineraire;
    return getHibernateTemplate().find("FROM Horaire as h WHERE h.idCourse in (" + clause + ")");
  }

  public List<Horaire> getHorairesItineraires(final Collection<Long> idItineraires)
  {
    if (isCollectionVide(idItineraires))
    {
      return new ArrayList<Horaire>();
    }
    String clause = "SELECT DISTINCT c.id FROM Course c WHERE c.idItineraire in ("
            + getSQLlist(idItineraires) + ")";
    return getHibernateTemplate().find("FROM Horaire as h WHERE h.idCourse in (" + clause + ")");
  }

  public List<Horaire> getHorairesCourse(final Long idCourse)
  {
    return getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(Horaire.class).add(Expression.eq("idCourse", idCourse)));
  }

  public List<Horaire> getHorairesCourses(final Collection<Long> idCourses)
  {
    if (isCollectionVide(idCourses))
    {
      return new ArrayList<Horaire>();
    }
    return getHibernateTemplate().find("FROM Horaire as h WHERE h.idCourse in (" + getSQLlist(idCourses) + ") ORDER BY h.idCourse");
  }

  public List<Horaire> getHorairesArretItineraire(final Long idArretItineraire)
  {
    return getHibernateTemplate().find("FROM Horaire as h WHERE h.idArret=?", idArretItineraire);
  }

  public List<Long> getPhysiqueIdAvecItineraire()
  {
    String query = "SELECT DISTINCT new java.lang.Long(sa.id) ";
    query += "FROM PositionGeographique sa, ArretItineraire sp ";
    query += "WHERE sa.id=sp.idPhysique ";
    return getHibernateTemplate().find(query);
  }

  public List<PositionGeographique> getArretPhysiqueLigne(final Long idLigne)
  {
    String clause = "SELECT sp.idPhysique ";
    clause += "FROM ArretItineraire sp, Itineraire r ";
    clause += "WHERE r.idLigne=" + idLigne + " ";
    clause += "AND sp.idItineraire=r.id ";
    return getHibernateTemplate().find("FROM PositionGeographique as p WHERE p.id in (" + clause + ") ORDER BY p.id");
  }

  public List<PositionGeographique> getArretPhysiqueItineraire(final Long idItineraire)
  {
    String clause = "SELECT sp.idPhysique ";
    clause += "FROM ArretItineraire sp ";
    clause += "WHERE sp.idItineraire=" + idItineraire + " ";
    return getHibernateTemplate().find("FROM PositionGeographique as p WHERE p.id in (" + clause + ") ORDER BY p.id");
  }

  public List<Long> getIdsHorairesItineraire(final Long idItineraire)
  {
    String query = "SELECT new java.lang.Long(h.id) ";
    query += "FROM Course c, Horaire h ";
    query += "WHERE c.idItineraire=" + idItineraire + " ";
    query += "AND h.idCourse=c.id ";
    List<Long> idHoraires = getHibernateTemplate().find(query);
    return idHoraires;
  }

  public List<Mission> getMissionsItineraire(final Long idItineraire)
  {
    String clause = "SELECT c.idMission FROM Course c WHERE c.idItineraire=" + idItineraire;
    return getHibernateTemplate().find("FROM Mission as m WHERE m.id in (" + clause + ") ORDER BY m.name");
  }

  public List<Mission> getMissions(final Collection<Long> idMissions)
  {
    if (isCollectionVide(idMissions))
    {
      return new ArrayList<Mission>();
    }
    return getHibernateTemplate().find("FROM Mission as m WHERE m.id in (" + getSQLlist(idMissions) + ")");
  }

  public List<Mission> getMissionsItineraires(final Collection<Long> idItineraires)
  {
    if (isCollectionVide(idItineraires))
    {
      return new ArrayList<Mission>();
    }
    String clause = "SELECT c.idMission FROM Course c WHERE c.idItineraire in (" + getSQLlist(idItineraires) + ")";
    return getHibernateTemplate().find("FROM Mission as m WHERE m.id in (" + clause + ") ORDER BY m.name");
  }

  public List<Couple> getIdMissionIdCourseType(final Long idItineraire)
  {
    String query = "SELECT new fr.certu.chouette.dao.hibernate.Couple(c.idMission, MIN(c.id)) ";
    query += "FROM Course c ";
    query += "WHERE c.idItineraire=" + idItineraire + " ";
    query += "AND c.idMission IS NOT NULL ";
    query += "GROUP BY c.idMission";
    return getHibernateTemplate().find(query);
  }

  public void supprimerMissionSansCourse(final Collection<Long> idMissions)
  {
    if (isCollectionVide(idMissions))
    {
      return;
    }
    String query = "DELETE Mission m ";
    query += "WHERE m.id IN (" + getSQLlist(idMissions) + ") ";
    query += "AND NOT EXISTS (SELECT c.id FROM Course c WHERE c.idMission=m.id)";
    getSession().createQuery(query).executeUpdate();
  }

  public List<Long> getIdsPremiersHorairesItineraire(final Long idItineraire)
  {
    List<ArretItineraire> arretsItineraire = getArretsItineraire(idItineraire);
    Map<Long, Integer> positionParIdArret = new Hashtable<Long, Integer>();
    for (ArretItineraire arret : arretsItineraire)
    {
      positionParIdArret.put(arret.getId(), arret.getPosition());
    }
    String query = "SELECT new fr.certu.chouette.dao.hibernate.Triplet(h.id, c.id, h.idArret) ";
    query += "FROM Course c, Horaire h ";
    query += "WHERE c.idItineraire=" + idItineraire + " ";
    query += "AND h.idCourse=c.id ";
    List<Triplet> triplets = getHibernateTemplate().find(query);
    Map<Long, Integer> positionParIdHoraire = new Hashtable<Long, Integer>();
    for (Triplet triplet : triplets)
    {
      Integer position = positionParIdArret.get(triplet.troisieme);
      positionParIdHoraire.put(triplet.premier, position);
    }
    Map<Long, Long> premierHoraireParIdCourse = new Hashtable<Long, Long>();
    for (Triplet triplet : triplets)
    {
      Long idCourse = triplet.deuxieme;
      Long idPremHoraire = premierHoraireParIdCourse.get(idCourse);
      if (idPremHoraire == null)
      {
        premierHoraireParIdCourse.put(idCourse, triplet.premier);
      } else
      {
        Integer posPrecedente = positionParIdHoraire.get(idPremHoraire);
        Integer position = positionParIdHoraire.get(triplet.premier);
        if (position.intValue() < posPrecedente.intValue())
        {
          premierHoraireParIdCourse.put(idCourse, triplet.premier);
        }
      }
    }
    return new ArrayList<Long>(premierHoraireParIdCourse.values());
  }

  public void referencerDepartsCourses(Long idItineraire)
  {
    if (idItineraire == null)
    {
      return;
    }
    List<Long> idsHorairesDepartCourse = getIdsPremiersHorairesItineraire(idItineraire);
    if (idsHorairesDepartCourse.isEmpty())
    {
      return;
    }
    String sqlEffacement = "UPDATE Horaire h SET h.depart=false ";
    sqlEffacement += "WHERE h.idCourse in (SELECT c.id from Course c WHERE c.idItineraire=" + idItineraire + ")";
    Session session = this.getSession();
    session.createQuery(sqlEffacement).executeUpdate();
    String sqlRequete = "UPDATE Horaire h SET h.depart=true ";
    sqlRequete += "WHERE h.id in (" + getSQLlist(idsHorairesDepartCourse) + ")";
    session.createQuery(sqlRequete).executeUpdate();
  }

  public List<Course> getCoursesItineraireSelonHeureDepartPremiereCourse(final Long idItineraire, final Date seuilDateDepartCourses)
  {
    String query = "SELECT new java.lang.Long(course.id) ";
    query += "FROM Course course, Horaire horaire ";
    query += "WHERE course.idItineraire = " + idItineraire + " ";
    query += "AND horaire.idCourse = course.id ";
    query += "AND horaire.depart = true ";
    query += "AND horaire.departureTime > :seuilDateDepartCourse ";
    query += "ORDER BY horaire.departureTime ";
    List<Long> idsCourses = getHibernateTemplate().findByNamedParam(query, "seuilDateDepartCourse", seuilDateDepartCourses);
    return (idsCourses.size() == 0) ? new ArrayList() : getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(Course.class).add(Expression.in("id", idsCourses)));
  }

  public List<Course> getCoursesItineraire(final Long idItineraire)
  {
    List<Course> toutesCourses = getHibernateTemplate().find("FROM Course as c WHERE c.idItineraire=? ", idItineraire);
    String query = "SELECT new java.lang.Long(c.id) ";
    query += "FROM Course c, Horaire h ";
    query += "WHERE c.idItineraire=" + idItineraire + " ";
    query += "AND h.idCourse=c.id ";
    query += "AND h.depart=true ";
    query += "ORDER BY h.departureTime ";
    List<Long> idCoursesAhoraireTriees = getHibernateTemplate().find(query);
    Map<Long, Course> courseAhoraireParId = new Hashtable<Long, Course>();
    List<Course> coursesTriees = new ArrayList<Course>();
    for (Course course : toutesCourses)
    {
      if (idCoursesAhoraireTriees.contains(course.getId()))
      {
        courseAhoraireParId.put(course.getId(), course);
      } else
      {
        coursesTriees.add(course);
      }
    }
    for (Long idCourseAhoraire : idCoursesAhoraireTriees)
    {
      coursesTriees.add(courseAhoraireParId.get(idCourseAhoraire));
    }
    return coursesTriees;
  }

  public List<Course> getCoursesItinerairesSansHoraires(final Long idItineraire)
  {
    List<Course> toutesCourses = getHibernateTemplate().find("FROM Course as c WHERE c.idItineraire=? ", idItineraire);
    String query = "SELECT new java.lang.Long(c.id) ";
    query += "FROM Course c, Horaire h ";
    query += "WHERE c.idItineraire=" + idItineraire + " ";
    query += "AND h.idCourse=c.id ";
    query += "AND h.depart=true ";
    query += "ORDER BY h.departureTime ";
    List<Long> idCoursesAhoraireTriees = getHibernateTemplate().find(query);
    List<Course> courses = new ArrayList<Course>();
    for (Course course : toutesCourses)
    {
      if (!idCoursesAhoraireTriees.contains(course.getId()))
      {
        courses.add(course);
      }
    }
    return courses;
  }

  public List<Course> getCoursesItineraires(final Collection<Long> idItineraires)
  {
    if (isCollectionVide(idItineraires))
    {
      return new ArrayList<Course>();
    }
    return getHibernateTemplate().find("FROM Course as c WHERE c.idItineraire in (" + getSQLlist(idItineraires) + ") ORDER BY c.objectId");
  }

  public List<ArretItineraire> getArretsItineraireParGeoPosition(final Long idGeoPosition)
  {
    return getHibernateTemplate().find("FROM ArretItineraire as a WHERE a.idPhysique=? ", idGeoPosition);
  }

  public List<Itineraire> getItinerairesParGeoPosition(final Long idGeoPosition)
  {
    List<ArretItineraire> arrets = getArretsItineraireParGeoPosition(idGeoPosition);
    Collection<Long> idItineraires = new HashSet<Long>();
    for (ArretItineraire arret : arrets)
    {
      idItineraires.add(arret.getIdItineraire());
    }
    if (idItineraires.isEmpty())
    {
      return new ArrayList<Itineraire>();
    }
    return getHibernateTemplate().find("FROM Itineraire as i WHERE i.id in (" + getSQLlist(idItineraires) + ")");
  }

  public List<PositionGeographique> getGeoPositions(final Collection<Long> idsGeoPositions, Ordre ordre)
  {
    String orderBy = "";
    if (isCollectionVide(idsGeoPositions))
    {
      return new ArrayList<PositionGeographique>();
    }
    if (ordre != null)
    {
      orderBy += "ORDER BY a." + ordre.getPropriete();
      if (ordre.isCroissant())
      {
        orderBy += " ASC";
      } else
      {
        orderBy += " DESC";
      }
    }
    return getHibernateTemplate().find("FROM PositionGeographique as a WHERE a.id in (" + getSQLlist(idsGeoPositions) + ") " + orderBy);
  }

  public List<PositionGeographique> getGeoPositionsDirectementContenues(final Long idParent)
  {
    return getHibernateTemplate().find("FROM PositionGeographique as a WHERE a.idParent=" + idParent);
  }

  public List<ArretItineraire> getArretsItineraire(final Long idItineraire)
  {
    return getHibernateTemplate().find("FROM ArretItineraire as a WHERE a.idItineraire=? ORDER BY a.position", idItineraire);
  }

  public List<ArretItineraire> getArretsItineraires(final Collection<Long> idItineraires)
  {
    if (isCollectionVide(idItineraires))
    {
      return new ArrayList<ArretItineraire>();
    }
    return getHibernateTemplate().find("FROM ArretItineraire as a WHERE a.idItineraire in (" + getSQLlist(idItineraires) + ") ORDER BY a.idItineraire, a.position");
  }

  public List<InterdictionTraficLocal> getITLLigne(final Long idLigne)
  {
    return getHibernateTemplate().find("FROM InterdictionTraficLocal as i WHERE i.idLigne=?", idLigne);
  }

  public List<Itineraire> getItinerairesLigne(final Long idLigne)
  {
    return getHibernateTemplate().find("FROM Itineraire as i WHERE i.idLigne=?", idLigne);
  }

  public List<Itineraire> getLigneItinerairesExportables(final Long idLigne)
  {
    return getHibernateTemplate().find("FROM Itineraire as i WHERE i.id IN (SELECT sp.idItineraire FROM ArretItineraire as sp, Itineraire as ii "
            + "WHERE sp.position=1 AND sp.idItineraire=ii.id AND ii.idLigne=?) ORDER BY i.objectId", idLigne);
  }

  public Ligne getLigneParRegistration(final String registrationNumber)
  {
    final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Ligne.class);
    detachedCriteria.add(org.hibernate.criterion.Expression.eq("registrationNumber", registrationNumber));
    final List<Ligne> lignes = getHibernateTemplate().findByCriteria(detachedCriteria);
    if (lignes.isEmpty())
    {
      throw new ServiceException(CodeIncident.NO_REGISTRE_INCONNU, CodeDetailIncident.DEFAULT, registrationNumber);
    } else if (lignes.size() > 1)
    {
      throw new ServiceException(CodeIncident.NO_REGISTRE_NON_UNIQUE, CodeDetailIncident.DEFAULT, lignes.size(), registrationNumber);
    }
    return lignes.get(0);
  }

  private List<Ligne> getToutesLignes()
  {
    return getHibernateTemplate().find("FROM Ligne as l ORDER BY l.name");
  }

  private List<Ligne> getLignesReseaux(final Collection<Long> idReseaux)
  {
    if (isCollectionVide(idReseaux))
    {
      return getToutesLignes();
    }
    return getHibernateTemplate().find("FROM Ligne as l WHERE l.idReseau in (" + getSQLlist(idReseaux) + ") ORDER BY l.name");
  }

  private List<Ligne> getLignesTransporteurs(Collection<Long> idTransporteurs)
  {
    if (isCollectionVide(idTransporteurs))
    {
      return getToutesLignes();
    }
    return getHibernateTemplate().find("FROM Ligne as l WHERE l.idTransporteur in (" + getSQLlist(idTransporteurs) + ") ORDER BY l.name");
  }

  public List<Ligne> getLignes(final Collection<Long> idLignes)
  {
    if (isCollectionVide(idLignes))
    {
      return new ArrayList<Ligne>();
    }
    return getHibernateTemplate().find("FROM Ligne as l WHERE l.id in (" + getSQLlist(idLignes) + ")");
  }

  public List<Reseau> getReseaux(final Collection<Long> idReseaux)
  {
    if (isCollectionVide(idReseaux))
    {
      return new ArrayList<Reseau>();
    }
    return getHibernateTemplate().find("FROM Reseau as r WHERE r.id in (" + getSQLlist(idReseaux) + ")");
  }

  public List<Ligne> getLignesFiltrees(final Collection<Long> idReseaux, final Collection<Long> idTransporteurs)
  {
    List<Ligne> resultat = null;
    final boolean hasReseau = isCollectionNonVide(idReseaux);
    final boolean hasTransporteur = isCollectionNonVide(idTransporteurs);
    if (hasReseau && hasTransporteur)
    {
      resultat = getHibernateTemplate().find("FROM Ligne as l WHERE l.idTransporteur in (" + getSQLlist(idTransporteurs) + ") "
              + "AND l.idReseau in (" + getSQLlist(idReseaux) + ") ORDER BY l.name");
    } else if (!hasReseau)
    {
      resultat = getLignesTransporteurs(idTransporteurs);
    } else if (!hasTransporteur)
    {
      resultat = getLignesReseaux(idReseaux);
    }
    return resultat;
  }

  public List<Ligne> getLignesReseau(final Long idReseau)
  {
    return getHibernateTemplate().find("FROM Ligne as l WHERE l.idReseau=?", idReseau);
  }

  public List<Ligne> getLignesTransporteur(final Long idTransporteur)
  {
    return getHibernateTemplate().find("FROM Ligne as l WHERE l.idTransporteur=?", idTransporteur);
  }

  public List<TableauMarche> getTableauxMarcheLazy()
  {
    Session session = getHibernateTemplate().getSessionFactory().openSession();
    List<Object[]> rs = session.createSQLQuery("SELECT id, objectid, comment FROM " + getDatabaseSchema() + ".timetable order by comment").list();
    List<TableauMarche> tms = new ArrayList<TableauMarche>(rs.size());
    for (Object[] object : rs)
    {
      TableauMarche tm = new TableauMarche();
      tm.setId(Long.valueOf(object[ 0].toString()));
      tm.setObjectId(object[ 1].toString());
      tm.setComment(object[ 2] == null ? null : object[ 2].toString());
      tms.add(tm);
    }
    return tms;
  }

  public List<TableauMarche> getTableauxMarcheCourse(final Long idCourse)
  {
    List<Long> idCourses = new ArrayList<Long>(1);
    idCourses.add(idCourse);
    return getTableauxMarcheCourses(idCourses);
  }

  public List<TableauMarche> getTableauxMarcheItineraire(final Long idItineraire)
  {
    return getHibernateTemplate().find("from TableauMarche as tt WHERE tt.id in (SELECT DISTINCT t.id FROM TableauMarche as t, LienTMCourse as l, Course as c "
            + "WHERE c.idItineraire=" + idItineraire + " AND l.idCourse=c.id AND t.id=l.idTableauMarche )");
  }

  public List<TableauMarche> getTableauxMarcheItineraires(final Collection<Long> idItineraires)
  {
    if (isCollectionVide(idItineraires))
    {
      return new ArrayList<TableauMarche>();
    }
    return getHibernateTemplate().find("from TableauMarche as tt WHERE tt.id in (SELECT DISTINCT t.id FROM TableauMarche as t, LienTMCourse as l, Course as c "
            + "WHERE c.idItineraire in (" + getSQLlist(idItineraires) + ") AND l.idCourse=c.id AND t.id=l.idTableauMarche )");
  }

  public List<TableauMarche> getTableauxMarcheCourses(final Collection<Long> idCourses)
  {
    if (isCollectionVide(idCourses))
    {
      return new ArrayList<TableauMarche>();
    }
    final List<LienTMCourse> liensTMCourse = getHibernateTemplate().find("FROM LienTMCourse as l WHERE l.idCourse in (" + getSQLlist(idCourses) + ")");
    final int totalLiens = liensTMCourse.size();
    List<TableauMarche> resultat = new ArrayList<TableauMarche>();
    if (totalLiens > 0)
    {
      final String ids = getTMSQLlist(liensTMCourse);
      resultat = getHibernateTemplate().find("FROM TableauMarche as t WHERE t.id in (" + ids.toString() + ")");
    }
    return resultat;
  }

  public List<Course> getCoursesTableauMarche(final Long idTableauMarche)
  {
    return getHibernateTemplate().find("FROM Course as c WHERE c.id in (SELECT l.idCourse FROM LienTMCourse as l WHERE l.idTableauMarche=" + idTableauMarche + ")");
  }

  public List<Correspondance> getCorrespondancesParGeoPosition(final Long idGeoPosition)
  {
    return getHibernateTemplate().find("FROM Correspondance as c WHERE c.idDepart=" + idGeoPosition + " OR c.idArrivee=" + idGeoPosition);
  }

  public List<PositionGeographique> getGeoPositions()
  {
    return getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(PositionGeographique.class).addOrder(Order.asc("name")));
  }

  public List<Correspondance> getCorrespondances()
  {
    return getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(Correspondance.class).addOrder(Order.asc("name")));
  }

  public List<PositionGeographique> getGeoPositionsParentes(final Long idGeoPositionParente)
  {
    Collection<Long> idsGeoPositions = new ArrayList<Long>();
    idsGeoPositions.add(idGeoPositionParente);
    List<PositionGeographique> parents = new ArrayList<PositionGeographique>();
    Set<Long> idDejaSelectionnes = new HashSet<Long>();
    while (idsGeoPositions.size() > 0)
    {
      List<PositionGeographique> geoPositions = getGeoPositions(idsGeoPositions, new Ordre("name", true));
      idDejaSelectionnes.addAll(idsGeoPositions);
      idsGeoPositions.clear();
      if (!geoPositions.isEmpty())
      {
        parents.addAll(geoPositions);
        Long idParent = geoPositions.get(0).getIdParent();
        if (idParent != null && !idDejaSelectionnes.contains(idParent))
        {
          idsGeoPositions.add(idParent);
        }
      }
    }
    return parents;
  }

  public void deplacerArrets(List<Long> arretsOrdreInitial, List<Long> arretsOrdreNouveau, List<Integer> nouvellesPositions)
  {
    if (isCollectionVide(arretsOrdreInitial))
    {
      assert isCollectionVide(arretsOrdreNouveau) : "la liste arrets initiaux est vide alors que celle des arrets deplaces l'est pas";
      assert nouvellesPositions == null || nouvellesPositions.size() == 0 : "la liste arrets initiaux est vide alors que celle de leurs nouvelles positions l'est pas";
      return;
    }
    assert nouvellesPositions != null : "les nouvelles positions d'arret ne sont pas définies";
    assert arretsOrdreNouveau != null : "le nouvel ordre des arrets n'est pas défini";
    assert arretsOrdreNouveau.size() == arretsOrdreInitial.size() : "total d'arrets avant deplacement (" + arretsOrdreInitial.size() + ") et apres deplacement (" + arretsOrdreNouveau.size() + ") differents";
    assert arretsOrdreNouveau.size() == nouvellesPositions.size() : "total d'arrets avant deplacement (" + arretsOrdreInitial.size() + ") et de positions (" + nouvellesPositions.size() + ") differents";
    Set<Long> idsPerdus = new HashSet<Long>(arretsOrdreInitial);
    idsPerdus.removeAll(arretsOrdreNouveau);
    assert idsPerdus.size() == 0 : "Les arrets initiaux (" + idsPerdus + " n'apparaissent plus parmi les arrets deplaces";
    Set<Integer> positionsDisparues = new HashSet<Integer>(nouvellesPositions);
    positionsDisparues.removeAll(getPositionsArretsItineraire(arretsOrdreInitial));
    assert positionsDisparues.size() == 0 : "Les positions initiales (" + positionsDisparues + " n'apparaissent plus parmi les positions des arrets deplaces";
    echangerPositions(arretsOrdreNouveau, nouvellesPositions);
    echangerHoraires(arretsOrdreInitial, arretsOrdreNouveau);
  }

  private Set<Integer> getPositionsArretsItineraire(Collection<Long> idsArretsItineraire)
  {
    Set<Integer> resultat = new HashSet<Integer>();
    if (isCollectionVide(idsArretsItineraire))
    {
      return resultat;
    }
    String sqlIds = getSQLlist(idsArretsItineraire);
    List<ArretItineraire> arrets = getHibernateTemplate().find("FROM ArretItineraire as a WHERE a.id in (" + sqlIds + ")");
    for (ArretItineraire arret : arrets)
    {
      resultat.add(arret.getPosition());
    }
    return resultat;
  }

  public void echangerPositions(List<Long> arretsItineraireOrdreNouveau, List<Integer> nouvellesPositions)
  {
    int total = arretsItineraireOrdreNouveau.size();
    if (total == 0)
    {
      return;
    }
    Session session = getSession();
    for (int i = 0; i < total; i++)
    {
      Long idArretInitial = arretsItineraireOrdreNouveau.get(i);
      Integer nouvellePosition = nouvellesPositions.get(i);
      String sqlEchangeHoraire = "UPDATE ArretItineraire a SET a.modifie=true, a.position=";
      sqlEchangeHoraire += nouvellePosition;
      sqlEchangeHoraire += " WHERE a.id=" + idArretInitial;
      sqlEchangeHoraire += " AND a.modifie=false";
      session.createQuery(sqlEchangeHoraire).executeUpdate();
    }
    String listId = getSQLlist(arretsItineraireOrdreNouveau);
    String sqlArret = "UPDATE ArretItineraire a SET a.modifie=false ";
    sqlArret += " WHERE a.id in (" + listId + ")";
    session.createQuery(sqlArret).executeUpdate();
  }

  public void echangerHoraires(List<Long> arretsItineraireOrdreInitial, List<Long> arretsItineraireOrdreNouveau)
  {
    int total = arretsItineraireOrdreInitial.size();
    if (total == 0)
    {
      return;
    }
    Session session = getSession();
    for (int i = 0; i < total; i++)
    {
      Long idArretInitial = arretsItineraireOrdreInitial.get(i);
      Long idArretRemplacant = arretsItineraireOrdreNouveau.get(i);
      String sqlEchangeHoraire = "UPDATE Horaire h SET h.modifie=true, h.idArret=";
      sqlEchangeHoraire += idArretRemplacant;
      sqlEchangeHoraire += " WHERE h.idArret=" + idArretInitial;
      sqlEchangeHoraire += " AND h.modifie=false";
      session.createQuery(sqlEchangeHoraire).executeUpdate();
    }
    String listId = getSQLlist(arretsItineraireOrdreInitial);
    String sqlHoraire = "UPDATE Horaire h SET h.modifie=false ";
    sqlHoraire += " WHERE h.idArret in (" + listId + ")";
    session.createQuery(sqlHoraire).executeUpdate();
  }

  public void associerTableauMarcheCourses(Long idTM, List<Long> idCourses)
  {
    List<LienTMCourse> liensTMCourse = null;
    List<Long> nvIdCourses = new ArrayList<Long>(idCourses);
    List<Long> idsLiensAsupprimer = new ArrayList<Long>();
    List<Long> idsCoursesExistantes = new ArrayList<Long>();
    liensTMCourse = getHibernateTemplate().find("FROM LienTMCourse as l WHERE l.idTableauMarche=?", idTM);
    for (LienTMCourse lien : liensTMCourse)
    {
      Long idAncienneCourse = lien.getIdCourse();
      idsCoursesExistantes.add(idAncienneCourse);
      if (!idCourses.contains(idAncienneCourse))
      {
        idsLiensAsupprimer.add(lien.getId());
      }
    }
    if (idsLiensAsupprimer.size() > 0)
    {
      String supprimesIds = getSQLlist(idsLiensAsupprimer);
      String sqlRequeteSuppression = "DELETE LienTMCourse l ";
      sqlRequeteSuppression += "WHERE l.id in (" + supprimesIds.toString() + ")";
      Session session = this.getSession();
      session.createQuery(sqlRequeteSuppression).executeUpdate();
    }
    nvIdCourses.removeAll(idsCoursesExistantes);
    for (Long idCourse : nvIdCourses)
    {
      LienTMCourse nouveauLien = new LienTMCourse();
      nouveauLien.setIdCourse(idCourse);
      nouveauLien.setIdTableauMarche(idTM);
      getHibernateTemplate().save(nouveauLien);
    }
  }

  public void associerCourseTableauxMarche(Long idCourse, List<Long> idTMs)
  {
    List<LienTMCourse> liensTMCourse = null;
    List<Long> nvIdTMs = new ArrayList<Long>(idTMs);
    List<Long> idsLiensAsupprimer = new ArrayList<Long>();
    List<Long> idsTMExistants = new ArrayList<Long>();
    liensTMCourse = getHibernateTemplate().find("FROM LienTMCourse as l WHERE l.idCourse=?", idCourse);
    for (LienTMCourse lien : liensTMCourse)
    {
      Long idAncienTM = lien.getIdTableauMarche();
      idsTMExistants.add(idAncienTM);
      if (!idTMs.contains(idAncienTM))
      {
        idsLiensAsupprimer.add(lien.getId());
      }
    }
    if (idsLiensAsupprimer.size() > 0)
    {
      String supprimesIds = getSQLlist(idsLiensAsupprimer);
      String sqlRequeteSuppression = "DELETE LienTMCourse l ";
      sqlRequeteSuppression += "WHERE l.id in (" + supprimesIds.toString() + ")";
      Session session = this.getSession();
      session.createQuery(sqlRequeteSuppression).executeUpdate();
    }
    nvIdTMs.removeAll(idsTMExistants);
    for (Long idTM : nvIdTMs)
    {
      LienTMCourse nouveauLien = new LienTMCourse();
      nouveauLien.setIdCourse(idCourse);
      nouveauLien.setIdTableauMarche(idTM);
      getHibernateTemplate().save(nouveauLien);
    }
  }

  public void affecterMission(Long idMission, Collection<Long> idCourses)
  {
    if (isCollectionVide(idCourses))
    {
      return;
    }
    String sqlRequete = "UPDATE Course SET idMission=" + idMission;
    sqlRequete += " WHERE id in (" + getSQLlist(idCourses) + ")";
    this.getSession().createQuery(sqlRequete).executeUpdate();
  }

  public void fusionnerMissions(Long idMission, Long idMissionPrincipale)
  {
    String sqlRequete = "UPDATE Course SET idMission=" + idMissionPrincipale;
    sqlRequete += " WHERE idMission=" + idMission;
    String sqlSuppression = "DELETE Mission WHERE id=" + idMission;
    this.getSession().createQuery(sqlRequete).executeUpdate();
    this.getSession().createQuery(sqlSuppression).executeUpdate();
  }

  public void associerItineraire(Long idRoute1, Long idRoute2)
  {
    dissocierItineraire(idRoute1);
    dissocierItineraire(idRoute2);
    _associerItineraire(idRoute1, idRoute2);
    _associerItineraire(idRoute2, idRoute1);
    Itineraire itineraire1 = (Itineraire) getHibernateTemplate().find("FROM Itineraire WHERE id=?", idRoute1).get(0);
    _orienterItineraire(idRoute2, (SENS_ALLER.equals(itineraire1.getWayBack())) ? SENS_RETOUR : SENS_ALLER);
  }

  private void _associerItineraire(Long idRoute1, Long idRoute2)
  {
    String sqlRequete = "UPDATE Itineraire r ";
    sqlRequete += " SET r.idRetour=" + idRoute2;
    sqlRequete += " WHERE r.id=" + idRoute1;
    this.getSession().createQuery(sqlRequete).executeUpdate();
  }

  private void _orienterItineraire(Long idRoute, String sens)
  {
    String sqlRequete = "UPDATE Itineraire r ";
    sqlRequete += " SET r.wayBack='" + sens + "'";
    sqlRequete += " WHERE r.id=" + idRoute;
    this.getSession().createQuery(sqlRequete).executeUpdate();
  }

  public void dissocierItineraire(Long idRoute1)
  {
    String sqlRequete = "UPDATE Itineraire r ";
    sqlRequete += " SET r.idRetour=null WHERE r.idRetour=" + idRoute1 + " OR r.id=" + idRoute1;
    this.getSession().createQuery(sqlRequete).executeUpdate();
  }

  public void dissocierITLGeoPosition(Collection<Long> idGeoPositions)
  {
    String sqlRequete = "DELETE FROM " + getDatabaseSchema() + ".routingConstraint_stoparea ";
    sqlRequete += "WHERE stopareaId in (" + getSQLlist(idGeoPositions) + ")";
    getSession().createSQLQuery(sqlRequete).executeUpdate();
  }

  public void associerGeoPositions(Long idContenant, Long idContenue)
  {
    String sqlRequete = "UPDATE PositionGeographique p ";
    sqlRequete += " SET p.idParent=" + idContenant;
    sqlRequete += " WHERE p.id=" + idContenue;
    this.getSession().createQuery(sqlRequete).executeUpdate();
  }

  public void dissocierGeoPositionParente(Long idContenue)
  {
    String sqlRequete = "UPDATE PositionGeographique p ";
    sqlRequete += " SET p.idParent=null WHERE p.id=" + idContenue;
    this.getSession().createQuery(sqlRequete).executeUpdate();
  }

  public void dissocierGeoPositionsContenues(Long idContenant)
  {
    String sqlRequete = "UPDATE PositionGeographique p ";
    sqlRequete += " SET p.idParent=null WHERE p.idParent=" + idContenant;
    this.getSession().createQuery(sqlRequete).executeUpdate();
  }

  public void dissocierGeoPosition(Long idGeoPosition)
  {
    dissocierGeoPositionParente(idGeoPosition);
    dissocierGeoPositionsContenues(idGeoPosition);
  }

  private String getSQLlist(Collection<Long> ids)
  {
    StringBuffer sqlBuff = new StringBuffer();
    int total = ids.size();
    int totalLu = 0;
    for (Long id : ids)
    {
      sqlBuff.append(id);
      totalLu++;
      if (totalLu < total)
      {
        sqlBuff.append(',');
      }
    }
    return sqlBuff.toString();
  }

  private boolean isCollectionNonVide(final Collection<Long> ids)
  {
    return !isCollectionVide(ids);
  }

  private boolean isCollectionVide(final Collection<Long> ids)
  {
    return ids == null || ids.isEmpty();
  }

  private String getTMSQLlist(List<LienTMCourse> ids)
  {
    List<Long> lesIdTM = new ArrayList<Long>(ids.size());
    for (LienTMCourse lien : ids)
    {
      lesIdTM.add(lien.getIdTableauMarche());
    }
    return getSQLlist(lesIdTM);
  }

  @SuppressWarnings("unused")
  private String getCourseSQLlist(List<LienTMCourse> ids)
  {
    List<Long> lesIdTM = new ArrayList<Long>(ids.size());
    for (LienTMCourse lien : ids)
    {
      lesIdTM.add(lien.getIdCourse());
    }
    return getSQLlist(lesIdTM);
  }

  public void substituerArretPhysiqueDansArretsItineraireAssocies(final Long idAncienArretPhysique, final Long idNouveauArretPhysique)
  {
    String sqlRequete = "UPDATE ArretItineraire SET idPhysique = " + idNouveauArretPhysique;
    sqlRequete += " WHERE idPhysique = " + idAncienArretPhysique;
    this.getSession().createQuery(sqlRequete).executeUpdate();
  }

  public void substituerArretPhysiqueDansITLsAssocies(final Long idAncienArretPhysique, final Long idNouveauArretPhysique)
  {
    String sqlRequete = "UPDATE routingConstraint_stoparea SET stopareaId = " + idNouveauArretPhysique;
    sqlRequete += " WHERE stopareaId = " + idAncienArretPhysique;
    this.getSession().createSQLQuery(sqlRequete).executeUpdate();
  }

  public void supprimerGeoPosition(final Long idPosition)
  {
    final PositionGeographique position = (PositionGeographique) getHibernateTemplate().get(PositionGeographique.class, idPosition);
    getHibernateTemplate().delete(position);
  }

  public List<PositionGeographique> getArretsFiltres(final String nomArret, final String code, final Long idReseau, final List<ChouetteAreaType> areaTypes)
  {
    StringBuffer query = new StringBuffer();
    query.append("SELECT DISTINCT positionGeographique ");
    query.append("FROM PositionGeographique positionGeographique ");
    String nextQueryToken = "";
    if (idReseau != null)
    {
      query.append(", ArretItineraire arretItineraire, Itineraire itineraire, Ligne ligne ");
      query.append("WHERE positionGeographique.id = arretItineraire.idPhysique ");
      query.append("AND arretItineraire.idItineraire = itineraire.id ");
      query.append("AND itineraire.idLigne = ligne.id ");
      query.append("AND ligne.idReseau = " + idReseau + " ");
      if (nomArret != null || code != null || !areaTypes.isEmpty())
      {
        nextQueryToken = "AND ";
      }
    } else if (nomArret != null || code != null || !areaTypes.isEmpty())
    {
      nextQueryToken = "WHERE ";
    }
    if (nomArret != null)
    {
      query.append(nextQueryToken);
      query.append("UPPER(positionGeographique.name) LIKE '%" + nomArret.toUpperCase() + "%' ");
      nextQueryToken = "AND ";
    }
    if (code != null)
    {
      query.append(nextQueryToken);
      query.append("positionGeographique.countryCode = '" + code + "' ");
      nextQueryToken = "AND ";
    }
    if (!areaTypes.isEmpty())
    {
      query.append(nextQueryToken);
      query.append("(");
      for (Iterator i = areaTypes.iterator(); i.hasNext();)
      {
        query.append("positionGeographique.areaType = '" + ((ChouetteAreaType) i.next()).toString() + "' ");
        query.append((i.hasNext()) ? "OR " : "");
      }
      query.append(")");
    }
    logger.debug("query = " + query);
    return getHibernateTemplate().find(query.toString());
  }

  public List<TableauMarche> getCalendriersFiltres(final Date dateDebutInterval, final Date dateFinInterval, final String commentaire, final Long idReseau)
  {
    List calendriers = null;
    StringBuffer query = new StringBuffer();
    String nextQueryToken = "";
    query.append("SELECT DISTINCT tableauMarche ");
    query.append("FROM TableauMarche tableauMarche ");
    query.append(((idReseau != null)) ? ", LienTMCourse lienTMCourse, Course course, Itineraire itineraire, Ligne ligne " : "");
    query.append("LEFT JOIN tableauMarche.periodes periode ");
    query.append("LEFT JOIN tableauMarche.dates date ");
    nextQueryToken = "WHERE ";
    if (idReseau != null)
    {
      query.append(nextQueryToken);
      query.append("tableauMarche.id = lienTMCourse.idTableauMarche AND ");
      query.append("course.id = lienTMCourse.idCourse AND ");
      query.append("course.idItineraire = itineraire.id AND ");
      query.append("itineraire.idLigne = ligne.id AND ");
      query.append("ligne.idReseau = " + idReseau);
      nextQueryToken = "AND ";
    }
    if (commentaire != null)
    {
      query.append(nextQueryToken);
      query.append("UPPER(tableauMarche.comment) LIKE '%" + commentaire.toUpperCase() + "%' ");
      nextQueryToken = "AND ";
    }
    int intervalIntDayTypes = -1;
    if (dateDebutInterval != null)
    {
      intervalIntDayTypes = TableauMarcheUtils.getIntervalIntDayType(dateDebutInterval, dateFinInterval);
      System.out.println("GOT INT DAY TYPE SET TO / " + intervalIntDayTypes);
      query.append(nextQueryToken);
      query.append("(");
      query.append("(");
      query.append("(");
      query.append(":dateDebutInterval >= periode.debut ");
      query.append("AND ");
      query.append(":dateDebutInterval <= periode.fin ");
      query.append(") ");
      if (dateFinInterval != null)
      {
        query.append("OR ");
        query.append("(");
        query.append("periode.debut >= :dateDebutInterval ");
        query.append("AND ");
        query.append("periode.debut <= :dateFinInterval ");
        query.append(") ");
      }
      query.append(") ");
      query.append("AND ");
      query.append("(");
      query.append("bitwise_and (tableauMarche.intDayTypes, :intervalIntDayTypes) = :intervalIntDayTypes ");
      query.append("OR ");
      query.append("(");
      query.append("bitwise_and (:intervalIntDayTypes, tableauMarche.intDayTypes) = tableauMarche.intDayTypes ");
      query.append("AND ");
      query.append("tableauMarche.intDayTypes != 0 ");
      query.append(")");
      query.append(") ");
      query.append(") ");
      query.append("OR ");
      query.append("(");
      query.append("date >= :dateDebutInterval ");
      if (dateFinInterval != null)
      {
        query.append("AND ");
        query.append("date <= :dateFinInterval ");
      }
      query.append(") ");
    }
    if (dateDebutInterval != null)
    {
      ArrayList<String> params = new ArrayList<String>();
      ArrayList<Object> values = new ArrayList<Object>();
      params.add("dateDebutInterval");
      values.add(dateDebutInterval);
      if (dateFinInterval != null)
      {
        params.add("dateFinInterval");
        values.add(dateFinInterval);
      }
      params.add("intervalIntDayTypes");
      values.add(intervalIntDayTypes);
      logger.debug("query = " + query);
      int idx = 0;
      String[] paramsTab = new String[params.size()];
      for (Object param : params.toArray())
      {
        paramsTab[idx] = (String) param;
        idx++;
      }
      calendriers = getHibernateTemplate().findByNamedParam(query.toString(), paramsTab, values.toArray());
    } else
    {
      logger.debug("QUERY = " + query);
      calendriers = getHibernateTemplate().find(query.toString());
    }
    return calendriers;
  }

  public List<TableauMarche> getCalendriersFiltresGOOD(final Date dateDebutInterval, final Date dateFinInterval, final String commentaire, final Long idReseau)
  {
    List calendriers = null;
    StringBuffer query = new StringBuffer();
    String nextQueryToken = "";
    query.append("SELECT DISTINCT tableauMarche ");
    query.append("FROM TableauMarche tableauMarche ");
    query.append(((idReseau != null)) ? ", LienTMCourse lienTMCourse, Course course, Itineraire itineraire, Ligne ligne " : "");
    query.append("LEFT JOIN tableauMarche.periodes periode ");
    query.append("LEFT JOIN tableauMarche.dates date ");
    nextQueryToken = "WHERE ";
    if (idReseau != null)
    {
      query.append(nextQueryToken);
      query.append("tableauMarche.id = lienTMCourse.idTableauMarche AND ");
      query.append("course.id = lienTMCourse.idCourse AND ");
      query.append("course.idItineraire = itineraire.id AND ");
      query.append("itineraire.idLigne = ligne.id AND ");
      query.append("ligne.idReseau = " + idReseau);
      nextQueryToken = "AND ";
    }
    if (commentaire != null)
    {
      query.append(nextQueryToken);
      query.append("UPPER(tableauMarche.comment) LIKE '%" + commentaire.toUpperCase() + "%' ");
      nextQueryToken = "AND ";
    }
    int intervalIntDayTypes = -1;
    if (dateDebutInterval != null && dateFinInterval != null)
    {
      intervalIntDayTypes = TableauMarcheUtils.getIntervalIntDayType(dateDebutInterval, dateFinInterval);
      query.append(nextQueryToken);
      query.append("(");
      query.append("(");
      query.append("(");
      query.append(":dateDebutInterval >= periode.debut ");
      query.append("AND ");
      query.append(":dateDebutInterval <= periode.fin ");
      query.append(") ");
      query.append("OR ");
      query.append("(");
      query.append("periode.debut >= :dateDebutInterval ");
      query.append("AND ");
      query.append("periode.debut <= :dateFinInterval ");
      query.append(") ");
      query.append(") ");
      query.append("AND ");
      query.append("(");
      query.append("bitwise_and (tableauMarche.intDayTypes, :intervalIntDayTypes) = :intervalIntDayTypes ");
      query.append("OR ");
      query.append("(");
      query.append("bitwise_and (:intervalIntDayTypes, tableauMarche.intDayTypes) = tableauMarche.intDayTypes ");
      query.append("AND ");
      query.append("tableauMarche.intDayTypes != 0 ");
      query.append(")");
      query.append(") ");
      query.append(") ");
      query.append("OR ");
      query.append("(");
      query.append("date >= :dateDebutInterval ");
      query.append("AND ");
      query.append("date <= :dateFinInterval ");
      query.append(") ");
    }
    if (dateDebutInterval != null && dateFinInterval != null)
    {
      ArrayList<String> params = new ArrayList<String>();
      ArrayList<Object> values = new ArrayList<Object>();
      params.add("dateDebutInterval");
      values.add(dateDebutInterval);
      params.add("dateFinInterval");
      values.add(dateFinInterval);
      params.add("intervalIntDayTypes");
      values.add(intervalIntDayTypes);
      logger.debug("query = " + query);
      int idx = 0;
      String[] paramsTab = new String[params.size()];
      for (Object param : params.toArray())
      {
        paramsTab[idx] = (String) param;
        idx++;
      }
      calendriers = getHibernateTemplate().findByNamedParam(query.toString(), paramsTab, values.toArray());
    } else
    {
      calendriers = getHibernateTemplate().find(query.toString());
    }
    return calendriers;
  }

  public List<Object> select(final IClause clause)
  {
    DetachedCriteria criteria = DetachedCriteria.forClass(ArretItineraire.class);
    criteria.createCriteria("positionGeographique");
    return getHibernateTemplate().findByCriteria(criteria);
  }

  public Map<Long, String> getCommentParTMId(final Long idItineraire)
  {
    long start = System.currentTimeMillis();
    StringBuffer queryString = new StringBuffer();
    queryString.append("SELECT t.id, t.comment ");
    queryString.append("FROM Course c, LienTMCourse tv, TableauMarche t ");
    queryString.append("WHERE c.idItineraire =  ");
    queryString.append(idItineraire);
    queryString.append(" AND tv.idCourse = c.id ");
    queryString.append(" AND t.id = tv.idTableauMarche ");

    List<Object[]> rows = getHibernateTemplate().find(queryString.toString());

    Map<Long, String> result = new HashMap<Long, String>();
    for (Object[] row : rows)
    {
      Long tmId = (Long) row[0];
      String comment = (String) row[1];

      result.put(tmId, comment);
    }

    logger.info("BENCH getCommentParTMId(idItineraire=" + idItineraire
            + ") elapse " + (System.currentTimeMillis() - start));
    return result;
  }

  public Map<Long, List<Long>> getTimeTablesIdByRouteId(final Long routeId)
  {
    long start = System.currentTimeMillis();
    StringBuffer queryString = new StringBuffer();
    queryString.append("SELECT new fr.certu.chouette.dao.hibernate.Couple(c.id, tv.idTableauMarche) ");
    queryString.append("FROM Course c, LienTMCourse tv ");
    queryString.append("WHERE c.idItineraire =  ");
    queryString.append(routeId);
    queryString.append(" AND tv.idCourse = c.id ");

    List<Couple> couples = getHibernateTemplate().find(queryString.toString());

    Map<Long, List<Long>> result = new HashMap<Long, List<Long>>();
    for (Couple couple : couples)
    {
      Long courseId = couple.premier;
      Long tmId = couple.deuxieme;

      List<Long> tms = result.get(courseId);
      if (tms == null)
      {
        tms = new ArrayList<Long>();
        result.put(courseId, tms);
      }

      tms.add(tmId);
    }

    logger.info("BENCH getTimeTablesIdByRouteId(routeId=" + routeId
            + ") elapse " + (System.currentTimeMillis() - start));
    return result;
  }

  public List<Long> getTimeTablesIdByVehicleJourneyId(final Long vehicleJourneyId)
  {
    long start = System.currentTimeMillis();
    StringBuffer queryString = new StringBuffer();
    queryString.append("SELECT tv.idTableauMarche ");
    queryString.append("FROM Course c, LienTMCourse tv ");
    queryString.append("WHERE tv.idCourse = ");
    queryString.append(vehicleJourneyId);

    List<Long> result = getHibernateTemplate().find(queryString.toString());

    logger.info("BENCH getTimeTablesIdByVehicleJourneyId(vehicleJourneyId=" + vehicleJourneyId
            + ") elapse " + (System.currentTimeMillis() - start));
    return result;
  }

  public List<Course> getCoursesFiltrees(final Long idItineraire, final Long idTableauMarche, final Date seuilDateDepartCourses)
  {
    logger.error("idItineraire : " + idItineraire + ", idTableauMarche : " +idTableauMarche + ", seuilDateDepartCourses : " +  seuilDateDepartCourses);
    long start = System.currentTimeMillis();

    StringBuffer sql = new StringBuffer("SELECT {c.*}, {h.*} ");
    sql.append(" FROM ");
    if (idTableauMarche != null)
    {
      sql.append(" SPECIFIC_SCHEMA.timetablevehiclejourney tv ,");
    }
    sql.append(" SPECIFIC_SCHEMA.vehiclejourney c");
    sql.append(" LEFT JOIN SPECIFIC_SCHEMA.vehiclejourneyatstop h ON h.vehiclejourneyid=c.id AND h.isdeparture = true ");

    sql.append(" WHERE c.routeid=");
    sql.append(idItineraire);
    if (seuilDateDepartCourses != null)
    {
      sql.append(" AND h.departureTime >= :seuilDateDepartCourses ");
    }
    if (idTableauMarche != null)
    {
      sql.append(" AND tv.timetableid = :idTableauMarche");
      sql.append(" AND tv.vehiclejourneyid = c.id");
    }
    sql.append(" ORDER BY h.departureTime");

    Query query = getSession().createSQLQuery(sql.toString().replaceAll("SPECIFIC_SCHEMA", getDatabaseSchema().trim())).addEntity("c", Course.class).addEntity("h", Horaire.class);

    if (seuilDateDepartCourses != null)
    {
      query.setTime("seuilDateDepartCourses", seuilDateDepartCourses);
    }
    if (idTableauMarche != null)
    {
      query.setLong("idTableauMarche", idTableauMarche);
    }

    List<Object[]> rowCH = query.list();
    List<Course> courses = new ArrayList<Course>(rowCH.size());
    for (Object[] row : rowCH)
    {
      courses.add((Course) row[0]);
    }

    logger.info("BENCH getCoursesFiltrees(idItineraire=" + idItineraire
            + ", idTableauMarche=" + idTableauMarche
            + ", seuilDateDepartCourses" + seuilDateDepartCourses + ") elapse "
            + (System.currentTimeMillis() - start));
    return courses;
  }

  public void setDatabaseSchema(String databaseSchema)
  {
    this.databaseSchema = databaseSchema;
  }

  public String getDatabaseSchema()
  {
    return databaseSchema;
  }
}
