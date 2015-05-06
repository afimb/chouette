package mobi.chouette.command;

import java.io.File;

import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

import org.apache.commons.io.FileUtils;

public class ParametersConverter {

	public static AbstractParameter convertConfiguration(String filename) throws Exception
	{
		String parameters = FileUtils.readFileToString(new File(filename), "UTF-8");
	    parameters = "{ \"parameters\" : "+parameters+ "}";
	    
	    return JSONUtil.fromJSON(parameters, Parameters.class).getConfiguration();
	}
	
	public static ValidationParameters convertValidation(String filename) throws Exception
	{
		String parameters = FileUtils.readFileToString(new File(filename), "UTF-8");
	    
	    return JSONUtil.fromJSON(parameters, ValidationParameters.class);
	}
}
