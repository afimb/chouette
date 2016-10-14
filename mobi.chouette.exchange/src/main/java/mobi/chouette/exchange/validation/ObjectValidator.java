package mobi.chouette.exchange.validation;

import mobi.chouette.common.Context;

public class ObjectValidator implements Validator<Object> {

	@Override
	public void validate(Context context, Object target
			) throws ValidationException {
		return;
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {

		private ObjectValidator instance;

		@Override
		protected Validator<Object> create(Context context) {
			if (instance == null) {
				instance = new ObjectValidator();
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
				.put(ObjectValidator.class.getName(), new DefaultValidatorFactory());
	}



}
