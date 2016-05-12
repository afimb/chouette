package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.GroupOfLine;

@Stateless
public class GroupOfLineDAOImpl extends GenericDAOImpl<GroupOfLine> implements GroupOfLineDAO{

	public GroupOfLineDAOImpl() {
		super(GroupOfLine.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
