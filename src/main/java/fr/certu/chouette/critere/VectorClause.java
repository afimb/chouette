package fr.certu.chouette.critere;

import java.util.Collection;

public class VectorClause extends AbstractOperatorClause {
	protected Collection values;
	protected VectorOperatorEnum operator;

	private VectorClause(String parameterName, Collection values,
			VectorOperatorEnum operator) {
		super(parameterName);
		this.values = values;
		this.operator = operator;
	}
	public static VectorClause newInClause(String parameterName, Collection values) {
		return new VectorClause(parameterName, values, VectorOperatorEnum.In);
	}
	
	public VectorOperatorEnum getOperator() {
		return operator;
	}
	public Collection getValues() {
		return values;
	}
	public void setValues(Collection values) {
		this.values = values;
	}
	public String toString() {
		return "[" + super.toString() + ", operator : " +operator+ ", values : " + values.toString() + "]";
	}
}
