/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import fr.certu.chouette.filter.DetailLevelEnum;


/**
 * basic class for every Neptune object
 */

public abstract class NeptuneObject implements Serializable
{
	private static final Logger logger = Logger.getLogger(NeptuneObject.class);
	
	// constant for persistence fields
	public static final String ID = "id"; 

	private static final long serialVersionUID = -1406542019260386319L;
	protected static final String CHILD_INDENT = "        ";
	protected static final String CHILD_ARROW = "  -->";
	protected static final String CHILD_LIST_INDENT = "           ";
	protected static final String CHILD_LIST_ARROW = "      -->";

	@Getter @Setter private Long id;
//	@Getter private DetailLevelEnum level = DetailLevelEnum.UNINITIALIZED;

	private boolean validationProceeded = false;


	/**
	 * Build a list of internal Ids (Id) from a list of Neptune Objects 
	 * 
	 * @param neptuneObjects the list to parse
	 * @return the ids list
	 */
	public static List<Long> extractIds(List<? extends NeptuneObject> neptuneObjects)
	{
		List<Long> ids = new ArrayList<Long>();
		if(neptuneObjects != null)
		{
			for (NeptuneObject neptuneObject : neptuneObjects) 
			{
				if(neptuneObject != null)
				{
					Long id = neptuneObject.getId();
					if(id != null)
					{
						ids.add(id);
					}
				}
			}
		}

		return ids;
	}

	/**
	 * Build a map of internal Ids (Id) from a list of Neptune Objects 
	 * 
	 * @param neptuneObjects the list to parse
	 * @return the ids map
	 */
	public static <T extends NeptuneObject> Map<Long,T > mapOnIds(List<T> neptuneObjects)
	{
		Map<Long,T> map = new HashMap<Long,T>();
		if(neptuneObjects != null)
		{
			for (T neptuneObject : neptuneObjects) 
			{
				if(neptuneObject != null)
				{
					Long id = neptuneObject.getId();
					if(id != null)
					{
						map.put(id, neptuneObject);
					}
				}
			}
		}
		return map;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString()
	{

		//		return ToStringBuilder.reflectionToString(this,
		//				ToStringStyle.MULTI_LINE_STYLE);
		//
		//
		return toString("",0);
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

	
	/**
	 * @param level
	 */
//	public void expand(DetailLevelEnum level)
//	{
//		if (this.level.ordinal() >= level.ordinal()) return;
//		this.level = level;
//
//		if (this.level.ordinal() > DetailLevelEnum.ATTRIBUTE.ordinal() )
//		{
//			Method[] methods = this.getClass().getMethods();
//
//			for (Method method : methods) 
//			{
//				if (method.getName().startsWith("get"))
//				{
//					Class<?> returnType = method.getReturnType();
//					if ((returnType.getSuperclass() != null && returnType.getSuperclass().equals(NeptuneIdentifiedObject.class)) || returnType.equals(List.class))
//					{
//						try 
//						{
//							Object ret = method.invoke(this, new Object[0]);
//						} 
//						catch (Exception e) 
//						{
//							logger.error("cannot call "+method.getName(),e);
//						} 
//					}
//				}
//			}
//		}
//
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
