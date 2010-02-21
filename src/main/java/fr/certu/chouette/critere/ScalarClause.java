package fr.certu.chouette.critere;

public class ScalarClause extends AbstractOperatorClause {
	protected Object value;
	protected ScalarOperatorEnum operator;
	
	public ScalarOperatorEnum getOperator() {
		return operator;
	}
	private ScalarClause(String parameterName, Object value,
			ScalarOperatorEnum operator) {
		super(parameterName);
		this.value = value;
		this.operator = operator;
	}
	public  static ScalarClause newEqualsClause(String parameterName, Object value) {
		return new ScalarClause( parameterName, value, ScalarOperatorEnum.Equals);
	}
	public  static ScalarClause newLikeClause(String parameterName, Object value) {
		return new ScalarClause( parameterName, value, ScalarOperatorEnum.Like);
	}
	public  static ScalarClause newIlikeClause(String parameterName, Object value) {
		return new ScalarClause( parameterName, value, ScalarOperatorEnum.Ilike);
	}
	
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String toString() {
		return "[" + super.toString() + ", operator : " +operator+ ", value : " + value + "]";
	}
}
