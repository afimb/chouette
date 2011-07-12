package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.type.ServiceStatusValueEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;

public class VehicleJourney extends NeptuneIdentifiedObject
{
	private static final long serialVersionUID = 304336286208135064L;
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
	@Getter @Setter private String companyId;
	@Getter @Setter private Company company;
	@Getter @Setter private String lineIdShortcut;
	@Getter @Setter private Line line;
	@Getter @Setter private List<VehicleJourneyAtStop> vehicleJourneyAtStops;
	@Getter @Setter private List<Timetable> timetables;

	//	/* (non-Javadoc)
	//	 * @see fr.certu.chouette.model.neptune.NeptuneBean#expand(fr.certu.chouette.manager.NeptuneBeanManager.DETAIL_LEVEL)
	//	 */
	//	@Override
	//	public void expand(DetailLevelEnum level)
	//	{
	//		// to avoid circular call check if level is already set according to this level
	//		if (getLevel().ordinal() >= level.ordinal()) return;
	//		super.expand(level);
	//		switch (level)
	//		{
	//		case ATTRIBUTE : 
	//			route = null;
	//			journeyPattern = null;
	//			timeSlot = null;
	//			vehicleJourneyAtStops = null;
	//			timetables = null;
	//			break;
	//		case NARROW_DEPENDENCIES : 
	//			if (getRoute() != null) getRoute().expand(DetailLevelEnum.ATTRIBUTE);
	//			if (getJourneyPattern() != null) getJourneyPattern().expand(DetailLevelEnum.ATTRIBUTE);
	//			if (getTimeSlot() != null) getTimeSlot().expand(DetailLevelEnum.ATTRIBUTE);
	//			if (getVehicleJourneyAtStops() != null)
	//			{
	//				for (VehicleJourneyAtStop vehicleJourneyAtStop : getVehicleJourneyAtStops())
	//				{
	//					vehicleJourneyAtStop.expand(DetailLevelEnum.ATTRIBUTE);
	//				}
	//			}
	//			if (getTimetables() != null)
	//			{
	//				for (Timetable timetable : getTimetables())
	//				{
	//					timetable.expand(DetailLevelEnum.ATTRIBUTE);
	//				}
	//			}
	//			break;
	//		case STRUCTURAL_DEPENDENCIES : 
	//		case ALL_DEPENDENCIES :
	//			if (getRoute() != null) getRoute().expand(DetailLevelEnum.ATTRIBUTE);
	//			if (getJourneyPattern() != null) getJourneyPattern().expand(DetailLevelEnum.ATTRIBUTE);
	//			if (getTimeSlot() != null) getTimeSlot().expand(level);
	//			if (getVehicleJourneyAtStops() != null)
	//			{
	//				for (VehicleJourneyAtStop vehicleJourneyAtStop : getVehicleJourneyAtStops())
	//				{
	//					vehicleJourneyAtStop.expand(level);
	//				}
	//			}
	//			if (getTimetables() != null)
	//			{
	//				for (Timetable timetable : getTimetables())
	//				{
	//					timetable.expand(level);
	//				}
	//			}
	//			break;
	//		}
	//	}

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
		sb.append("\n").append(indent).append("companyId = ").append(companyId);

		int childLevel = level -1;

		String childIndent = indent + CHILD_LIST_INDENT;
		if (vehicleJourneyAtStops != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("vehicleJourneyAtStops");
			for (VehicleJourneyAtStop vehicleJourneyAtStop : getVehicleJourneyAtStops())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(vehicleJourneyAtStop.toString(childIndent,childLevel));
			}
		}
		if (level > 0)
		{
			childIndent = indent + CHILD_INDENT;
			if (timeSlot != null) 
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(timeSlot.toString(childIndent,0));
			}

			if (company != null) 
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(company.toString(childIndent,0));
			}
			childIndent = indent + CHILD_LIST_INDENT;
			if (timetables != null)
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append("timetables");
				for (Timetable timetable : getTimetables())
				{
					sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(timetable.toString(childIndent,0));
				}
			}
		}
		return sb.toString();
	}

	public void addVehicleJourneyAtStop(VehicleJourneyAtStop vehicleJourneyAtStop)
	{
		if (vehicleJourneyAtStops== null) 
			vehicleJourneyAtStops = new ArrayList<VehicleJourneyAtStop>();
		if (vehicleJourneyAtStop != null && !vehicleJourneyAtStops.contains(vehicleJourneyAtStop)) 
			vehicleJourneyAtStops.add(vehicleJourneyAtStop);
	}	

	public void addTimetable(Timetable timetable)
	{
		if (timetables== null) timetables = new ArrayList<Timetable>();
		if (timetable != null && !timetables.contains(timetable))
			timetables.add(timetable);
	}

	public List<VehicleJourney> getVehicleJourneysByRoute(String routeId){
		List<VehicleJourney> res = new ArrayList<VehicleJourney>();
		if(this.routeId.equals(routeId))
			res.add(this);
		return res;
	}

	@Override
	public boolean clean() {
		if(vehicleJourneyAtStops == null || vehicleJourneyAtStops.isEmpty()){
			return false;
		}
		if(timetables == null || timetables.isEmpty()){
			return false;
		}
		return true;
	}
	
	public void removeStopPoint(StopPoint stopPoint)
	{
		if (getVehicleJourneyAtStops() != null)
		{
			List<VehicleJourneyAtStop> vjas = getVehicleJourneyAtStops();
			for (Iterator<VehicleJourneyAtStop> iterator = vjas.iterator(); iterator.hasNext();)
			{
				VehicleJourneyAtStop vehicleJourneyAtStop =  iterator.next();
				if (stopPoint.equals(vehicleJourneyAtStop.getStopPoint()))
				{
					vehicleJourneyAtStop.setStopPoint(null);
					iterator.remove();
					break;
				}
				
			}
			sortVehicleJourneyAtStops();
		}
	}

	public void sortVehicleJourneyAtStops() 
	{
		if (getVehicleJourneyAtStops() != null)
		{
			List<VehicleJourneyAtStop> vjass = getVehicleJourneyAtStops();
			Collections.sort(vjass, new VehicleJourneyAtStopComparator());
			int last = vjass.size()-1;
			for (int i = 0; i < vjass.size(); i ++) 
			{
				VehicleJourneyAtStop vjas = vjass.get(i);
				vjas.setDeparture(i==0);
				vjas.setOrder(i+1);
				vjas.setArrival(i==last);
			}
			
		}
		
	}
	
	private class VehicleJourneyAtStopComparator implements Comparator<VehicleJourneyAtStop>
	{

		@Override
		public int compare(VehicleJourneyAtStop o1, VehicleJourneyAtStop o2) 
		{
			StopPoint point1 = o1.getStopPoint();
			StopPoint point2 = o2.getStopPoint();
			if (point1 != null && point2 != null)
			{
				return point1.getPosition() - point2.getPosition();
			}
			return 0;
		}
		
	}
	
}
