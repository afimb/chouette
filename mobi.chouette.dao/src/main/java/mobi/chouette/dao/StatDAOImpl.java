package mobi.chouette.dao;

import java.sql.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.log4j.Log4j;
import mobi.chouette.model.Stat;
import mobi.chouette.model.type.StatActionEnum;
import mobi.chouette.model.type.StatFormatEnum;

@Stateless
@Log4j
public class StatDAOImpl extends GenericDAOImpl<Stat> implements StatDAO{
	
	private static final String SQL = "SELECT * FROM STATS ORDER BY DATE, FORMAT, ACTION";
	

	
	public StatDAOImpl() {
		super(Stat.class);
	}

	@PersistenceContext(unitName = "public")
	EntityManager em;
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	@Override
	public void addStatToDatabase(Date date, String action, String type) {
		// Insertion des statistiques d'import, export, validation dans la table stats
		Stat chouetteStat = new Stat();
		
		chouetteStat.setDate(date);
		chouetteStat.setAction(StatActionEnum.valueOf(action));
		
		if(type != null)	
			chouetteStat.setFormat(StatFormatEnum.valueOf(type));
		
		log.info("stat object -> action : "+ chouetteStat.getAction().toString() + " type : " + chouetteStat.getFormat().toString());
		// Création de la nouvelle statistique en base de données
		if(chouetteStat != null) {
			log.info("chouette stat not null");
			save(chouetteStat);
		}
		else
			log.info("chouette stat null");
	}
	
	
	@Override
	public List<Stat> getCurrentYearStats() {
		//Query query = em.createNativeQuery(SQL);
		
		//List<Stat> lstStat = query.getResultList();
		
		return findAll();
	}
	
	@Override
	public void removeObsoleteStatFromDatabase(Date date) {
		Query query = em.createNativeQuery("DELETE FROM STATS WHERE  date < (date '" + date + "' - interval '1 year')");
		query.executeUpdate();
	}
	
	public void save(Stat stat) {
		em.persist(stat);
	}

	@Override
	public void clear() {
		em.clear();
	}
}
