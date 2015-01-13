package mobi.chouette.validation;

import mobi.chouette.common.Context;

public interface Validator<T> {

	ValidationConstraints validate(Context context, T target, Object... params)
			throws ValidationException;

}
