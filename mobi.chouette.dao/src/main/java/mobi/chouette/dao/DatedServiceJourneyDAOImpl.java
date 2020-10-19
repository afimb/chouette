package mobi.chouette.dao;

import mobi.chouette.model.DatedServiceJourney;
import mobi.chouette.model.FootNoteAlternativeText;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class DatedServiceJourneyDAOImpl extends GenericDAOImpl<DatedServiceJourney> implements DatedServiceJourneyDAO {

    public DatedServiceJourneyDAOImpl() {
        super(DatedServiceJourney.class);
    }

    @PersistenceContext(unitName = "referential")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
