package fr.certu.chouette.service.amivif;

import java.util.Hashtable;
import java.util.Map;

import chouette.schema.AreaCentroid;
import chouette.schema.ChouetteArea;
import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.StopArea;
import chouette.schema.StopPoint;
import chouette.schema.types.ChouetteAreaType;

public class AccesseurAreaStop implements IAccesseurAreaStop 
{
	private Map<String, StopArea> stopAreaParStopId;
	private Map<String, AreaCentroid> areaCentroidParStopId;
	private Map<String, AreaCentroid> areaCentroidParAreaId;
	private Map<String, AreaCentroid> centroidParId;
	
	public void initialiser(ChouettePTNetworkTypeType chouettePTNetwork)
	{
		ChouetteArea chouetteArea = chouettePTNetwork.getChouetteArea();
		
		Map<String, StopArea> areaParId = new Hashtable<String, StopArea>();
		int totalArea = chouetteArea.getStopAreaCount();
		for (int i = 0; i < totalArea; i++) 
		{
			StopArea stopArea = chouetteArea.getStopArea(i);
			ChouetteAreaType areaType = stopArea.getStopAreaExtension().getAreaType();
			if ( 	ChouetteAreaType.BOARDINGPOSITION.equals( areaType)
				|| 	ChouetteAreaType.QUAY.equals( areaType))
				areaParId.put( stopArea.getObjectId(), chouetteArea.getStopArea(i));
		}
		centroidParId = new Hashtable<String, AreaCentroid>();
		int totalCentroid = chouetteArea.getAreaCentroidCount();
		for (int i = 0; i < totalCentroid; i++) 
		{
			AreaCentroid areaCentroid = chouetteArea.getAreaCentroid( i);
			centroidParId.put( areaCentroid.getObjectId(), areaCentroid);
		}
		
		stopAreaParStopId = new Hashtable<String, StopArea>();
		areaCentroidParStopId = new Hashtable<String, AreaCentroid>();
		areaCentroidParAreaId = new Hashtable<String, AreaCentroid>();
		int totalStop = chouettePTNetwork.getChouetteLineDescription().getStopPointCount();
		for (int i = 0; i < totalStop; i++) 
		{
			StopPoint stopPoint = chouettePTNetwork.getChouetteLineDescription().getStopPoint( i);
			
			StopArea stopArea = areaParId.get( stopPoint.getContainedIn());
			stopAreaParStopId.put( stopPoint.getObjectId(), stopArea);
			areaCentroidParStopId.put( stopPoint.getObjectId(), centroidParId.get( stopArea.getCentroidOfArea()));
			areaCentroidParAreaId.put( stopArea.getObjectId(), centroidParId.get( stopArea.getCentroidOfArea()));
		}
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.amivif.IAccesseurAreaStop#getStopAreaOfStop(java.lang.String)
	 */
	public StopArea getStopAreaOfStop( String stopId)
	{
		return stopAreaParStopId.get( stopId);
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.amivif.IAccesseurAreaStop#getAreaCentroidOfStop(java.lang.String)
	 */
	public AreaCentroid getAreaCentroidOfStop( String stopId)
	{
		return areaCentroidParStopId.get( stopId);
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.amivif.IAccesseurAreaStop#getAreaCentroidOfArea(java.lang.String)
	 */
	public AreaCentroid getAreaCentroidOfArea( String stopId)
	{
		return areaCentroidParAreaId.get( stopId);
	}
	
	public AreaCentroid getCentroidById( String centroidId)
	{
		return centroidParId.get( centroidId);
	}
}
