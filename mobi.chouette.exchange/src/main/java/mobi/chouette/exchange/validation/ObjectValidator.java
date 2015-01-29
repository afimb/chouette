package mobi.chouette.exchange.validation;

import mobi.chouette.common.Context;

public class ObjectValidator implements Validator<Object> {

	@Override
	public ValidationConstraints validate(Context context, Object target,
			Object... params) throws ValidationException {
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {

		private ObjectValidator instance;

		@Override
		protected Validator<Object> create() {
			if (instance == null) {
				instance = new ObjectValidator();
			}
			return instance;
		}

	}

	static {
		ValidatorFactory factory = new DefaultValidatorFactory();
		ValidatorFactory.factories
				.put(ObjectValidator.class.getName(), factory);
	}



}
