package fr.certu.chouette.modele;

import java.util.Calendar;
import java.util.Date;

import org.exolab.castor.types.Time;

import chouette.schema.VehicleJourneyAtStop;
import chouette.schema.VehicleJourneyAtStopTypeChoice;
import chouette.schema.VehicleJourneyAtStopTypeChoiceSequence;
import chouette.schema.types.BoardingAlightingPossibilityType;

public class Horaire extends BaseObjet
{
	private VehicleJourneyAtStop vehicleJourneyAtStop;
	
	private Long idCourse;
	private Long idArret;
	private boolean isModifie;
	private boolean isDepart;
	
	private Date departureTime;
	private Date arrivalTime;
	
	public Horaire() {
		super();
		initialise();
	}
	
	private void initialise()
	{
		vehicleJourneyAtStop = new VehicleJourneyAtStop();
		final VehicleJourneyAtStopTypeChoice choice = new VehicleJourneyAtStopTypeChoice();
		choice.setVehicleJourneyAtStopTypeChoiceSequence( new VehicleJourneyAtStopTypeChoiceSequence());
		vehicleJourneyAtStop.setVehicleJourneyAtStopTypeChoice( choice);
	}
	
	private VehicleJourneyAtStopTypeChoiceSequence getChoiceSequence()
	{
		return vehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence();
	}

	public BoardingAlightingPossibilityType getBoardingAlightingPossibility() {
		return vehicleJourneyAtStop.getBoardingAlightingPossibility();
	}

	public String getConnectingServiceId() {
		return vehicleJourneyAtStop.getConnectingServiceId();
	}

	public void setBoardingAlightingPossibility(BoardingAlightingPossibilityType boardingAlightingPossibility) {
		vehicleJourneyAtStop.setBoardingAlightingPossibility(boardingAlightingPossibility);
	}

	public void setConnectingServiceId(String connectingServiceId) {
		vehicleJourneyAtStop.setConnectingServiceId(connectingServiceId);
	}

	public String getStopPointId() {
		return vehicleJourneyAtStop.getStopPointId();
	}

	public String getVehicleJourneyId() {
		return vehicleJourneyAtStop.getVehicleJourneyId();
	}

	public void setStopPointId(String stopPointId) {
		vehicleJourneyAtStop.setStopPointId(stopPointId);
	}

	public void setVehicleJourneyId(String vehicleJourneyId) {
		vehicleJourneyAtStop.setVehicleJourneyId(vehicleJourneyId);
	}

	public Date getArrivalTime() {
		if (arrivalTime != null)
			return arrivalTime;
		if (getChoiceSequence().getArrivalTime() == null)
			return null;
		return getChoiceSequence().getArrivalTime().toDate();
	}

	public Date getDepartureTime() {
		if (departureTime != null)
			return departureTime;
		if (getChoiceSequence().getDepartureTime() == null)
			return null;
		return getChoiceSequence().getDepartureTime().toDate();
	}

	public Date getWaitingTime() {
		if ( getChoiceSequence().getWaitingTime()==null) return null;
		return getChoiceSequence().getWaitingTime().toDate();
	}

	public void setArrivalTime(Date arrivalTime) {
		setArrivalTime2(arrivalTime);
		getChoiceSequence().setArrivalTime(toCastorTime(arrivalTime));
	}

	public void setDepartureTime(Date departureTime) {
		setDepartureTime2(departureTime);
		getChoiceSequence().setDepartureTime(toCastorTime(departureTime));
	}
	
	public void setArrivalTime2(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public void setDepartureTime2(Date departureTime) {
		this.departureTime = departureTime;
	}

	public void setWaitingTime(Date waitingTime) {
		getChoiceSequence().setWaitingTime( toCastorTime( waitingTime));
	}
	
	private Time toCastorTime( Date waitingTime)
	{
		if ( waitingTime==null) return null;
		
		short[] aTimeVal = new short[ 4];
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime( waitingTime);
		
		aTimeVal[ 0] = ( short)aCalendar.get( Calendar.HOUR_OF_DAY);
		aTimeVal[ 1] = ( short)aCalendar.get( Calendar.MINUTE);
		aTimeVal[ 2] = ( short)aCalendar.get( Calendar.SECOND);
		aTimeVal[ 3] = ( short)aCalendar.get( Calendar.MILLISECOND);
		
		return new Time(aTimeVal);
	}

	public Long getIdArret() {
		return idArret;
	}

	public void setIdArret(final Long idArret) {
		this.idArret = idArret;
	}

	public boolean isModifie() {
		return isModifie;
	}

	public void setModifie(final boolean isModifie) {
		this.isModifie = isModifie;
	}

	public boolean getDepart() {
		return isDepart;
	}

	public void setDepart(boolean isDepart) {
		this.isDepart = isDepart;
	}

	public Long getIdCourse() {
		return idCourse;
	}

	public void setIdCourse(final Long idCourse) {
		this.idCourse = idCourse;
	}

	public VehicleJourneyAtStop getVehicleJourneyAtStop() {
		return vehicleJourneyAtStop;
	}

	public void setVehicleJourneyAtStop(final VehicleJourneyAtStop vehicleJourneyAtStop) {
		if ( vehicleJourneyAtStop==null)
		{
			initialise();
		}
		else
		{
			this.vehicleJourneyAtStop = vehicleJourneyAtStop;
			
			if ( vehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice()==null)
			{
				final VehicleJourneyAtStopTypeChoice choice = new VehicleJourneyAtStopTypeChoice();
				choice.setVehicleJourneyAtStopTypeChoiceSequence( new VehicleJourneyAtStopTypeChoiceSequence());
				this.vehicleJourneyAtStop.setVehicleJourneyAtStopTypeChoice( choice);
			}
			else if ( vehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice()!=null
				&& vehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence()==null)
			{
				this.vehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().setVehicleJourneyAtStopTypeChoiceSequence( new VehicleJourneyAtStopTypeChoiceSequence());
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Horaire other = (Horaire) obj;
		if (idArret == null) {
			if (other.idArret != null)
				return false;
		} else if (!idArret.equals(other.idArret))
			return false;
		if (idCourse == null) {
			if (other.idCourse != null)
				return false;
		} else if (!idCourse.equals(other.idCourse))
			return false;
		if (isDepart != other.isDepart)
			return false;
		if (this.getArrivalTime() == null) {
			if (other.getArrivalTime() != null)
				return false;
		} else if (!this.getArrivalTime().equals(other.getArrivalTime()))
			return false;
		if (this.getDepartureTime() == null) {
			if (other.getDepartureTime() != null)
				return false;
		} else if (!this.getDepartureTime().equals(other.getDepartureTime()))
			return false;
		return true;
	}
	
	
	
}
