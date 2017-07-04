package mobi.chouette.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

@Stateless
public class StopAreaDAOImpl extends GenericDAOImpl<StopArea> implements StopAreaDAO {

    public StopAreaDAOImpl() {
        super(StopArea.class);
    }

    @PersistenceContext(unitName = "public")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    protected String getTableName() {
        return "public." + super.getTableName();
    }


    @Override
    public List<String> getBoardingPositionObjectIds() {
        return em.createQuery("select s.objectId from StopArea s where s.areaType = :areaType").setParameter("areaType", ChouetteAreaEnum.BoardingPosition).getResultList();
    }

}
