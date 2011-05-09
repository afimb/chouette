package fr.certu.chouette.service.amivif.base;

public class CompanyConverter {

    private RegistrationConverter registrationConverter = new RegistrationConverter();

    public chouette.schema.Company atc(amivif.schema.Company amivifCompany) {
        if (amivifCompany == null) {
            return null;
        }
        chouette.schema.Company chouetteCompany = new chouette.schema.Company();
        chouetteCompany.setCode(amivifCompany.getCode());
        chouetteCompany.setCreationTime(amivifCompany.getCreationTime());
        chouetteCompany.setCreatorId(amivifCompany.getCreatorId());
        chouetteCompany.setEmail(amivifCompany.getEmail());
        chouetteCompany.setFax(amivifCompany.getFax());
        chouetteCompany.setName(amivifCompany.getName());
        chouetteCompany.setObjectId(amivifCompany.getObjectId());
        if (amivifCompany.hasObjectVersion() && amivifCompany.getObjectVersion() >= 1) {
            chouetteCompany.setObjectVersion(amivifCompany.getObjectVersion());
        } else {
            chouetteCompany.setObjectVersion(1);
        }
        chouetteCompany.setOperatingDepartmentName(amivifCompany.getOperatingDepartmentName());
        chouetteCompany.setOrganisationalUnit(amivifCompany.getOrganisationalUnit());
        chouetteCompany.setPhone(amivifCompany.getPhone());
        chouetteCompany.setRegistration(registrationConverter.atc(amivifCompany.getRegistration()));
        chouetteCompany.setShortName(amivifCompany.getShortName());
        return chouetteCompany;
    }

    public chouette.schema.Company[] atc(amivif.schema.Company[] amivifCompanies) {
        if (amivifCompanies == null) {
            return new chouette.schema.Company[0];
        }
        int totalCompany = amivifCompanies.length;
        chouette.schema.Company[] chouetteCompanies = new chouette.schema.Company[totalCompany];
        for (int i = 0; i < totalCompany; i++) {
            chouetteCompanies[i] = atc(amivifCompanies[i]);
        }
        return chouetteCompanies;
    }

    public amivif.schema.Company cta(chouette.schema.Company chouetteCompany) {
        if (chouetteCompany == null) {
            return null;
        }
        amivif.schema.Company amivifCompany = new amivif.schema.Company();
        amivifCompany.setCode(chouetteCompany.getCode());
        amivifCompany.setCreationTime(chouetteCompany.getCreationTime());
        amivifCompany.setCreatorId(chouetteCompany.getCreatorId());
        amivifCompany.setEmail(chouetteCompany.getEmail());
        amivifCompany.setFax(chouetteCompany.getFax());
        amivifCompany.setName(chouetteCompany.getName());
        amivifCompany.setObjectId(chouetteCompany.getObjectId());
        if (chouetteCompany.hasObjectVersion() && chouetteCompany.getObjectVersion() >= 1) {
            amivifCompany.setObjectVersion(chouetteCompany.getObjectVersion());
        } else {
            amivifCompany.setObjectVersion(1);
        }
        amivifCompany.setOperatingDepartmentName(chouetteCompany.getOperatingDepartmentName());
        amivifCompany.setOrganisationalUnit(chouetteCompany.getOrganisationalUnit());
        amivifCompany.setPhone(chouetteCompany.getPhone());
        amivifCompany.setRegistration(registrationConverter.cta(chouetteCompany.getRegistration()));
        amivifCompany.setShortName(chouetteCompany.getShortName());
        //amivifCompany.setExpiryTime(expiryTime);
        //amivifCompany.setTridentObjectTypeChoice(tridentObjectTypeChoice);
        return amivifCompany;
    }

    public amivif.schema.Company[] cta(chouette.schema.Company[] chouetteCompanies) {
        if (chouetteCompanies == null) {
            return new amivif.schema.Company[0];
        }
        int totalCompanies = chouetteCompanies.length;
        amivif.schema.Company[] amivifCompanies = new amivif.schema.Company[totalCompanies];
        for (int i = 0; i < totalCompanies; i++) {
            amivifCompanies[i] = cta(chouetteCompanies[i]);
        }
        return amivifCompanies;
    }
}
