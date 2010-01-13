package fr.certu.chouette.modele;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class SchemaObjet {

	public String toString() {
	       return ToStringBuilder.reflectionToString(this,
	               ToStringStyle.MULTI_LINE_STYLE);
	}

}
