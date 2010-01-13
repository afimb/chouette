package fr.certu.chouette.service.validation.amivif;

public class LocationTridentObject extends TridentObject {
	
	private ReferencingMethod	referencingMethod;	// 0..1
	
	public void setLocationTridentObject(LocationTridentObject locationTridentObject) {
		setTridentObject(locationTridentObject);
		this.setReferencingMethod(locationTridentObject.getReferencingMethod());
	}
	
	public void setReferencingMethod(ReferencingMethod referencingMethod) {
		this.referencingMethod = referencingMethod;
	}
	
	public ReferencingMethod getReferencingMethod() {
		return referencingMethod;
	}
	
	public enum ReferencingMethod {
		VALUE_1,
		VALUE_2,
		VALUE_3,
		VALUE_4,
		VALUE_5,
		VALUE_6,
		VALUE_7,
		VALUE_8,
		VALUE_9,
		VALUE_10,
		VALUE_11
	}
}
