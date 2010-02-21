package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.ChouettePTNetwork;
import fr.certu.chouette.service.validation.commun.ValidationException;

public interface IMainSchemaProducer {
	
	public ChouettePTNetwork getASG(chouette.schema.ChouettePTNetworkTypeType castorChouettePTNetwork);
	public void setValidationException(ValidationException validationException);
	public ValidationException getValidationException();
	public void setChouettePTNetwork(ChouettePTNetwork chouettePTNetwork);
	public ChouettePTNetwork getChouettePTNetwork();
}
