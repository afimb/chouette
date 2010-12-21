package fr.certu.chouette.service.validation.amivif;


public class Company extends TridentObject {
	
	private String								name;																				// 1
	private String								shortName;																			// 0..1
	private String								organisationalUnit;																	// 0..1
	private String								operatingDepartmentName;															// 0..1
	private String								code;																				// 0..1
	private String								phone;																				// 0..1
	private String								fax;																				// 0..1
	private String								email;																				// 0..1
	private Registration						registration;																		// 0..1
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public void setOrganisationalUnit(String organisationalUnit) {
		this.organisationalUnit = organisationalUnit;
	}
	
	public String getOrganisationalUnit() {
		return organisationalUnit;
	}
	
	public void setOperatingDepartmentName(String operatingDepartmentName) {
		this.operatingDepartmentName = operatingDepartmentName;
	}
	
	public String getOperatingDepartmentName() {
		return operatingDepartmentName;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setFax(String fax) {
		this.fax = fax;
	}
	
	public String getFax() {
		return fax;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setRegistration(Registration registration) {
		this.registration = registration;
	}
	
	public Registration getRegistration() {
		return registration;
	}
}
