package mobi.chouette.validation;

import mobi.chouette.common.Context;

public interface Validator<T> {

	boolean validate(Context context, T object);

}
