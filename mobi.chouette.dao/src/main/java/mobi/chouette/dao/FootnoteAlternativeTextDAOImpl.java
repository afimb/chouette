package mobi.chouette.dao;

import mobi.chouette.model.FootNoteAlternativeText;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FootnoteAlternativeTextDAOImpl extends GenericDAOImpl<FootNoteAlternativeText> implements FootnoteAlternativeTextDAO {

    public FootnoteAlternativeTextDAOImpl() {
        super(FootNoteAlternativeText.class);
    }

    @PersistenceContext(unitName = "referential")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
