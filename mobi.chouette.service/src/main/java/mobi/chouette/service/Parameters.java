package mobi.chouette.service;

import java.util.Iterator;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

import org.codehaus.jettison.json.JSONObject;

@Data
@Log4j
public class Parameters {

	private AbstractParameter configuration;
	
	private String command;

	private ValidationParameters validation;
	
	@SuppressWarnings("rawtypes")
	public Parameters(String jsonSource,InputValidator inputValidator) throws Exception
	{
		if (inputValidator == null) return;
		JSONObject object = new JSONObject(jsonSource);
		if (object.length() != 1)
		{
			log.error("wrong size for json container");
			throw new Exception("wrong size for json container");
		}
		JSONObject param = object.getJSONObject("parameters");
		if (param.length() < 1 || param.length() > 2)
		{
			log.error("wrong size for json parameter");
			throw new Exception("wrong size for json parameter");
		}
		for (Iterator iterator = param.keys(); iterator.hasNext();) {
			String type = (String) iterator.next();
			JSONObject elt = new JSONObject();
			elt.put(type, param.getJSONObject(type));
			if (type.equals("validation"))
			{
				validation = inputValidator.toValidation(elt.toString());
			}
			else
			{
				command = type;
				configuration = inputValidator.toActionParameter(elt.toString());
				if (configuration == null)
				{
					throw new RequestServiceException(RequestExceptionCode.INVALID_PARAMETERS, "wrong type "+ type);
				}
			}
		}
	}

}
