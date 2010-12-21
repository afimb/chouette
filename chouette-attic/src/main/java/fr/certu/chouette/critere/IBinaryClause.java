package fr.certu.chouette.critere;

public interface IBinaryClause {
	public IClause getRightSubClause();
	
	public void setRightSubClause(IClause rightSubClause);
	
	public IClause getLeftSubClause();
	
	public void setLeftSubClause(IClause leftSubClause);
}
