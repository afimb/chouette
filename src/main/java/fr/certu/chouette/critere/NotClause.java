package fr.certu.chouette.critere;

import org.apache.log4j.Logger;

public class NotClause implements IUnaryClause, IClause {
	
	protected static final Logger logger = Logger.getLogger(NotClause.class);
	
	protected IClause clause;
	
	public NotClause(IClause clause) {
		setClause(clause);
	}
	
	public IClause getClause() {
		return clause; 
	}
	
	public void setClause(IClause clause) {
		this.clause = clause; 
	}
}
