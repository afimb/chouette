package fr.certu.chouette.service.validation.amivif;

import java.util.ArrayList;
import java.util.List;

public class RespPTLineStructTimetableType {
	
	private TransportNetwork			transportNetwork;													// 0..1
	private List<Company>				companies				= new ArrayList<Company>();					// 1..w
	private List<GroupOfLine>			groupOfLines			= new ArrayList<GroupOfLine>();				// 0..w
	private Line						line;																// 1
	private List<StopArea>				stopAreas				= new ArrayList<StopArea>();				// 0..w
	private List<StopPoint>				stopPoints				= new ArrayList<StopPoint>();				// 2..w
	private List<PTLink>				pTLinks					= new ArrayList<PTLink>();					// 1..w
	private List<Route>					routes					= new ArrayList<Route>();					// 1..w
	private List<SubLine>				subLines				= new ArrayList<SubLine>();					// 0..w
	private List<AccessPoint>			accessPoints			= new ArrayList<AccessPoint>();				// 0..w
	private List<PTAccessLink>  		pTAccessLinks			= new ArrayList<PTAccessLink>();			// 0..w
	private List<StopPointInConnection>	stopPointInConnections	= new ArrayList<StopPointInConnection>();	// 0..w
	private List<ConnectionLink>		connectionLinks			= new ArrayList<ConnectionLink>();			// 0..w
	private List<ICT>					icts					= new ArrayList<ICT>();						// 0..w
	private List<Timetable>				timetables				= new ArrayList<Timetable>();				// 1..w
	private List<JourneyPattern>		journeyPatterns			= new ArrayList<JourneyPattern>();			// 0..w
	private List<VehicleJourney>		vehicleJourneys			= new ArrayList<VehicleJourney>();			// 1..w
	
	public void setTransportNetwork(TransportNetwork transportNetwork) {
		this.transportNetwork = transportNetwork;
	}
	
	public TransportNetwork getTransportNetwork() {
		return transportNetwork;
	}
	
	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}
	
	public List<Company> getCompanies() {
		return companies;
	}
	
	public void addCompany(Company company) {
		companies.add(company);
	}
	
	public void removeCompany(Company company) {
		companies.remove(company);
	}
	
	public void removeCompany(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getCompaniesCount()))
			throw new IndexOutOfBoundsException();
		companies.remove(i);
	}
	
	public Company getCopany(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getCompaniesCount()))
			throw new IndexOutOfBoundsException();
		return (Company)companies.get(i);
	}
	
	public int getCompaniesCount() {
		if (companies == null)
			return 0;
		return companies.size();
	}
	
	public void setGroupOfLines(List<GroupOfLine> groupOfLines) {
		this.groupOfLines = groupOfLines;
	}
	
	public List<GroupOfLine> getGroupOfLines() {
		return groupOfLines;
	}
	
	public void addGroupOfLine(GroupOfLine groupOfLine) {
		groupOfLines.add(groupOfLine);
	}
	
	public void removeGroupOfLine(GroupOfLine groupOfLine) {
		groupOfLines.remove(groupOfLine);
	}
	
	public void removeGroupOfLine(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getGroupOfLinesCount()))
			throw new IndexOutOfBoundsException();
		groupOfLines.remove(i);
	}
	
	public GroupOfLine getGroupOfLine(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getGroupOfLinesCount()))
			throw new IndexOutOfBoundsException();
		return (GroupOfLine)groupOfLines.get(i);
	}
	
	public int getGroupOfLinesCount() {
		if (groupOfLines == null)
			return 0;
		return groupOfLines.size();
	}
	
	public void setLine(Line line) {
		this.line = line;
	}
	
	public Line getLine() {
		return line;
	}
	
	public void setStopAreas(List<StopArea> stopAreas) {
		this.stopAreas = stopAreas;
	}
	
	public List<StopArea> getStopAreas() {
		return stopAreas;
	}
	
	public void addStopArea(StopArea stopArea) {
		stopAreas.add(stopArea);
	}
	
	public void removeStopArea(StopArea stopArea) {
		stopAreas.remove(stopArea);
	}
	
	public void removeStopArea(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopAreasCount()))
			throw new IndexOutOfBoundsException();
		stopAreas.remove(i);
	}
	
	public StopArea getStopArea(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopAreasCount()))
			throw new IndexOutOfBoundsException();
		return (StopArea)stopAreas.get(i);
	}
	
	public int getStopAreasCount() {
		if (stopAreas == null)
			return 0;
		return stopAreas.size();
	}
	
	public void setStopPoints(List<StopPoint> stopPoints) {
		this.stopPoints = stopPoints;
	}
	
	public List<StopPoint> getStopPoints() {
		return stopPoints;
	}
	
	public void addStopPoint(StopPoint stopPoint) {
		stopPoints.add(stopPoint);
	}
	
	public void removeStopPoint(StopPoint stopPoint) {
		stopPoints.remove(stopPoint);
	}
	
	public void removeStopPoint(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopPointsCount()))
			throw new IndexOutOfBoundsException();
		stopPoints.remove(i);
	}
	
	public StopPoint getStopPoint(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopPointsCount()))
			throw new IndexOutOfBoundsException();
		return (StopPoint)stopPoints.get(i);
	}
	
	public int getStopPointsCount() {
		if (stopPoints == null)
			return 0;
		return stopPoints.size();
	}
	
	public void setPTLinks(List<PTLink> pTLinks) {
		this.pTLinks = pTLinks;
	}
	
	public List<PTLink> getPTLinks() {
		return pTLinks;
	}
	
	public void addPTLink(PTLink pTLink) {
		pTLinks.add(pTLink);
	}
	
	public void removePTLink(PTLink pTLink) {
		pTLinks.remove(pTLink);
	}
	
	public void removePTLink(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getPTLinksCount()))
			throw new IndexOutOfBoundsException();
		pTLinks.remove(i);
	}
	
	public PTLink getPTLink(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getPTLinksCount()))
			throw new IndexOutOfBoundsException();
		return (PTLink)pTLinks.get(i);
	}
	
	public int getPTLinksCount() {
		if (pTLinks == null)
			return 0;
		return pTLinks.size();
	}
	
	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
	
	public List<Route> getRoutes() {
		return routes;
	}
	
	public void addRoute(Route route) {
		routes.add(route);
	}
	
	public void removeRoute(Route route) {
		routes.remove(route);
	}
	
	public void removeRoute(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getRoutesCount()))
			throw new IndexOutOfBoundsException();
		routes.remove(i);
	}
	
	public Route getRoute(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getRoutesCount()))
			throw new IndexOutOfBoundsException();
		return (Route)routes.get(i);
	}
	
	public int getRoutesCount() {
		if (routes == null)
			return 0;
		return routes.size();
	}
	
	public void setSubLines(List<SubLine> subLines) {
		this.subLines = subLines;
	}
	
	public List<SubLine> getSubLines() {
		return subLines;
	}
	
	public void addSubLine(SubLine subLine) {
		subLines.add(subLine);
	}
	
	public void removeSubLine(SubLine subLine) {
		subLines.remove(subLine);
	}
	
	public void removeSubLine(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getSubLinesCount()))
			throw new IndexOutOfBoundsException();
		subLines.remove(i);
	}
	
	public SubLine getSubLine(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getSubLinesCount()))
			throw new IndexOutOfBoundsException();
		return (SubLine)subLines.get(i);
	}
	
	public int getSubLinesCount() {
		if (subLines == null)
			return 0;
		return subLines.size();
	}
	
	public void setAccessPoints(List<AccessPoint> accessPoints) {
		this.accessPoints = accessPoints;
	}
	
	public List<AccessPoint> getAccessPoints() {
		return accessPoints;
	}
	
	public void addAccessPoint(AccessPoint accessPoint) {
		accessPoints.add(accessPoint);
	}
	
	public void removeAccessPoint(AccessPoint accessPoint) {
		accessPoints.remove(accessPoint);
	}
	
	public void removeAccessPoint(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getAccessPointsCount()))
			throw new IndexOutOfBoundsException();
		accessPoints.remove(i);
	}
	
	public AccessPoint getAccessPoint(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getAccessPointsCount()))
			throw new IndexOutOfBoundsException();
		return (AccessPoint)accessPoints.get(i);
	}
	
	public int getAccessPointsCount() {
		if (accessPoints == null)
			return 0;
		return accessPoints.size();
	}
	
	public void setPTAccessLinks(List<PTAccessLink> pTAccessLinks) {
		this.pTAccessLinks = pTAccessLinks;
	}
	
	public List<PTAccessLink> getPTAccessLinks() {
		return pTAccessLinks;
	}
	
	public void addPTAccessLink(PTAccessLink pTAccessLink) {
		pTAccessLinks.add(pTAccessLink);
	}
	
	public void removePTAccessLink(PTAccessLink pTAccessLink) {
		pTAccessLinks.remove(pTAccessLink);
	}
	
	public void removePTAccessLink(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getPTAccessLinksCount()))
			throw new IndexOutOfBoundsException();
		pTAccessLinks.remove(i);
	}
	
	public PTAccessLink getPTAccessLink(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getPTAccessLinksCount()))
			throw new IndexOutOfBoundsException();
		return (PTAccessLink)pTAccessLinks.get(i);
	}
	
	public int getPTAccessLinksCount() {
		if (pTAccessLinks == null)
			return 0;
		return pTAccessLinks.size();
	}
	
	public void setStopPointInConnections(List<StopPointInConnection> stopPointInConnections) {
		this.stopPointInConnections = stopPointInConnections;
	}
	
	public List<StopPointInConnection> getStopPointInConnections() {
		return stopPointInConnections;
	}
	
	public void addStopPointInConnection(StopPointInConnection stopPointInConnection) {
		stopPointInConnections.add(stopPointInConnection);
	}
	
	public void removeStopPointInConnection(StopPointInConnection stopPointInConnection) {
		stopPointInConnections.remove(stopPointInConnection);
	}
	
	public void removeStopPointInConnection(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopPointInConnectionsCount()))
			throw new IndexOutOfBoundsException();
		stopPointInConnections.remove(i);
	}
	
	public StopPointInConnection getStopPointInConnection(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getStopPointInConnectionsCount()))
			throw new IndexOutOfBoundsException();
		return (StopPointInConnection)stopPointInConnections.get(i);
	}
	
	public int getStopPointInConnectionsCount() {
		if (stopPointInConnections == null)
			return 0;
		return stopPointInConnections.size();
	}
	
	public void setConnectionLinks(List<ConnectionLink> connectionLinks) {
		this.connectionLinks = connectionLinks;
	}
	
	public List<ConnectionLink> getConnectionLinks() {
		return connectionLinks;
	}
	
	public void addConnectionLink(ConnectionLink connectionLink) {
		connectionLinks.add(connectionLink);
	}
	
	public void removeConnectionLink(ConnectionLink connectionLink) {
		connectionLinks.remove(connectionLink);
	}
	
	public void removeConnectionLink(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getConnectionLinksCount()))
			throw new IndexOutOfBoundsException();
		connectionLinks.remove(i);
	}
	
	public ConnectionLink getConnectionLink(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getConnectionLinksCount()))
			throw new IndexOutOfBoundsException();
		return (ConnectionLink)connectionLinks.get(i);
	}
	
	public int getConnectionLinksCount() {
		if (connectionLinks == null)
			return 0;
		return connectionLinks.size();
	}
	
	public void setICTs(List<ICT> icts) {
		this.icts = icts;
	}
	
	public List<ICT> getICTs() {
		return icts;
	}
	
	public void addICT(ICT ict) {
		icts.add(ict);
	}
	
	public void removeICT(ICT ict) {
		icts.remove(ict);
	}
	
	public void removeICT(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getICTsCount()))
			throw new IndexOutOfBoundsException();
		icts.remove(i);
	}
	
	public ICT getICT(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getICTsCount()))
			throw new IndexOutOfBoundsException();
		return (ICT)icts.get(i);
	}
	
	public int getICTsCount() {
		if (icts == null)
			return 0;
		return icts.size();
	}
	
	public void setTimetables(List<Timetable> timetables) {
		this.timetables = timetables;
	}
	
	public List<Timetable> getTimetables() {
		return timetables;
	}
	
	public void addTimetable(Timetable timetable) {
		timetables.add(timetable);
	}
	
	public void removeTimetable(Timetable timetable) {
		timetables.remove(timetable);
	}
	
	public void removeTimetable(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getTimetablesCount()))
			throw new IndexOutOfBoundsException();
		timetables.remove(i);
	}
	
	public Timetable getTimetable(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getTimetablesCount()))
			throw new IndexOutOfBoundsException();
		return (Timetable)timetables.get(i);
	}
	
	public int getTimetablesCount() {
		if (timetables == null)
			return 0;
		return timetables.size();
	}
	
	public void setJourneyPatterns(List<JourneyPattern> journeyPatterns) {
		this.journeyPatterns = journeyPatterns;
	}
	
	public List<JourneyPattern> getJourneyPatterns() {
		return journeyPatterns;
	}
	
	public void addJourneyPattern(JourneyPattern journeyPattern) {
		journeyPatterns.add(journeyPattern);
	}
	
	public void removeJourneyPattern(JourneyPattern journeyPattern) {
		journeyPatterns.remove(journeyPattern);
	}
	
	public void removeJourneyPattern(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getJourneyPatternsCount()))
			throw new IndexOutOfBoundsException();
		journeyPatterns.remove(i);
	}
	
	public JourneyPattern getJourneyPattern(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getJourneyPatternsCount()))
			throw new IndexOutOfBoundsException();
		return (JourneyPattern)journeyPatterns.get(i);
	}
	
	public int getJourneyPatternsCount() {
		if (journeyPatterns == null)
			return 0;
		return journeyPatterns.size();
	}
	
	public void setVehicleJourneys(List<VehicleJourney> vehicleJourneys) {
		this.vehicleJourneys = vehicleJourneys;
	}
	
	public List<VehicleJourney> getVehicleJourneys() {
		return vehicleJourneys;
	}
	
	public void addVehicleJourney(VehicleJourney vehicleJourney) {
		vehicleJourneys.add(vehicleJourney);
	}
	
	public void removeVehicleJourney(VehicleJourney vehicleJourney) {
		vehicleJourneys.remove(vehicleJourney);
	}
	
	public void removeVehicleJourney(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getVehicleJourneysCount()))
			throw new IndexOutOfBoundsException();
		vehicleJourneys.remove(i);
	}

	public VehicleJourney getVehicleJourney(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getVehicleJourneysCount()))
			throw new IndexOutOfBoundsException();
		return (VehicleJourney)vehicleJourneys.get(i);
	}
	
	public int getVehicleJourneysCount() {
		if (vehicleJourneys == null)
			return 0;
		return vehicleJourneys.size();
	}
}
