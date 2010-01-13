package fr.certu.chouette.service.validation;

import org.exolab.castor.types.Duration;
import org.exolab.castor.types.Time;

public class VehicleJourneyAtStop {
	
	private VehicleJourney					vehicleJourney;
	private String							stopPointId;
	private StopPoint						stopPoint;
	private String 							vehicleJourneyId;
	private String 							connectingServiceId;
	private Time							arrivalTime;
	private Time							departureTime;
	private Time							waitingTime;
	private Duration						elapseDuration;
	private Duration						headwayFrequency;
	private BoardingAlightingPossibility	boardingAlightingPossibility;
	private int								order;
	private boolean							hasOrder 						= false;
	
	public void setVehicleJourney(VehicleJourney vehicleJourney) {
		this.vehicleJourney = vehicleJourney;
	}
	
	public VehicleJourney getVehicleJourney() {
		return vehicleJourney;
	}
	
	public void setStopPointId(String stopPointId) {
		this.stopPointId = stopPointId;
	}
	
	public String getStopPointId() {
		return stopPointId;
	}
	
	public void setStopPoint(StopPoint stopPoint) {
		this.stopPoint = stopPoint;
	}
	
	public StopPoint getStopPoint() {
		return stopPoint;
	}
	
	public void setVehicleJourneyId(String vehicleJourneyId) {
		this.vehicleJourneyId = vehicleJourneyId;
	}
	
	public String getVehicleJourneyId() {
		return vehicleJourneyId;
	}
	
	public void setConnectingServiceId(String connectingServiceId) {
		this.connectingServiceId = connectingServiceId;
	}
	
	public String getConnectingServiceId() {
		return connectingServiceId;
	}
	
	public void setArrivalTime(Time arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	
	public Time getArrivalTime() {
		return arrivalTime;
	}
	
	public void setDepartureTime(Time departureTime) {
		this.departureTime = departureTime;
	}
	
	public Time getDepartureTime() {
		return departureTime;
	}
	
	public void setWaitingTime(Time waitingTime) {
		this.waitingTime = waitingTime;
	}
	
	public Time getWaitingTime() {
		return waitingTime;
	}
	
	public void setElapseDuration(Duration elapseDuration) {
		this.elapseDuration = elapseDuration;
	}
	
	public Duration getElapseDuration() {
		return elapseDuration;
	}
	
	public void setHeadwayFrequency(Duration headwayFrequency) {
		this.headwayFrequency = headwayFrequency;
	}
	
	public Duration getHeadwayFrequency() {
		return headwayFrequency;
	}
	
	public void setBoardingAlightingPossibility(BoardingAlightingPossibility boardingAlightingPossibility) {
		this.boardingAlightingPossibility = boardingAlightingPossibility;
	}
	
	public BoardingAlightingPossibility getBoardingAlightingPossibility() {
		return boardingAlightingPossibility;
	}
	
	public void setOrder(int order) {
		this.order = order;
		if (order >= 0)
			hasOrder = true;
		else
			hasOrder = false;
	}
	
	public int getOrder() {
		return order;
	}
	
	public boolean hasOrder() {
		return hasOrder;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<VehicleJourneyAtStop>\n");
		stb.append("<StopPointId>"+stopPointId+"</StopPointId>\n");
		stb.append("<VehicleJourneyId>"+vehicleJourney.getObjectId()+"</VehicleJourneyId>\n");
		if (connectingServiceId != null)
			stb.append("<ConnectingServiceId>"+connectingServiceId+"</ConnectingServiceId>\n");
		if (arrivalTime != null)
			stb.append("<ArrivalTime>"+arrivalTime+"</ArrivalTime>\n");
		stb.append("<DepartureTime>"+departureTime+"</DepartureTime>\n");
		if (waitingTime != null)
			stb.append("<WaitingTime>"+waitingTime+"</WaitingTime>\n");
		stb.append("<ElapseDuration>"+elapseDuration+"</ElapseDuration>\n");
		if (headwayFrequency != null)
			stb.append("<HeadwayFrequency>"+headwayFrequency+"</HeadwayFrequency>\n");
		if (boardingAlightingPossibility != null)
			stb.append("<BoardingAlightingPossibility>"+boardingAlightingPossibility+"</BoardingAlightingPossibility>\n");
		if (hasOrder)
			stb.append("<Order>"+order+"</Order>\n");
		stb.append("</VehicleJourneyAtStop>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<VehicleJourneyAtStop>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<StopPointId>"+stopPointId+"</StopPointId>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<VehicleJourneyId>"+vehicleJourney.getObjectId()+"</VehicleJourneyId>\n");
		if (connectingServiceId != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<ConnectingServiceId>"+connectingServiceId+"</ConnectingServiceId>\n");
		}
		if (arrivalTime != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<ArrivalTime>"+arrivalTime+"</ArrivalTime>\n");
		}
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<DepartureTime>"+departureTime+"</DepartureTime>\n");
		if (waitingTime != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<WaitingTime>"+waitingTime+"</WaitingTime>\n");
		}
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<ElapseDuration>"+elapseDuration+"</ElapseDuration>\n");
		if (headwayFrequency != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<HeadwayFrequency>"+headwayFrequency+"</HeadwayFrequency>\n");
		}
		if (boardingAlightingPossibility != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<BoardingAlightingPossibility>"+boardingAlightingPossibility+"</BoardingAlightingPossibility>\n");
		}
		if (hasOrder) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<Order>"+order+"</Order>\n");
		}
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</VehicleJourneyAtStop>\n");
		return stb.toString();
	}
}
