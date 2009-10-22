package fr.certu.chouette.critere;


public class AndClause extends LogicClause  {
	
	public AndClause () {}
	
	public AndClause (IClause ... clauses) {

		add (clauses);
	}
	
	public AndClause add (IClause ... clauses) {
		
		for (IClause clause : clauses) {
			add(clause);
		}
		
		return this;
	}
	
	public AndClause add (IClause clause) {
		
		if (clause == null) return this;
		
		if (rightSubClause == null) {
			this.rightSubClause = clause;
			return this;
		}
		
		if (leftSubClause == null) {
			this.leftSubClause = clause;
			return this;
		}
		
		this.leftSubClause = new AndClause(this.leftSubClause, clause);
		return this;
	}
	
}
