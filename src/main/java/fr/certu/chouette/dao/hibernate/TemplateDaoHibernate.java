package fr.certu.chouette.dao.hibernate;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.Ordre;
import fr.certu.chouette.dao.IClauseTranslator;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.dao.PersistenceQueryLanguageAwareClause;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.BaseObjet;
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
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

public class TemplateDaoHibernate<T extends BaseObjet> extends HibernateDaoSupport implements ITemplateDao<BaseObjet>
{
	private static final Logger logger = Logger.getLogger(TemplateDaoHibernate.class);
	
	private Class<T> type;

	public TemplateDaoHibernate(Class<T> type) {
		this.type = type;
	}

   public static TemplateDaoHibernate<Reseau> creerReseauDao()
   {
	   return new TemplateDaoHibernate<Reseau>( Reseau.class);
   }
   public static TemplateDaoHibernate<Ligne> creerLigneDao()
   {
	   return new TemplateDaoHibernate<Ligne>( Ligne.class);
   }
   public static TemplateDaoHibernate<Transporteur> creerTransporteurDao()
   {
	   return new TemplateDaoHibernate<Transporteur>( Transporteur.class);
   }
   public static TemplateDaoHibernate<Itineraire> creerItineraireDao()
   {
	   return new TemplateDaoHibernate<Itineraire>( Itineraire.class);
   }
   public static TemplateDaoHibernate<ArretItineraire> creerArretItineraireDao()
   {
	   return new TemplateDaoHibernate<ArretItineraire>( ArretItineraire.class);
   }
   public static TemplateDaoHibernate<PositionGeographique> creerPositionGeographiqueDao()
   {
	   return new TemplateDaoHibernate<PositionGeographique>( PositionGeographique.class);
   }

   public static TemplateDaoHibernate<TableauMarche> creerTableauMarcheDao()
   {
	   return new TemplateDaoHibernate<TableauMarche>( TableauMarche.class);
   }
   public static TemplateDaoHibernate<Course> creerCourseDao()
   {
	   return new TemplateDaoHibernate<Course>( Course.class);
   }
   public static TemplateDaoHibernate<Correspondance> creerCorrespondanceDao()
   {
	   return new TemplateDaoHibernate<Correspondance>( Correspondance.class);
   }
   public static TemplateDaoHibernate<Horaire> creerHoraireDao()
   {
	   return new TemplateDaoHibernate<Horaire>( Horaire.class);
   }
   public static TemplateDaoHibernate<LienTMCourse> creerLienTMCourseDao()
   {
	   return new TemplateDaoHibernate<LienTMCourse>( LienTMCourse.class);
   }
   public static TemplateDaoHibernate<InterdictionTraficLocal> creerITLDao()
   {
	   return new TemplateDaoHibernate<InterdictionTraficLocal>( InterdictionTraficLocal.class);
   }
   public static TemplateDaoHibernate<Mission> creerMissionDao()
   {
	   return new TemplateDaoHibernate<Mission>( Mission.class);
   }

	public BaseObjet get(Long id)
	{
      T object = ( T)getHibernateTemplate().get( type, id);
      if ( object==null)
      {
         throw new ObjectRetrievalFailureException( type, id);
      }
      return object;
	}
	
	public List<BaseObjet> select(final IClause clause) {
		DetachedCriteria criteria = DetachedCriteria.forClass(type);
		PersistenceQueryLanguageAwareClause queryLanguageAwareClauze = new HibernateQueryLanguageAwareClause(clause);
		criteria.add((Criterion)queryLanguageAwareClauze.translate());
		return getHibernateTemplate().findByCriteria(criteria);
	}
	
	public List<BaseObjet> select(final IClause clause, final Collection <Ordre> ordres) {
		DetachedCriteria criteria = DetachedCriteria.forClass(type);
		if (clause != null) {
			PersistenceQueryLanguageAwareClause queryLanguageAwareClauze = new HibernateQueryLanguageAwareClause(clause);
			criteria.add((Criterion)queryLanguageAwareClauze.translate());
		}
		addHibernateOrdres(criteria, ordres);
		return getHibernateTemplate().findByCriteria( criteria);
	}
	
	private void addHibernateOrdres( final DetachedCriteria criteria, final Collection<Ordre> ordres) {
		if ( ordres!=null)
		{
			for (Ordre ordre : ordres) {
				if ( ordre.isCroissant())
					criteria.addOrder( Order.asc( ordre.getPropriete()));
				else
					criteria.addOrder( Order.desc( ordre.getPropriete()));
			}
		}
	}

	/*public List<BaseObjet> oldSelect( final OLDClause clause)
	{
		DetachedCriteria criteria = DetachedCriteria.forClass( type);
		ClauseAdapter clauseAdapter = new ClauseAdapter();
		criteria.add( clauseAdapter.adapt( clause));
		return getHibernateTemplate().findByCriteria( criteria);
	}*/
	
	public T getByObjectId( final String objectId)
	{
		if ( objectId==null || objectId.isEmpty()) return null;
		
		final List<T> list = getHibernateTemplate().find( 
				"FROM "+type.getName()+" as t WHERE t.objectId = '" + objectId + "'");		
		final int total = list.size();
		
		if ( total==0)
		{
			throw new ObjectRetrievalFailureException( type, objectId);
		}
		else if ( total>1)
		{
			throw new ServiceException( CodeIncident.BASE_NON_INTEGRE, CodeDetailIncident.TIMETABLE_COUNT,total ,objectId);
		}
		
		return list.get( 0);
	}	

	public List<BaseObjet> getAll() {
      return getHibernateTemplate().find( "from "+type.getName());
	}

	public void remove(Long id)
	{
      getHibernateTemplate().delete( get( id));
	}

	public void save(BaseObjet object)
	{
      getHibernateTemplate().save( object);
      getHibernateTemplate().flush();
	}

	public void update(BaseObjet object)
	{
		
		try
		{
			getHibernateTemplate().saveOrUpdate( object);
		}
		catch(HibernateSystemException hse)
		{
			if ( hse.getCause()!=null && 
				 hse.getCause() instanceof NonUniqueObjectException)
				getHibernateTemplate().merge( object);
		}	
	}
}
