package fr.certu.chouette.critere;


public class OrClause extends LogicClause {
	
	public OrClause () {}
	
	public OrClause (IClause ... clauses) {

		add (clauses);
	}
	
	public OrClause add (IClause ... clauses) {
		
		for (IClause clause : clauses) {
			add(clause);
		}
		
		return this;
	}
	
	public OrClause add (IClause clause) {
		
		if (clause == null) return this;
		
		if (rightSubClause == null) {
			this.rightSubClause = clause;
			return this;
		}
		
		if (leftSubClause == null) {
			this.leftSubClause = clause;
			return this;
		}
		
		this.leftSubClause = new OrClause(this.leftSubClause, clause);
		return this;
	}
	
}
