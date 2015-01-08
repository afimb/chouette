package mobi.chouette.validation;

import mobi.chouette.common.Context;

public class ObjectValidator implements Validator<Object> {

	@Override
	public boolean validate(Context context, Object object) {
		return false;
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
