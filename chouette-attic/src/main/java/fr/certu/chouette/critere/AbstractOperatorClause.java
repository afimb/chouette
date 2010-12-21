package fr.certu.chouette.critere;

public abstract class AbstractOperatorClause implements IClause {
	protected String parameterName;
	
	public AbstractOperatorClause(String parameterName) {
		super();
		this.parameterName = parameterName;
	}
	public String toString() {
		return "parameterName : " + parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	public String getParameterName() {
		return parameterName;
	}
}
