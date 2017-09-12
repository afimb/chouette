package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.Footnote;

@Stateless
public class FootnoteDAOImpl extends GenericDAOImpl<Footnote> implements FootnoteDAO{

	public FootnoteDAOImpl() {
		super(Footnote.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
