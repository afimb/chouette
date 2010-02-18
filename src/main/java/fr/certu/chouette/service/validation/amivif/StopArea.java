package fr.certu.chouette.service.validation.amivif;

import java.util.ArrayList;
import java.util.List;

public class StopArea extends LocationTridentObject {
	
	private String								name;																				// 0..1
	private List<String>						contains						= new ArrayList<String>();							// 1..w
	private List<StopPoint>						stopPoints						= new ArrayList<StopPoint>();						// 1..w
	private List<AccessPoint>					accessPoints					= new ArrayList<AccessPoint>();						// 1..w
	private List<StopPointInConnection>			stopPointInConnections			= new ArrayList<StopPointInConnection>();			// 1..w
	private List<String>						boundaryPointIds				= new ArrayList<String>();							// 0..w
	//private List<XXXXX>							boundaryPoints					= new ArrayList<XXXXX>();							// 0..w Le type XXXXX ne fait pas partie du modèle
	private String								centroidOfAreaId;																	// 0..1
	//private YYYYY								centroidOfArea;																		// 0..1 Le type YYYYY ne fait pas partie du modèle
	private String								comment;																			// 0..1
	private String								nearestTopicName;																	// 0..1
	private	int									upFareCode;																			// 1
	private int									downFareCode;																		// 0..1
	private ProjectedPoint						projectedPoint;																		// 0..1
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setContains(List<String> contains) {
		this.contains = contains;
	}
	
	public List<String> getContains() {
		return contains;
	}
	
	public void addContain(String contain) {
		contains.add(contain);
	}
	
	public void removeContain(String contain) {
		contains.remove(contain);
	}
	
	public void removeContain(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getContainsCount()))
			throw new IndexOutOfBoundsException();
		contains.remove(i);
	}
	
	public String getContain(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getContainsCount()))
			throw new IndexOutOfBoundsException();
		return (String)contains.get(i);
	}
	
	public int getContainsCount() {
		if (contains == null)
			return 0;
		return contains.size();
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
	
	public void setBoundaryPointIds(List<String> boundaryPointIds) {
		this.boundaryPointIds = boundaryPointIds;
	}
	
	public List<String> getBoundaryPointIds() {
		return boundaryPointIds;
	}
	
	public void addBoundaryPointId(String boundaryPointId) {
		boundaryPointIds.add(boundaryPointId);
	}
	
	public void removeBoundaryPointId(String boundaryPointId) {
		boundaryPointIds.remove(boundaryPointId);
	}
	
	public void removeBoundaryPointId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getBoundaryPointIdsCount()))
			throw new IndexOutOfBoundsException();
		boundaryPointIds.remove(i);
	}
	
	public String getBoundaryPointId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getBoundaryPointIdsCount()))
			throw new IndexOutOfBoundsException();
		return (String)boundaryPointIds.get(i);
	}
	
	public int getBoundaryPointIdsCount() {
		if (boundaryPointIds == null)
			return 0;
		return boundaryPointIds.size();
	}
	
	public void setCentroidOfAreaId(String centroidOfAreaId) {
		this.centroidOfAreaId = centroidOfAreaId;
	}
	
	public String getCentroidOfAreaId() {
		return centroidOfAreaId;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setNearestTopicName(String nearestTopicName) {
		this.nearestTopicName = nearestTopicName;
	}
	
	public String getNearestTopicName() {
		return nearestTopicName;
	}
	
	public void setUpFareCode(int upFareCode) {
		this.upFareCode = upFareCode;
	}
	
	public int getUpFareCode() {
		return upFareCode;
	}
	
	public void setDownFareCode(int downFareCode) {
		this.downFareCode = downFareCode;
	}
	
	public int getDownFareCode() {
		return downFareCode;
	}
	
	public void setProjectedPoint(ProjectedPoint projectedPoint) {
		this.projectedPoint = projectedPoint;
	}
	
	public ProjectedPoint getProjectedPoint() {
		return projectedPoint;
	}
}
