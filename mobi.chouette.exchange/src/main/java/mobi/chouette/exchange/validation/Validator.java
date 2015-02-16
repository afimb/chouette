package mobi.chouette.exchange.validation;

import mobi.chouette.common.Context;

public interface Validator<T> {
	
	ValidationConstraints validate(Context context, T target)
			throws ValidationException;

}
