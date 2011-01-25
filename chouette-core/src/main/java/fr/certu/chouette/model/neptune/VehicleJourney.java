package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.type.ServiceStatusValueEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;

public class VehicleJourney extends NeptuneIdentifiedObject
{
	@Getter @Setter private ServiceStatusValueEnum serviceStatusValue;
	@Getter @Setter private TransportModeNameEnum transportMode;
	@Getter @Setter private String comment;
	@Getter @Setter private String facility;
	@Getter @Setter private long number;
	@Getter @Setter private String routeId;
	@Getter @Setter private Route route;
	@Getter @Setter private String journeyPatternId;
	@Getter @Setter private JourneyPattern journeyPattern;
	@Getter @Setter private String timeSlotId;
	@Getter @Setter private TimeSlot timeSlot;
	@Getter @Setter private String publishedJourneyName;
	@Getter @Setter private String publishedJourneyIdentifier;
	@Getter @Setter private String vehicleTypeIdentifier;
	@Getter @Setter private String operatorId;
	@Getter @Setter private List<VehicleJourneyAtStop> vehicleJourneyAtStops;


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(String indent,int level)
	{
		StringBuilder sb = new StringBuilder(super.toString(indent,level));
		sb.append("\n").append(indent).append("serviceStatusValue = ").append(serviceStatusValue);
		sb.append("\n").append(indent).append("transportMode = ").append(transportMode);
		sb.append("\n").append(indent).append("comment = ").append(comment);
		sb.append("\n").append(indent).append("facility = ").append(facility);
		sb.append("\n").append(indent).append("number = ").append(number);
		sb.append("\n").append(indent).append("routeId = ").append(routeId);
		sb.append("\n").append(indent).append("journeyPatternId = ").append(journeyPatternId);
		sb.append("\n").append(indent).append("timeSlotId = ").append(timeSlotId);
		sb.append("\n").append(indent).append("publishedJourneyName = ").append(publishedJourneyName);
		sb.append("\n").append(indent).append("publishedJourneyIdentifier = ").append(publishedJourneyIdentifier);
		sb.append("\n").append(indent).append("vehicleTypeIdentifier = ").append(vehicleTypeIdentifier);
		sb.append("\n").append(indent).append("operatorId = ").append(operatorId);

		if (level > 0)
		{
			int childLevel = level -1;
			String childIndent = indent + CHILD_INDENT;
			
			if (timeSlot != null) 
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(timeSlot.toString(childIndent,0));
			}
			
			childIndent = indent + CHILD_LIST_INDENT;
			if (vehicleJourneyAtStops != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("vehicleJourneyAtStops");
				for (VehicleJourneyAtStop vehicleJourneyAtStop : getVehicleJourneyAtStops())
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(vehicleJourneyAtStop.toString(childIndent,childLevel));
				}
			}
			
		}
		return sb.toString();
	}
	
	public void addVehicleJourneyAtStop(VehicleJourneyAtStop vehicleJourneyAtStop)
	{
		if (vehicleJourneyAtStops== null) vehicleJourneyAtStops = new ArrayList<VehicleJourneyAtStop>();
		vehicleJourneyAtStops.add(vehicleJourneyAtStop);
	}	
	

}
