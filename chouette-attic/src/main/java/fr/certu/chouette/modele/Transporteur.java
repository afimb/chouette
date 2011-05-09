package fr.certu.chouette.modele;

import java.util.Date;

import chouette.schema.Company;
import chouette.schema.Registration;

public class Transporteur extends BaseObjet {

    private Company company;
    
    public Transporteur() {
        super();
        
        company = new Company();
        company.setRegistration(new Registration());
    }
    
    public Company getCompany() {
        return company;
    }
    
    public void setCompany(final Company company) {
        if (company == null) {
            this.company = new Company();
            this.company.setRegistration(new Registration());
        } else {
            this.company = company;
            if (company.getRegistration() == null) {
                this.company.setRegistration(new Registration());
            }
        }
    }
    
    public String getCode() {
        return company.getCode();
    }
    
    public Date getCreationTime() {
        return company.getCreationTime();
    }
    
    public String getCreatorId() {
        return company.getCreatorId();
    }
    
    public String getEmail() {
        return company.getEmail();
    }
    
    public String getFax() {
        return company.getFax();
    }
    
    public String getName() {
        return company.getName();
    }
    
    public String getObjectId() {
        return company.getObjectId();
    }
    
    public int getObjectVersion() {
        setObjectVersion((int) company.getObjectVersion());
        return (int) company.getObjectVersion();
    }
    
    public String getOperatingDepartmentName() {
        return company.getOperatingDepartmentName();
    }
    
    public String getOrganisationalUnit() {
        return company.getOrganisationalUnit();
    }
    
    public String getPhone() {
        return company.getPhone();
    }
    
    public String getShortName() {
        return company.getShortName();
    }
    
    public String getRegistrationNumber() {
        return getCompany().getRegistration().getRegistrationNumber();
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        getCompany().getRegistration().setRegistrationNumber(registrationNumber);
    }
    
    public void setCode(String code) {
        company.setCode(code);
    }
    
    public void setCreationTime(Date creationTime) {
        company.setCreationTime(creationTime);
    }
    
    public void setCreatorId(String creatorId) {
        company.setCreatorId(creatorId);
    }
    
    public void setEmail(String email) {
        company.setEmail(email);
    }
    
    public void setFax(String fax) {
        company.setFax(fax);
    }
    
    public void setName(String name) {
        company.setName(name);
    }
    
    public void setObjectId(String objectId) {
        company.setObjectId(objectId);
    }
    
    public void setObjectVersion(int objectVersion) {
        if (objectVersion >= 1) {
            company.setObjectVersion(objectVersion);
        } else {
            company.setObjectVersion(1);
        }
    }
    
    public void setOperatingDepartmentName(String operatingDepartmentName) {
        company.setOperatingDepartmentName(operatingDepartmentName);
    }
    
    public void setOrganisationalUnit(String organisationalUnit) {
        company.setOrganisationalUnit(organisationalUnit);
    }
    
    public void setPhone(String phone) {
        company.setPhone(phone);
    }
    
    public void setShortName(String shortName) {
        company.setShortName(shortName);
    }
}
