package fr.certu.chouette.service.validation.amivif;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Point extends LocationTridentObject {
	
	private BigDecimal			longitude;										// 1 BETWEEN -180 AND 180 
	private BigDecimal			latitude;										// 1 BETWEEN _90 AND 90
	private LongLatType			longLatType;									// 1
	private String				languageCode;									// 0..1
	private Address				address;										// 0..1
	private PointOfInterest		pointOfInterest;								// 0..1
	private ProjectedPoint		projectedPoint;									// 0..1
	private List<String>		containedIns	= new ArrayList<String>();		// 0..w
	private List<StopArea>		stopAreas		= new ArrayList<StopArea>();	// 0..w
	
	public void setPoint(Point point) {
		super.setLocationTridentObject(point);
		this.setLongitude(point.getLongitude());
		this.setLatitude(point.getLatitude());
		this.setLongLatType(point.getLongLatType());
		this.setLanguageCode(point.getLanguageCode());
		this.setAddress(point.getAddress());
		this.setPointOfInterest(point.getPointOfInterest());
		this.setProjectedPoint(point.getProjectedPoint());
		this.setContainedIns(point.getContainedIns());
		this.setStopAreas(point.getStopAreas());
	}
	
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude; 
	}
	
	public BigDecimal getLongitude() {
		return longitude;
	}
	
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude; 
	}
	
	public BigDecimal getLatitude() {
		return latitude;
	}
	
	public void setLongLatType(LongLatType longLatType) {
		this.longLatType = longLatType; 
	}
	
	public LongLatType getLongLatType() {
		return longLatType;
	}
	
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	
	public String getLanguageCode() {
		return languageCode;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void setPointOfInterest(PointOfInterest pointOfInterest) {
		this.pointOfInterest = pointOfInterest;
	}
	
	public PointOfInterest getPointOfInterest() {
		return pointOfInterest;
	}
	
	public void setProjectedPoint(ProjectedPoint projectedPoint) {
		this.projectedPoint = projectedPoint;
	}
	
	public ProjectedPoint getProjectedPoint() {
		return projectedPoint;
	}
	
	public void setContainedIns(List<String> containedIns) {
		this.containedIns = containedIns;
	}
	
	public List<String> getContainedIns() {
		return containedIns;
	}
	
	public void addContainedIn(String containedIn) {
		containedIns.add(containedIn);
	}
	
	public void removeContainedIn(String containedIn) {
		containedIns.remove(containedIn);
	}
	
	public void removeContainedIn(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getContainedInsCount()))
			throw new IndexOutOfBoundsException();
		containedIns.remove(i);
	}
	
	public String getContainedIn(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getContainedInsCount()))
			throw new IndexOutOfBoundsException();
		return (String)containedIns.get(i);
	}
	
	public int getContainedInsCount() {
		if (containedIns == null)
			return 0;
		return containedIns.size();
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
	
	public enum LongLatType {
		WGS84,
		WGS92,
		Standard
	}
	
	public class Address {
		
		private String	streetName;		// 0..1
		private String	countryCode;	// 0..1
		private String	province;		// 0..1
		private String	region;			// 0..1
		private String	town;			// 0..1
		private String	roadNumber;		// 0..1
		private String	houseNumber;	// 0..1
		private String	postalCode;		// 1
		
		public void setStreetName(String streetName) {
			this.streetName = streetName;
		}
		
		public String getStreetName() {
			return streetName;
		}
		
		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}
		
		public String getCountryCode() {
			return countryCode;
		}
		
		public void setProvince(String province) {
			this.province = province;
		}
		
		public String getProvince() {
			return province;
		}
		
		public void setRegion(String region) {
			this.region = region;
		}
		
		public String getRegion() {
			return region;
		}
		
		public void setTown(String town) {
			this.town = town;
		}
		
		public String getTown() {
			return town;
		}
		
		public void setRoadNumber(String roadNumber) {
			this.roadNumber = roadNumber;
		}
		
		public String getRoadNumber() {
			return roadNumber;
		}
		
		public void setHouseNumber(String houseNumber) {
			this.houseNumber = houseNumber;
		}
		
		public String getHouseNumber() {
			return houseNumber;
		}
		
		public void setPostalCode(String postalCode) {
			this.postalCode = postalCode;
		}
		
		public String getPostalCode() {
			return postalCode;
		}
	}
	
	public class PointOfInterest {
		
		private String				name;					// 0..1
		private PointOfInterestType	pointOfInterestType;	// 0..1
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setPointOfInterestType(PointOfInterestType pointOfInterestType) {
			this.pointOfInterestType = pointOfInterestType;
		}
		
		public PointOfInterestType getPointOfInterestType() {
			return pointOfInterestType;
		}
	}
	
	public enum PointOfInterestType {
        AccommodationEatingAndDrinking,
        CommercialServices,
        Attraction,
        SportAndEntertainment,
        EducationAndHealth,
        PublicInfrastructure,
        ManufacturingAndProduction,
        Wholesale,
        Retail,
        Transport
	}
}
