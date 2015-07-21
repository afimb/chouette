package mobi.chouette.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLTagValidator {
	
	private static Pattern pattern;
	private static Matcher matcher;
	
	private static final String HTML_TAG_PATTERN = "[^<]*<(\"[^\"]*\"|'[^']*'|[^'\">])*>[^>]*";
	 
	public HTMLTagValidator(){
		pattern = Pattern.compile(HTML_TAG_PATTERN);
	}
	 
	/**
	 * Validate html tag with regular expression
	 * @param tag html tag for validation
	 * @return true valid html tag, false invalid html tag
	 */
	public static boolean validate(final String tag){
		matcher = pattern.matcher(tag);
		return matcher.matches();
	  }
}
