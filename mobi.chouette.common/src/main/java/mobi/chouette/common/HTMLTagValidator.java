package mobi.chouette.common;

import java.util.regex.Pattern;

public class HTMLTagValidator {
	
	private static Pattern pattern = Pattern.compile("[^<]*<(\"[^\"]*\"|'[^']*'|[^'\">])*>[^>]*");
	 
	/**
	 * Validate html tag with regular expression
	 * @param tag html tag for validation
	 * @return true valid html tag, false invalid html tag
	 */
	public static boolean validate(final String tag){
		return pattern.matcher(tag).matches();
	}
}
