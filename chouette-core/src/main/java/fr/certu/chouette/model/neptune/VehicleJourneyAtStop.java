package fr.certu.chouette.model.neptune;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.model.neptune.type.BoardingAlightingPossibilityEnum;

public class VehicleJourneyAtStop extends NeptuneObject {
	@Getter @Setter private String stopPointId;
	@Getter @Setter private StopPoint stopPoint;
	@Getter @Setter private String vehicleJourneyId;
	@Getter @Setter private VehicleJourney vehicleJourney;
	@Getter @Setter private String connectingServiceId;
	@Getter @Setter private BoardingAlightingPossibilityEnum boardingAlightingPossibility;
	@Getter @Setter private long order;
	@Getter @Setter private Date arrivalTime;
	@Getter @Setter private Date departureTime;
	@Getter @Setter private Date waitingTime;
	@Getter @Setter private Date elapseDuration;
	@Getter @Setter private Date headwayFrequency;
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.model.neptune.NeptuneBean#expand(fr.certu.chouette.manager.NeptuneBeanManager.DETAIL_LEVEL)
	 */
	@Override
	public void expand(DetailLevelEnum level)
	{
		// to avoid circular call check if level is already set according to this level
		if (getLevel().ordinal() >= level.ordinal()) return;
		super.expand(level);
		switch (level)
		{
		case ATTRIBUTE : 
			stopPoint = null;
			vehicleJourney = null;
			break;
		case NARROW_DEPENDENCIES : 
			if (getStopPoint() != null) getStopPoint().expand(DetailLevelEnum.ATTRIBUTE);
			if (getVehicleJourney() != null) getVehicleJourney().expand(DetailLevelEnum.ATTRIBUTE);
			break;
		case STRUCTURAL_DEPENDENCIES : 
		case ALL_DEPENDENCIES :
			if (getStopPoint() != null) getStopPoint().expand(level);
			if (getVehicleJourney() != null) getVehicleJourney().expand(level);
		}
	} 
	
	@Override
	public String toString(String indent, int level) {
		StringBuilder sb = new StringBuilder(super.toString(indent,level));
		sb.append("\n").append(indent).append("stopPointId = ").append(stopPointId);
		sb.append("\n").append(indent).append("vehicleJourneyId = ").append(vehicleJourneyId);
		sb.append("\n").append(indent).append("connectingServiceId = ").append(connectingServiceId);
		sb.append("\n").append(indent).append("boardingAlightingPossibility = ").append(boardingAlightingPossibility);
		sb.append("\n").append(indent).append("order = ").append(order);
		sb.append("\n").append(indent).append("arrivalTime = ").append(formatDate(arrivalTime));
		sb.append("\n").append(indent).append("departureTime = ").append(formatDate(departureTime));
		sb.append("\n").append(indent).append("waitingTime = ").append(formatDate(waitingTime));
		sb.append("\n").append(indent).append("elapseDuration = ").append(formatDate(elapseDuration));
		sb.append("\n").append(indent).append("headwayFrequency = ").append(formatDate(headwayFrequency));
		
		return sb.toString();
	}
	
	private String formatDate(Date date){
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		if(date != null){
			return dateFormat.format(date);
		}
		else{
			return null;
		}
	}
}
