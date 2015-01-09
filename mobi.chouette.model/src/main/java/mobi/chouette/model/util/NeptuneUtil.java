package mobi.chouette.model.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.NeptuneObject;

public abstract class NeptuneUtil {

	/**
	 * Build a list of Neptune Ids (ObjectId) from a list of Neptune Objects
	 * 
	 * @param neptuneObjects
	 *            the list to parse
	 * @return the object ids list
	 */
	public static List<String> extractObjectIds(
			List<? extends NeptuneIdentifiedObject> neptuneObjects) {
		List<String> objectIds = new ArrayList<String>();
		if (neptuneObjects != null) {
			for (NeptuneIdentifiedObject neptuneObject : neptuneObjects) {
				if (neptuneObject != null) {
					String objectId = neptuneObject.getObjectId();
					if (objectId != null) {
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
	 * @param neptuneObjects
	 *            the list to parse
	 * @return the ids map
	 */
	public static <T extends NeptuneIdentifiedObject> Map<String, T> mapOnObjectIds(
			List<T> neptuneObjects) {
		Map<String, T> map = new HashMap<String, T>();
		if (neptuneObjects != null) {
			for (T neptuneObject : neptuneObjects) {
				if (neptuneObject != null) {
					String id = neptuneObject.getObjectId();
					if (id != null) {
						map.put(id, neptuneObject);
					}
				}
			}
		}
		return map;
	}

	/**
	 * Build a list of internal Ids (Id) from a list of Neptune Objects
	 * 
	 * @param neptuneObjects
	 *            the list to parse
	 * @return the ids list
	 */
	public static List<Long> extractIds(
			List<? extends NeptuneObject> neptuneObjects) {
		List<Long> ids = new ArrayList<Long>();
		if (neptuneObjects != null) {
			for (NeptuneObject neptuneObject : neptuneObjects) {
				if (neptuneObject != null) {
					Long id = neptuneObject.getId();
					if (id != null) {
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
	 * @param neptuneObjects
	 *            the list to parse
	 * @return the ids map
	 */
	public static <T extends NeptuneObject> Map<Long, T> mapOnIds(
			List<T> neptuneObjects) {
		Map<Long, T> map = new HashMap<Long, T>();
		if (neptuneObjects != null) {
			for (T neptuneObject : neptuneObjects) {
				if (neptuneObject != null) {
					Long id = neptuneObject.getId();
					if (id != null) {
						map.put(id, neptuneObject);
					}
				}
			}
		}
		return map;
	}
	
	
}
