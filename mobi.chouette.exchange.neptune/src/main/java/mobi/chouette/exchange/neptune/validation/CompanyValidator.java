package mobi.chouette.exchange.neptune.validation;


import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.Company;
import mobi.chouette.model.NeptuneIdentifiedObject;

public class CompanyValidator extends AbstractValidator implements Validator<Company> , Constant{

	public static String NAME = "CompanyValidator";
	
	// private static final String COMPANY_1 = "2-NEPTUNE-Company-1";

	public static final String LOCAL_CONTEXT = "Company";


    @Override
	protected void initializeCheckPoints(Context context)
	{
		// addItemToValidation(context, prefix, "Company", 1, "W");

	}

	public void addLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber)
	{
		addLocation( context,LOCAL_CONTEXT,  object,  lineNumber,  columnNumber);
		
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
				instance = new CompanyValidator();
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
