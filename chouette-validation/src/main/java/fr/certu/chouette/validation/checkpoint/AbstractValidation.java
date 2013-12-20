/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.validation.checkpoint;

import fr.certu.chouette.model.neptune.NeptuneLocalizedObject;

/**
 * @author michel
 *
 */
public abstract class AbstractValidation 
{
	/**
	 * calculate distance on spheroid
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	protected double distance(NeptuneLocalizedObject obj1,NeptuneLocalizedObject obj2)
	{
		double long1rad = Math.toRadians(obj1.getLongitude().doubleValue());
		double lat1rad = Math.toRadians(obj1.getLatitude().doubleValue());
		double long2rad = Math.toRadians(obj2.getLongitude().doubleValue());
		double lat2rad = Math.toRadians(obj2.getLatitude().doubleValue());
		
		double alpha = Math.cos(lat1rad)*Math.cos(lat2rad)*Math.cos(long2rad-long1rad) + Math.sin(lat1rad)*Math.sin(lat2rad);
			
		double distance = 6378. * Math.acos(alpha);
		
		return distance * 1000.;
	}

}
