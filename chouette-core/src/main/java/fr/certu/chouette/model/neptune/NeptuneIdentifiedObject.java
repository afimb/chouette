/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract object used for all Identified Neptune Object
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
@SuppressWarnings("serial")
public abstract class NeptuneIdentifiedObject extends NeptuneObject
{
	/**
	 * Neptune unique identifier : mandatory
	 */
	@Getter @Setter private String objectId;  // BD
	/**
	 * Version of Neptune Object (default = 1) 
	 */
	@Getter @Setter private int objectVersion = 1; // BD
	/**
	 * Creation time
	 */
	@Getter @Setter private Date creationTime;  // BD
	/**
	 * Creator Reference 
	 */
	@Getter @Setter private String creatorId; // BD
	/**
	 * Object Name
	 */
	@Getter @Setter private String name; // BD

	/* (non-Javadoc)
	 * @see fr.certu.chouette.model.neptune.NeptuneObject#toString(java.lang.String, int)
	 */
	@Override
	public String toString(String indent, int level)
	{
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
		String s = super.toString(indent,level);
		s += "\n"+indent+"  objectId = "+objectId;
		s += "\n"+indent+"  objectVersion = "+objectVersion;
		if (creationTime != null)
			s += "\n"+indent+"  creationTime = "+f.format(creationTime);
		if (creatorId != null)
			s += "\n"+indent+"  creatorId = "+creatorId;
		if (name != null)
			s += "\n"+indent+"  name = "+name;

		return s;
	}

	/**
	 * Build a list of Neptune Ids (ObjectId) from a list of Neptune Objects 
	 * 
	 * @param neptuneObjects the list to parse
	 * @return the object ids list
	 */
	public static List<String> extractObjectIds(List<? extends NeptuneIdentifiedObject> neptuneObjects){
		List<String> objectIds = new ArrayList<String>();
			if(neptuneObjects != null){
			for (NeptuneIdentifiedObject neptuneObject : neptuneObjects) {
				if(neptuneObject != null){
					String objectId = neptuneObject.getObjectId();
					if(objectId != null){
						objectIds.add(objectId);
					}
				}
			}
		}
			
		return objectIds;
	}
	
	
	@Override
	public boolean equals(Object arg0) 
	{
		if (arg0 instanceof NeptuneIdentifiedObject)
		{
			NeptuneIdentifiedObject another = (NeptuneIdentifiedObject) arg0;
			if (objectId != null) return objectId.equals(another.getObjectId());
		}
		return super.equals(arg0);
	}


	@Override
	public int hashCode() 
	{
		if (objectId != null) return objectId.hashCode();
		return super.hashCode();
	} 
}
