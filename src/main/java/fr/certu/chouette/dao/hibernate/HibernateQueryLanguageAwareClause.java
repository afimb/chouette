package fr.certu.chouette.dao.hibernate;

import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.dao.PersistenceQueryLanguageAwareClause;

public class HibernateQueryLanguageAwareClause extends PersistenceQueryLanguageAwareClause {

	public HibernateQueryLanguageAwareClause(IClause clause) {
		super(clause);
		this.clauzeTranslator = new ToHibernateClauseTranslator();
	}
	
}
