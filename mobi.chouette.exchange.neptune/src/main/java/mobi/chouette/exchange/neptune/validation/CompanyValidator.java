package mobi.chouette.exchange.neptune.validation;


import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.Company;

public class CompanyValidator extends AbstractValidator implements Validator<Company> , Constant{

	public static String NAME = "CompanyValidator";
	
	// private static final String COMPANY_1 = "2-NEPTUNE-Company-1";

	static final String LOCAL_CONTEXT = "Company";


	public CompanyValidator(Context context) 
	{
		// addItemToValidation(context, prefix, "Company", 1, "W");

	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		Context objectContext = getObjectContext(context,LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_NUMBER, Integer.valueOf(lineNumber));
		objectContext.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));
		
	}
	
	

	@Override
	public ValidationConstraints validate(Context context, Company target) throws ValidationException
	{
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {

		

		@Override
		protected Validator<Company> create(Context context) {
			CompanyValidator instance = (CompanyValidator) context.get(NAME);
			if (instance == null) {
				instance = new CompanyValidator(context);
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(CompanyValidator.class.getName(), new DefaultValidatorFactory());
	}



}
