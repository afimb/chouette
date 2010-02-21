package fr.certu.chouette.dao;

import fr.certu.chouette.critere.IClause;

public interface IClauseTranslator {
	public Object translate(IClause clause);
}
