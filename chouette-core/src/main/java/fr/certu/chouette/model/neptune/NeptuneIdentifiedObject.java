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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

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
   private static final Logger logger = Logger.getLogger(NeptuneIdentifiedObject.class); 
	// constant for persistence fields
	public static final String OBJECT_ID = "objectId"; 
	public static final String OBJECT_VERSION = "objectVersion"; 
	public static final String CREATION_TIME = "creationTime"; 
	public static final String CREATOR_ID = "creatorId"; 
	public static final String NAME = "name"; 

	// constants for ObjectId prefixes
	public static final String ACCESSPOINT_KEY="AccessPoint";
	public static final String ACCESSLINK_KEY="AccessLink";
	public static final String AREACENTROID_KEY="AreaCentroid";
	public static final String COMPANY_KEY="Company";
	public static final String CONNECTIONLINK_KEY="ConnectionLink";
	public static final String FACILITY_KEY="Facility";
	public static final String GROUPOFLINE_KEY="GroupOfLine";
	public static final String JOURNEYPATTERN_KEY="JourneyPattern";
	public static final String LINE_KEY="Line";
	public static final String PTLINK_KEY="PtLink";
	public static final String PTNETWORK_KEY="GroupOfLine";
	public static final String ROUTE_KEY="Route";
	public static final String STOPAREA_KEY="StopArea";
	public static final String STOPPOINT_KEY="StopPoint";
	public static final String TIMESLOT_KEY="TimeSlot";
	public static final String TIMETABLE_KEY="Timetable";
	public static final String VEHICLEJOURNEY_KEY="VehicleJourney";


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

	/**
	 * indicated if object is completed for export purpose
	 */
	@Getter private boolean completed = false;
	
	/**
	 * Clean object dependencies : if children objects are not clean, they are dereferenced.
	 * <br/>
	 * This method is available for export purpose 
	 * 
	 * @return true if object is clean (i.e. has valid references) or false if it isn't  
	 */
	public boolean clean()
	{
		return true;
	}

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
	 * default complete action : do nothing
	 */
	public void complete()
	{
	   completed = true;
	}
	
	/**
	 * Build a list of Neptune Ids (ObjectId) from a list of Neptune Objects 
	 * 
	 * @param neptuneObjects the list to parse
	 * @return the object ids list
	 */
	public static List<String> extractObjectIds(List<? extends NeptuneIdentifiedObject> neptuneObjects)
	{
		List<String> objectIds = new ArrayList<String>();
		if(neptuneObjects != null)
		{
			for (NeptuneIdentifiedObject neptuneObject : neptuneObjects) 
			{
				if(neptuneObject != null)
				{
					String objectId = neptuneObject.getObjectId();
					if(objectId != null)
					{
						objectIds.add(objectId);
					}
				}
			}
		}

		return objectIds;
	}

	
	/**
	 * Build a map of objectIds (Id) from a list of Neptune Identified Objects 
	 * 
	 * @param neptuneObjects the list to parse
	 * @return the ids map
	 */
	public static <T extends NeptuneIdentifiedObject> Map<String,T > mapOnObjectIds(List<T> neptuneObjects)
	{
		Map<String,T> map = new HashMap<String,T>();
		if(neptuneObjects != null)
		{
			for (T neptuneObject : neptuneObjects) 
			{
				if(neptuneObject != null)
				{
					String id = neptuneObject.getObjectId();
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
	 * @see fr.certu.chouette.model.neptune.NeptuneObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof  NeptuneIdentifiedObject))
		{
			return false;
		}
		NeptuneIdentifiedObject another = (NeptuneIdentifiedObject) obj;
		if (objectId != null) 
		{
			return objectId.equals(another.getObjectId());
		}
		
		return super.equals(obj);
	}


	/* (non-Javadoc)
	 * @see fr.certu.chouette.model.neptune.NeptuneObject#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		if (objectId != null) return objectId.hashCode();
		return super.hashCode();
	}
	

	/**
	 * check if an objectId is conform to Trident 
	 * @param oid objectId to check
	 * @return true if valid, false othewise
	 */
	public static boolean checkObjectId(String oid)
	{
	   if (oid == null) return false;
	   
	   Pattern p = Pattern.compile("(\\w|_)+:\\w+:([0-9A-Za-z]|_|-)+");
	   return p.matcher(oid).matches();
	}
}
