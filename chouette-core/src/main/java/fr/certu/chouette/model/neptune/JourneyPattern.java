package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class JourneyPattern extends NeptuneIdentifiedObject
{
	private static final long serialVersionUID = 7895941111990419404L;
	@Getter @Setter private String registrationNumber; //  BD
	@Getter @Setter private String comment; //  BD
	@Getter @Setter private String origin;   // StopPointId départ (déterminé par algo ? 
	@Getter @Setter private String destination; // StopPointId arrivée (déterminé par algo ?
	@Getter @Setter private String publishedName; //  BD
	@Getter @Setter private List<StopPoint>	stopPoints;
	@Getter @Setter private List<String>	stopPointIds;  // StopPointId (déterminé par algo ?
	@Getter @Setter private String lineIdShortcut; //(déterminé par algo : remontée de route)
	@Getter @Setter private String routeId; // FK
	@Getter @Setter private Route route;   // FK 
	@Getter @Setter private List<VehicleJourney> vehicleJourneys; // FK inverse



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(String indent,int level)
	{
		StringBuilder sb = new StringBuilder(super.toString(indent,level));
		sb.append("\n").append(indent).append("routeId = ").append(routeId);
		sb.append("\n").append(indent).append("publishedName = ").append(publishedName);
		sb.append("\n").append(indent).append("origin = ").append(origin);
		sb.append("\n").append(indent).append("destination = ").append(destination);
		sb.append("\n").append(indent).append("registrationNumber = ").append(registrationNumber);
		sb.append("\n").append(indent).append("comment = ").append(comment);

		if (stopPointIds != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("stopPointIds");
			for (String stopPointId : stopPointIds)
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(stopPointId);
			}
		}
		if (level > 0)
		{
			int childLevel = level -1;
			String childIndent = indent + CHILD_INDENT;

			childIndent = indent + CHILD_LIST_INDENT;
			if (stopPoints != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("stopPoints");
				for (StopPoint stopPoint : getStopPoints())
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(stopPoint.toString(childIndent,childLevel));
				}
			}
			if (vehicleJourneys != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("vehicleJourneys");
				for (VehicleJourney vehicleJourney : getVehicleJourneys())
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(vehicleJourney.toString(childIndent,childLevel));
				}
			}
		}
		return sb.toString();
	}

	public void addStopPointId(String stopPointId)
	{
		if (stopPointIds== null) stopPointIds = new ArrayList<String>();
		if (!stopPointIds.contains(stopPointId))
			stopPointIds.add(stopPointId);
	}

	public void addStopPoint(StopPoint stopPoint)
	{
		if (stopPoints== null) stopPoints = new ArrayList<StopPoint>();
		if (!stopPoints.contains(stopPoint))
			stopPoints.add(stopPoint);
	}

	public void removeStopPoint(StopPoint stopPoint)
	{
		if (stopPoints== null) stopPoints = new ArrayList<StopPoint>();
		if (stopPoints.contains(stopPoint))
			stopPoints.remove(stopPoint);
	}

	public void addVehicleJourney(VehicleJourney vehicleJourney)
	{
		if (vehicleJourneys== null) vehicleJourneys = new ArrayList<VehicleJourney>();
		if (!vehicleJourneys.contains(vehicleJourney))
			vehicleJourneys.add(vehicleJourney);
	}

	public void removeVehicleJourney(VehicleJourney vehicleJourney)
	{
		if (vehicleJourneys== null) vehicleJourneys = new ArrayList<VehicleJourney>();
		if (vehicleJourneys.contains(vehicleJourney))
			vehicleJourneys.remove(vehicleJourney);
	}


	public String getStopPointsAsKey()
	{
		
		if (stopPoints != null)
		{
			StringBuffer buffer = new StringBuffer();
			for (StopPoint point : stopPoints) 
			{
				buffer.append(point.getId());
				buffer.append(',');
			}
			return buffer.toString();
		}
        return "empty journeyPattern";
	}


	@Override
	public boolean clean() {
		if(vehicleJourneys == null){
			return false;
		}
		for (Iterator<VehicleJourney> iterator = vehicleJourneys.iterator(); iterator.hasNext();) {
			VehicleJourney vehicleJourney = iterator.next();
			if(vehicleJourney == null || !vehicleJourney.clean()){
				iterator.remove();
			}
		}
		if(vehicleJourneys.isEmpty()){
			return false;
		}
		return true;
	}
	
   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#complete()
    */
   @Override
   public void complete()
   {
      if (isCompleted()) return;
      super.complete();
      Route route = getRoute();
      if(route != null)
      {
         Line line = route.getLine();
         if(line != null)
            setLineIdShortcut(line.getObjectId());
      }
      List<StopPoint> stopPoints = getStopPoints();
      List<VehicleJourney> vjs = getVehicleJourneys();
      if (vjs != null && !vjs.isEmpty())
      {
         // complete StopPoints
         if (stopPoints == null || stopPoints.isEmpty())
         {
            VehicleJourney vj = vjs.get(0);
            for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops()) 
            {
               addStopPoint(vjas.getStopPoint());
            }
         }
         // complete VJ
         for (VehicleJourney vehicleJourney : vjs) 
         {
            vehicleJourney.complete();
         }
      }
   }
}
