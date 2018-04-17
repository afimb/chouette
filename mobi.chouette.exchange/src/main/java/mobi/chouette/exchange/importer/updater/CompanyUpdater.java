package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.dao.BrandingDAO;
import mobi.chouette.model.Branding;
import mobi.chouette.model.Company;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = CompanyUpdater.BEAN_NAME)
public class CompanyUpdater implements Updater<Company> {

	public static final String BEAN_NAME = "CompanyUpdater";

	@EJB(beanName = BrandingUpdater.BEAN_NAME)
	private Updater<Branding> brandingUpdater;

	@EJB
	private BrandingDAO brandingDAO;

	@Override
	public void update(Context context, Company oldValue, Company newValue) throws Exception {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		Referential cache = (Referential) context.get(CACHE);
        Monitor monitor = MonitorFactory.start(BEAN_NAME);
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
		if (newValue.getLegalName() != null
				&& !newValue.getLegalName().equals(oldValue.getLegalName())) {
			oldValue.setLegalName(newValue.getLegalName());
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

		if (newValue.getPublicPhone() != null
				&& !newValue.getPublicPhone().equals(oldValue.getPublicPhone())) {
			oldValue.setPublicPhone(newValue.getPublicPhone());
		}
		if (newValue.getPublicEmail() != null
				&& !newValue.getPublicEmail().equals(oldValue.getPublicEmail())) {
			oldValue.setPublicEmail(newValue.getPublicEmail());
		}
		if (newValue.getPublicUrl() != null
				&& !newValue.getPublicUrl().equals(oldValue.getPublicUrl())) {
			oldValue.setPublicUrl(newValue.getPublicUrl());
		}
		if (newValue.getOrganisationType() != null
				&& !newValue.getOrganisationType().equals(oldValue.getOrganisationType())) {
			oldValue.setOrganisationType(newValue.getOrganisationType());
		}

		// Branding

		if (newValue.getBranding() == null) {
			oldValue.setBranding(null);
		} else {
			String objectId = newValue.getBranding().getObjectId();
			Branding branding = cache.getBrandings().get(objectId);
			if (branding == null) {
				branding = brandingDAO.findByObjectId(objectId);
				if (branding != null) {
					cache.getBrandings().put(objectId, branding);
				}
			}
			if (branding == null) {
				branding = ObjectFactory.getBranding(cache, objectId);
			}
			oldValue.setBranding(branding);

			brandingUpdater.update(context, oldValue.getBranding(), newValue.getBranding());
		}

		
		monitor.stop();
	}

}
