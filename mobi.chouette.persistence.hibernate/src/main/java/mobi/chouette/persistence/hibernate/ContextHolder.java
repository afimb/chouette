package mobi.chouette.persistence.hibernate;

import lombok.Getter;
import lombok.Setter;

public class ContextHolder {

	private static final ThreadLocal<String> holder = new ThreadLocal<String>();

	@Getter
	@Setter
	private static String defaultSchema;

	public static void setContext(final String context) {
		holder.set(context);
	}

	public static String getContext() {
		String result = holder.get();
		return (result != null) ? result : defaultSchema;
	}

	public static void clear() {
		holder.remove();
	}

}
