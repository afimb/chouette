package fr.certu.chouette.critere;

import org.apache.log4j.Logger;

public abstract class LogicClause implements IBinaryClause, IClause {
	
	protected static final Logger logger = Logger.getLogger(LogicClause.class);
	
	protected IClause rightSubClause;
	protected IClause leftSubClause;
	
	public IClause getRightSubClause() {
		return rightSubClause;
	}
	
	public void setRightSubClause(IClause rightSubClause) {
		this.rightSubClause = rightSubClause;
	}
	
	public IClause getLeftSubClause() {
		return leftSubClause;
	}
	
	public void setLeftSubClause(IClause leftSubClause) {
		this.leftSubClause = leftSubClause;
	}
	
}
