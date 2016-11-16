package mobi.chouette.dao.iev;

import java.sql.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.iev.Stat;

@Stateless
@Log4j
public class StatDAO extends GenericDAOImpl<Stat> {
	
	public StatDAO() {
		super(Stat.class);
	}

	@PersistenceContext(unitName = "iev")
	EntityManager em;
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	public void addStatToDatabase(Date date, String referential, String action, String type) {
		// Insertion des statistiques d'import, export, validation dans la table stats
		Stat chouetteStat = new Stat();
		
		chouetteStat.setReferential(referential);
		chouetteStat.setDate(date);
		chouetteStat.setAction(action);
		
		if(type != null)	
			chouetteStat.setFormat(type);
		
		// log.info("stat object -> action : "+ chouetteStat.getAction() + " type : " + chouetteStat.getFormat());
		// Création de la nouvelle statistique en base de données
	    // log.info("chouette stat not null");
			save(chouetteStat);
		
	}
	
	public List<Stat> getCurrentYearStats() {
		// log.info("BEGIN GET CURRENT YEAR STAT");
		TypedQuery<Stat> query = em.createQuery("SELECT s FROM Stat s ORDER BY s.referential, s.date, s.format, s.action", Stat.class);
		// log.info("GET CURRENT YEAR STAT 1");
		List<Stat> lstStat = (List<Stat>)query.getResultList();
		// log.info("BEGIN GET CURRENT YEAR STAT 2");
//		if(lstStat != null)
//			log.info("Found " + lstStat.size() + "stats to send to IHM");
//		else
//			log.info("Error stat list is null");
			
//		log.info("END GET CURRENT YEAR STAT");		
		return lstStat;
	}
	
	public void removeObsoleteStatFromDatabase(Date date) {
		Query query = em.createNativeQuery("DELETE FROM Stats s WHERE  s.date < (date '" + date + "' - interval '1 year')");
		query.executeUpdate();
	}
	
	public void save(Stat stat) {
		em.persist(stat);
	}

	@Override
	public void clear() {
		em.clear();
	}

	@Override
	public Stat findByChouetteId(String codeSpace, String objectId) {
		// TODO Auto-generated method stub
		return null;
	}
}

