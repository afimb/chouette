package fr.certu.chouette.dao;

import fr.certu.chouette.critere.IClause;

public abstract class PersistenceQueryLanguageAwareClause {

	protected IClause clause = null;
	protected IClauseTranslator clauzeTranslator = null;
	
	private PersistenceQueryLanguageAwareClause() {};
	
	public PersistenceQueryLanguageAwareClause(IClause clause) {
		this.clause = clause;
	}
	
	public Object translate () {
		return clauzeTranslator.translate(clause);
	}
}
