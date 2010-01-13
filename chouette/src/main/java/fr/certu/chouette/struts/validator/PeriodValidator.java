package fr.certu.chouette.struts.validator;

import java.util.Date;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.opensymphony.xwork2.validator.validators.ValidatorSupport;

public class PeriodValidator extends  ValidatorSupport 
{
	
	public void validate(Object object) throws ValidationException 
	{
		Date start = (Date)getFieldValue("debut", object);
		Date end = (Date)getFieldValue("fin", object);
		ValidatorContext ctxt = getValidatorContext();
		if (start != null && end != null && start.after(end)) {
			//ctxt.addActionError("The period defined is not valid ");
			ctxt.addActionError(this.getMessage(null));
			return;
		}
	}
	
}
	
