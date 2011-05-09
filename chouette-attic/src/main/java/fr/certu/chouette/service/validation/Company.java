package fr.certu.chouette.service.validation;

public class Company {
	
	private ChouettePTNetwork 	chouettePTNetwork;
	private String 				code;
	private java.util.Date 		creationTime;
	private String 				creatorId;
	private String 				email;
	private String 				fax;
	private String 				name;
	private String 				objectId;
	private boolean 			hasObjectVersion = false;
	private int 				objectVersion;
	private String 				operatingDepartmentName;
	private String 				organisationalUnit;
	private String 				phone;
	private Registration 		registration;
	private String 				shortName;
	
	public void setChouettePTNetwork(ChouettePTNetwork chouettePTNetwork) {
		this.chouettePTNetwork = chouettePTNetwork;
	}
	
	public ChouettePTNetwork getChouettePTNetwork() {
		return chouettePTNetwork;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCreationTime(java.util.Date creationTime) {
		this.creationTime = creationTime;
	}
	
	public java.util.Date getCreationTime() {
		return creationTime;
	}
	
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	
	public String getCreatorId() {
		return creatorId;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setFax(String fax) {
		this.fax = fax;
	}
	
	public String getFax() {
		return fax;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public String getObjectId() {
		return objectId;
	}
	
	public void setObjectVersion(int objectVersion) {
		if (objectVersion >= 1) {
			hasObjectVersion = true;
		this.objectVersion = objectVersion;
                }
                else {
			hasObjectVersion = false;
		this.objectVersion = 1;
                }
	}
	
	public int getObjectVersion() {
		return objectVersion;
	}
	
	public boolean hasObjectVersion() {
		return hasObjectVersion;
	}
	
	public void setOperatingDepartmentName(String operatingDepartmentName) {
		this.operatingDepartmentName = operatingDepartmentName;
	}
	
	public String getOperatingDepartmentName() {
		return operatingDepartmentName;
	}
	
	public void setOrganisationalUnit(String organisationalUnit) {
		this.organisationalUnit = organisationalUnit;
	}
	
	public String getOrganisationalUnit() {
		return organisationalUnit;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setRegistration(Registration registration) {
		this.registration = registration;
	}
	
	public Registration getRegistration() {
		return registration;
	}
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<Company>\n");
		stb.append("<Name>"+name+"</Name>\n");		
		if (shortName != null)
			stb.append("<ShortName>"+shortName+"</ShortName>\n");
		if (organisationalUnit != null)
			stb.append("<OrganisationalUnit>"+organisationalUnit+"</OrganisationalUnit>\n");
		if (operatingDepartmentName != null)
			stb.append("<OperatingDepartmentName>"+operatingDepartmentName+"</OperatingDepartmentName>\n");
		if (code != null)
			stb.append("<Code>"+code+"</Code>\n");
		if (phone != null)
			stb.append("<Phone>"+phone+"</Phone>\n");
		if (fax != null)
			stb.append("<Fax>"+fax+"</Fax>\n");
		if (email != null)
			stb.append("<Email>"+email+"</Email>\n");
		if (registration != null)
			stb.append(registration.toString());
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion)
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		if (creationTime != null)
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		if (creatorId != null)
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		stb.append("</Company>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Company>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Name>"+name+"</Name>\n");
		if (shortName != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<ShortName>"+shortName+"</ShortName>\n");
		}
		if (organisationalUnit != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<OrganisationalUnit>"+organisationalUnit+"</OrganisationalUnit>\n");
		}
		if (operatingDepartmentName != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<OperatingDepartmentName>"+operatingDepartmentName+"</OperatingDepartmentName>\n");
		}
		if (code != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<Code>"+code+"</Code>\n");
		}
		if (phone != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<Phone>"+phone+"</Phone>\n");
		}
		if (fax != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<Fax>"+fax+"</Fax>\n");
		}
		if (email != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<Email>"+email+"</Email>\n");
		}
		if (registration != null)
			stb.append(registration.toString(indent+1, indentSize));
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		}
		if (creationTime != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		}
		if (creatorId != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		}
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</Company>\n");
		return stb.toString();
	}
}
