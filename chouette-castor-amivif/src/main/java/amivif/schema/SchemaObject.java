/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package amivif.schema;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class SchemaObject {

	public String toString() {
	       return ToStringBuilder.reflectionToString(this,
	               ToStringStyle.MULTI_LINE_STYLE);
	}

}
