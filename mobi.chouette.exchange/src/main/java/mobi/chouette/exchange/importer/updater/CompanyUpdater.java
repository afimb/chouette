package mobi.chouette.exchange.importer.updater;

import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.model.Company;

@Stateless(name = CompanyUpdater.BEAN_NAME)
public class CompanyUpdater implements Updater<Company> {

	public static final String BEAN_NAME = "CompanyUpdater";

	@Override
	public void update(Context context, Company oldValue, Company newValue) {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		if (newValue.getObjectId() != null
				&& !newValue.getObjectId().equals(oldValue.getObjectId())) {
			oldValue.setObjectId(newValue.getObjectId());
		}
		if (newValue.getObjectVersion() != null
				&& !newValue.getObjectVersion().equals(
						oldValue.getObjectVersion())) {
			oldValue.setObjectVersion(newValue.getObjectVersion());
		}
		if (newValue.getCreationTime() != null
				&& !newValue.getCreationTime().equals(
						oldValue.getCreationTime())) {
			oldValue.setCreationTime(newValue.getCreationTime());
		}
		if (newValue.getCreatorId() != null
				&& !newValue.getCreatorId().equals(oldValue.getCreatorId())) {
			oldValue.setCreatorId(newValue.getCreatorId());
		}
		if (newValue.getName() != null
				&& !newValue.getName().equals(oldValue.getName())) {
			oldValue.setName(newValue.getName());
		}
		if (newValue.getShortName() != null
				&& !newValue.getShortName().equals(oldValue.getShortName())) {
			oldValue.setShortName(newValue.getShortName());
		}
		if (newValue.getOrganisationalUnit() != null
				&& !newValue.getOrganisationalUnit().equals(
						oldValue.getOrganisationalUnit())) {
			oldValue.setOrganisationalUnit(newValue.getOrganisationalUnit());
		}
		if (newValue.getOperatingDepartmentName() != null
				&& !newValue.getOperatingDepartmentName().equals(
						oldValue.getOperatingDepartmentName())) {
			oldValue.setOperatingDepartmentName(newValue
					.getOperatingDepartmentName());
		}
		if (newValue.getCode() != null
				&& !newValue.getCode().equals(oldValue.getCode())) {
			oldValue.setCode(newValue.getCode());
		}
		if (newValue.getPhone() != null
				&& !newValue.getPhone().equals(oldValue.getPhone())) {
			oldValue.setPhone(newValue.getPhone());
		}
		if (newValue.getFax() != null
				&& !newValue.getFax().equals(oldValue.getFax())) {
			oldValue.setFax(newValue.getFax());
		}
		if (newValue.getEmail() != null
				&& !newValue.getEmail().equals(oldValue.getEmail())) {
			oldValue.setEmail(newValue.getEmail());
		}
		if (newValue.getRegistrationNumber() != null
				&& !newValue.getRegistrationNumber().equals(
						oldValue.getRegistrationNumber())) {
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
		}
		if (newValue.getUrl() != null
				&& !newValue.getUrl().equals(oldValue.getUrl())) {
			oldValue.setUrl(newValue.getUrl());
		}
		if (newValue.getTimeZone() != null
				&& !newValue.getTimeZone().equals(oldValue.getTimeZone())) {
			oldValue.setTimeZone(newValue.getTimeZone());
		}
	}

}
