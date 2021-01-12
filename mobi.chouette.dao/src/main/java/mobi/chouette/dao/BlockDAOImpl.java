package mobi.chouette.dao;

import mobi.chouette.model.Block;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class BlockDAOImpl extends GenericDAOImpl<Block> implements BlockDAO {

    public BlockDAOImpl() {
        super(Block.class);
    }

    @PersistenceContext(unitName = "referential")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
