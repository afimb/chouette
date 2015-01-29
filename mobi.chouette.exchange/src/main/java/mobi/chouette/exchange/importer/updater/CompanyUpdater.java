package mobi.chouette.exchange.importer.updater;

import mobi.chouette.common.Context;
import mobi.chouette.model.Company;

public class CompanyUpdater implements Updater<Company> {

	@Override
	public void update(Context context, Company oldValue, Company newValue) {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		if (newValue.getObjectId() != null
				&& newValue.getObjectId().compareTo(oldValue.getObjectId()) != 0) {
			oldValue.setObjectId(newValue.getObjectId());
		}
		if (newValue.getObjectVersion() != null
				&& newValue.getObjectVersion().compareTo(
						oldValue.getObjectVersion()) != 0) {
			oldValue.setObjectVersion(newValue.getObjectVersion());
		}
		if (newValue.getCreationTime() != null
				&& newValue.getCreationTime().compareTo(
						oldValue.getCreationTime()) != 0) {
			oldValue.setCreationTime(newValue.getCreationTime());
		}
		if (newValue.getCreatorId() != null
				&& newValue.getCreatorId().compareTo(oldValue.getCreatorId()) != 0) {
			oldValue.setCreatorId(newValue.getCreatorId());
		}
		if (newValue.getName() != null
				&& newValue.getName().compareTo(oldValue.getName()) != 0) {
			oldValue.setName(newValue.getName());
		}
		if (newValue.getShortName() != null
				&& newValue.getShortName().compareTo(oldValue.getShortName()) != 0) {
			oldValue.setShortName(newValue.getShortName());
		}
		if (newValue.getOrganisationalUnit() != null
				&& newValue.getOrganisationalUnit().compareTo(
						oldValue.getOrganisationalUnit()) != 0) {
			oldValue.setOrganisationalUnit(newValue.getOrganisationalUnit());
		}
		if (newValue.getOperatingDepartmentName() != null
				&& newValue.getOperatingDepartmentName().compareTo(
						oldValue.getOperatingDepartmentName()) != 0) {
			oldValue.setOperatingDepartmentName(newValue
					.getOperatingDepartmentName());
		}
		if (newValue.getCode() != null
				&& newValue.getCode().compareTo(oldValue.getCode()) != 0) {
			oldValue.setCode(newValue.getCode());
		}
		if (newValue.getPhone() != null
				&& newValue.getPhone().compareTo(oldValue.getPhone()) != 0) {
			oldValue.setPhone(newValue.getPhone());
		}
		if (newValue.getFax() != null
				&& newValue.getFax().compareTo(oldValue.getFax()) != 0) {
			oldValue.setFax(newValue.getFax());
		}
		if (newValue.getEmail() != null
				&& newValue.getEmail().compareTo(oldValue.getEmail()) != 0) {
			oldValue.setEmail(newValue.getEmail());
		}
		if (newValue.getRegistrationNumber() != null
				&& newValue.getRegistrationNumber().compareTo(
						oldValue.getRegistrationNumber()) != 0) {
			oldValue.setRegistrationNumber(newValue.getRegistrationNumber());
		}
		if (newValue.getUrl() != null
				&& newValue.getUrl().compareTo(oldValue.getUrl()) != 0) {
			oldValue.setUrl(newValue.getUrl());
		}
		if (newValue.getTimeZone() != null
				&& newValue.getTimeZone().compareTo(oldValue.getTimeZone()) != 0) {
			oldValue.setTimeZone(newValue.getTimeZone());
		}
	}

	static {
		UpdaterFactory.register(CompanyUpdater.class.getName(),
				new UpdaterFactory() {
					private CompanyUpdater INSTANCE = new CompanyUpdater();

					@Override
					protected Updater<Company> create() {
						return INSTANCE;
					}
				});
	}

}
