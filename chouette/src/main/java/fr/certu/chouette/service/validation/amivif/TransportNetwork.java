package fr.certu.chouette.service.validation.amivif;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransportNetwork extends TridentObject {
	
	private Date								versionDate;																		// 1
	private String								description;																		// 0..1
	private String								name;																				// 1
	private Registration						registration;																		// 0..1
	private String								sourceName;																			// 0..1
	private String								sourceIdentifier;																	// 0..1
	private SourceType							sourceType;																			// 0..1
	private List<String>						lineIds							= new ArrayList<String>();							// 0..w
	private List<Line>							lines							= new ArrayList<Line>();							// 0..w
	private String								comment;																			// 0..1
	
	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}
	
	public Date getVersionDate() {
		return versionDate;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setRegistration(Registration registration) {
		this.registration = registration;
	}
	
	public Registration getRegistration() {
		return registration;
	}
	
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	
	public String getSourceName() {
		return sourceName;
	}
	
	public void setSourceIdentifier(String sourceIdentifier) {
		this.sourceIdentifier = sourceIdentifier;
	}
	
	public String getSourceIdentifier() {
		return sourceIdentifier;
	}
	
	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}
	
	public SourceType getSourceType() {
		return sourceType;
	}
	
	public void setLineIds(List<String> lineIds) {
		this.lineIds = lineIds;
	}
	
	public List<String> getLineIds() {
		return lineIds;
	}
	
	public void addLineId(String lineId) {
		lineIds.add(lineId);
	}
	
	public void removeLineId(String lineId) {
		lineIds.remove(lineId);
	}
	
	public void removeLineId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLineIdsCount()))
			throw new IndexOutOfBoundsException();
		lineIds.remove(i);
	}
	
	public String getLineId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLineIdsCount()))
			throw new IndexOutOfBoundsException();
		return (String)lineIds.get(i);
	}
	
	public int getLineIdsCount() {
		if (lineIds == null)
			return 0;
		return lineIds.size();
	}
	
	public void setLines(List<Line> lines) {
		this.lines = lines;
	}
	
	public List<Line> getLines() {
		return lines;
	}
	
	public void addLine(Line line) {
		lines.add(line);
	}
	
	public void removeLine(Line line) {
		lines.remove(line);
	}
	
	public void removeLine(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLinesCount()))
			throw new IndexOutOfBoundsException();
		lines.remove(i);
	}
	
	public Line getLine(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLinesCount()))
			throw new IndexOutOfBoundsException();
		return (Line)lines.get(i);
	}
	
	public int getLinesCount() {
		if (lines == null)
			return 0;
		return lines.size();
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}

	public enum SourceType {
        AutomobileClubPatrol,
        SpotterAircraft,
        BreakdownService,
        CameraObservation,
        EmergencyServicePatrol,
        FreightVehicleOperator,
        InfraredMonitoringStation,
        InductionLoopMonitoringStation,
        MicrowaveMonitoringStation,
        MobileTelephoneCaller,
        OtherInformation,
        OtherOfficialVehicle,
        PolicePatrol,
        PublicAndPrivateUtilities,
        RoadAuthorities,
        RegisteredMotoristObserver,
        RoadsideTelephoneCaller,
        TrafficMonitoringStation,
        TransitOperator,
        VideoProcessingMonitoringStation,
        VehicleProbeMeasurement,
        PublicTransport,
        PassengerTransportCoordinatingAuthority,
        TravelInformationServiceProvider,
        TravelAgency,
        IndividualSubjectOfTravelItinerary
	}
}
