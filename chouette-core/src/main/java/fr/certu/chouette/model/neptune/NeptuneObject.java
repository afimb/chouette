/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.filter.DetailLevelEnum;


/**
 * basic class for every Neptune object
 */

public abstract class NeptuneObject implements Serializable
{
	private static final long serialVersionUID = -1406542019260386319L;
	protected static final String CHILD_INDENT = "        ";
	protected static final String CHILD_ARROW = "  -->";
	protected static final String CHILD_LIST_INDENT = "           ";
	protected static final String CHILD_LIST_ARROW = "      -->";

	@Getter @Setter private Long id;
//	@Getter private DetailLevelEnum level = DetailLevelEnum.UNINITIALIZED;

	private boolean validationProceeded = false;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString()
	{

		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);


		//return toString("",0);
	}

	/**
	 * pretty ToString()
	 * 
	 * @param indent indentation
	 * @param level deep level to print
	 * @return
	 */
	public String toString(String indent, int level)
	{
		return getClass().getSimpleName()+ "( id="+id +") ";
	}

//	/**
//	 * @param level
//	 */
//	public void expand(DetailLevelEnum level)
//	{
//		this.level = level;
//		return;
//	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NeptuneObject another = (NeptuneObject) obj;
		if (id != null) return id.equals(another.getId());
		return false;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		if (id != null) return id.hashCode();
		return super.hashCode();
	} 

	/**
	 * check if validation can check this object
	 * 
	 * @return true at first check and false after
	 */
	public final boolean checkValidationProcess()
	{
		boolean check = !validationProceeded;
		validationProceeded = true;
		return check;
	}

}
