package fr.certu.chouette.service.validation.amivif;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TridentObject {
	
	private TridentId				objectId;				// 1
	private int					objectVersion	= 1;	// 0..1 must be positive > 0
	private Date					creationTime;			// 0..1
	private Date					expiryTime;				// 0..1
	private String					creatorId;				// 0..1
	private List<ValidityPeriod>	validityPeriods;		// 0..w CHOICE WITH validityDomain
	private String					validityDomain;			// 0..1 CHOICE WITH validityPeriods
	private List<RespPTLineStructTimetableType>	respPTLineStructTimetableTypes	= new ArrayList<RespPTLineStructTimetableType>();	// 1..w
	
	public void setTridentObject(TridentObject tridentObject) {
		this.setObjectId(tridentObject.getObjectId());
		this.setObjectVersion(tridentObject.getObjectVersion());
		this.setCreationTime(tridentObject.getCreationTime());
		this.setExpiryTime(tridentObject.getExpiryTime());
		this.setCreatorId(tridentObject.getCreatorId());
		this.setValidityPeriods(tridentObject.getValidityPeriods());
		this.setValidityDomain(tridentObject.getValidityDomain());
	}
	
	public void setObjectId(TridentId objectId) {
		this.objectId = objectId;
	}

	public TridentId getObjectId() {
		return objectId;
	}
	
	public void setObjectVersion(int objectVersion) {
            if (objectVersion >= 1)
		this.objectVersion = objectVersion;
            else
                this.objectVersion = 1;
	}
	
	public int getObjectVersion() {
		return objectVersion;
	}
	
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	
	public Date getCreationTime() {
		return creationTime;
	}
	
	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}
	
	public Date getExpiryTime() {
		return expiryTime;
	}
	
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	
	public String getCreatorId() {
		return creatorId;
	}
	
	public void setValidityPeriods(List<ValidityPeriod> validityPeriods) {
		this.validityPeriods = validityPeriods;
	}
	
	public List<ValidityPeriod> getValidityPeriods() {
		return validityPeriods;
	}
	
	public void addValidityPeriod(ValidityPeriod validityPeriod) {
		validityPeriods.add(validityPeriod);
	}
	
	public void removeValidityPeriod(ValidityPeriod validityPeriod) {
		validityPeriods.remove(validityPeriod);
	}
	
	public void removeValidityPeriod(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getValidityPeriodsCount()))
			throw new IndexOutOfBoundsException();
		validityPeriods.remove(i);
	}
	
	public ValidityPeriod getValidityPeriod(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getValidityPeriodsCount()))
			throw new IndexOutOfBoundsException();
		return (ValidityPeriod)validityPeriods.get(i);
	}
	
	public int getValidityPeriodsCount() {
		if (validityPeriods == null)
			return 0;
		return validityPeriods.size();
	}
	
	public void setValidityDomain(String validityDomain) {
		this.validityDomain = validityDomain;
	}
	
	public String getValidityDomain() {
		return validityDomain;
	}
	
	public void setRespPTLineStructTimetableTypes(List<RespPTLineStructTimetableType> respPTLineStructTimetableTypes) {
		this.respPTLineStructTimetableTypes = respPTLineStructTimetableTypes;
	}
	
	public List<RespPTLineStructTimetableType> getRespPTLineStructTimetableTypes() {
		return respPTLineStructTimetableTypes;
	}
	
	public void addRespPTLineStructTimetableType(RespPTLineStructTimetableType respPTLineStructTimetableType) {
		respPTLineStructTimetableTypes.add(respPTLineStructTimetableType);
	}
	
	public void removeRespPTLineStructTimetableType(RespPTLineStructTimetableType respPTLineStructTimetableType) {
		respPTLineStructTimetableTypes.remove(respPTLineStructTimetableType);
	}
	
	public void removeRespPTLineStructTimetableType(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getRespPTLineStructTimetableTypesCount()))
			throw new IndexOutOfBoundsException();
		respPTLineStructTimetableTypes.remove(i);
	}
	
	public RespPTLineStructTimetableType getRespPTLineStructTimetableType(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getRespPTLineStructTimetableTypesCount()))
			throw new IndexOutOfBoundsException();
		return (RespPTLineStructTimetableType)respPTLineStructTimetableTypes.get(i);
	}
	
	public int getRespPTLineStructTimetableTypesCount() {
		if (respPTLineStructTimetableTypes == null)
			return 0;
		return respPTLineStructTimetableTypes.size();
	}
	
	public class TridentId {
		
		private String	peerId			= "";	// 1
		private String	className		= "";	// 1
		private String	progressiveInt	= "";	// 1
		
		public TridentId(String objectId) {
			if (objectId == null)
				throw new NullPointerException();
			String[] subStrings = objectId.split(":");
			if (subStrings.length != 3)
				throw new IndexOutOfBoundsException();
			setPeerId(subStrings[0]);
			setClassName(subStrings[1]);
			setProgressiveInt(subStrings[2]);
		}

		public void setPeerId(String peerId) {
			this.peerId = peerId;
		}
		
		public String getPeerId() {
			return peerId;
		}
		
		public void setClassName(String className) {
			this.className = className;
		}
		
		public String getClassName() {
			return className;
		}
		
		public void setProgressiveInt(String progressiveInt) {
			this.progressiveInt = progressiveInt;
		}
		
		public String getProgressiveInt() {
			return progressiveInt;
		}
		
		public String toString() {
			return peerId+":"+className+":"+progressiveInt;
		}
	}
	
	public class ValidityPeriod {
		
		private Date	start;	// 1
		private Date	end;	// 0..1
		
		public void setStart(Date start) {
			this.start = start;
		}
		
		public Date getStart() {
			return start;
		}
		
		public void setEnd(Date end) {
			this.end = end;
		}
		
		public Date getEnd() {
			return end;
		}
	}
}
