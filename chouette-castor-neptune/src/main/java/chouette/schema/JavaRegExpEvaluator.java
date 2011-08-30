/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package chouette.schema;

import java.util.regex.Pattern;

import org.exolab.castor.util.RegExpEvaluator;

/**
 * @author michel
 *
 */
public class JavaRegExpEvaluator implements RegExpEvaluator 
{
    private String regex;
	/* (non-Javadoc)
	 * @see org.exolab.castor.util.RegExpEvaluator#matches(java.lang.String)
	 */
	@Override
	public boolean matches(String value) 
	{
		return Pattern.matches(regex, value);
	}

	/* (non-Javadoc)
	 * @see org.exolab.castor.util.RegExpEvaluator#setExpression(java.lang.String)
	 */
	@Override
	public void setExpression(String rexpr) 
	{
		regex = rexpr;
	}

}
