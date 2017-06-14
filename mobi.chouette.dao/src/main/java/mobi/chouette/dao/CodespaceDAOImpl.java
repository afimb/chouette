package mobi.chouette.dao;

import mobi.chouette.model.Codespace;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless (name="CodespaceDAO")
public class CodespaceDAOImpl extends GenericDAOImpl<Codespace> implements CodespaceDAO {

    public CodespaceDAOImpl() {
        super(Codespace.class);
    }

    @PersistenceContext(unitName = "referential")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
