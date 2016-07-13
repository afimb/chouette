package mobi.chouette.exchange.importer.updater;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.NeptuneUtil;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = ConnectionLinkUpdater.BEAN_NAME)
@Log4j
public class ConnectionLinkUpdater implements Updater<ConnectionLink> {

	public static final String BEAN_NAME = "ConnectionLinkUpdater";

	@EJB
	private StopAreaDAO stopAreaDAO;

	@Override
	public void update(Context context, ConnectionLink oldValue, ConnectionLink newValue) {

		if (newValue.isSaved()) {
			return;
		}
		newValue.setSaved(true);

		Monitor monitor = MonitorFactory.start(BEAN_NAME);
		Referential cache = (Referential) context.get(CACHE);
		
		// Database test init
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, "2-ConnectionLink-1", "E");
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
				
				
		if (newValue.getName() == null) {
			NamingUtil.setDefaultName(newValue);
		}

		if (oldValue.isDetached()) {
			oldValue.setObjectId(newValue.getObjectId());
			oldValue.setObjectVersion(newValue.getObjectVersion());
			oldValue.setCreationTime(newValue.getCreationTime());
			oldValue.setCreatorId(newValue.getCreatorId());
			oldValue.setName(newValue.getName());
			oldValue.setComment(newValue.getComment());
			oldValue.setLinkDistance(newValue.getLinkDistance());
			oldValue.setLiftAvailable(newValue.getLiftAvailable());
			oldValue.setMobilityRestrictedSuitable(newValue.getMobilityRestrictedSuitable());
			oldValue.setStairsAvailable(newValue.getStairsAvailable());
			oldValue.setDefaultDuration(newValue.getDefaultDuration());
			oldValue.setFrequentTravellerDuration(newValue.getFrequentTravellerDuration());
			oldValue.setOccasionalTravellerDuration(newValue.getOccasionalTravellerDuration());
			oldValue.setMobilityRestrictedTravellerDuration(newValue.getMobilityRestrictedTravellerDuration());
			oldValue.setLinkType(newValue.getLinkType());
			oldValue.setIntUserNeeds(newValue.getIntUserNeeds());
			oldValue.setDetached(false);
		} else {
			if (newValue.getObjectId() != null && !newValue.getObjectId().equals(oldValue.getObjectId())) {
				oldValue.setObjectId(newValue.getObjectId());
			}
			if (newValue.getObjectVersion() != null && !newValue.getObjectVersion().equals(oldValue.getObjectVersion())) {
				oldValue.setObjectVersion(newValue.getObjectVersion());
			}
			if (newValue.getCreationTime() != null && !newValue.getCreationTime().equals(oldValue.getCreationTime())) {
				oldValue.setCreationTime(newValue.getCreationTime());
			}
			if (newValue.getCreatorId() != null && !newValue.getCreatorId().equals(oldValue.getCreatorId())) {
				oldValue.setCreatorId(newValue.getCreatorId());
			}
			if (newValue.getName() != null && !newValue.getName().equals(oldValue.getName())) {
				oldValue.setName(newValue.getName());
			}
			if (newValue.getComment() != null && !newValue.getComment().equals(oldValue.getComment())) {
				oldValue.setComment(newValue.getComment());
			}
			if (newValue.getLinkDistance() != null && !newValue.getLinkDistance().equals(oldValue.getLinkDistance())) {
				oldValue.setLinkDistance(newValue.getLinkDistance());
			}
			if (newValue.getLiftAvailable() != null && !newValue.getLiftAvailable().equals(oldValue.getLiftAvailable())) {
				oldValue.setLiftAvailable(newValue.getLiftAvailable());
			}
			if (newValue.getMobilityRestrictedSuitable() != null
					&& !newValue.getMobilityRestrictedSuitable().equals(oldValue.getMobilityRestrictedSuitable())) {
				oldValue.setMobilityRestrictedSuitable(newValue.getMobilityRestrictedSuitable());
			}
			if (newValue.getStairsAvailable() != null
					&& !newValue.getStairsAvailable().equals(oldValue.getStairsAvailable())) {
				oldValue.setStairsAvailable(newValue.getStairsAvailable());
			}
			if (newValue.getDefaultDuration() != null
					&& !newValue.getDefaultDuration().equals(oldValue.getDefaultDuration())) {
				oldValue.setDefaultDuration(newValue.getDefaultDuration());
			}
			if (newValue.getFrequentTravellerDuration() != null
					&& !newValue.getFrequentTravellerDuration().equals(oldValue.getFrequentTravellerDuration())) {
				oldValue.setFrequentTravellerDuration(newValue.getFrequentTravellerDuration());
			}
			if (newValue.getOccasionalTravellerDuration() != null
					&& !newValue.getOccasionalTravellerDuration().equals(oldValue.getOccasionalTravellerDuration())) {
				oldValue.setOccasionalTravellerDuration(newValue.getOccasionalTravellerDuration());
			}
			if (newValue.getMobilityRestrictedTravellerDuration() != null
					&& !newValue.getMobilityRestrictedTravellerDuration().equals(
							oldValue.getMobilityRestrictedTravellerDuration())) {
				oldValue.setMobilityRestrictedTravellerDuration(newValue.getMobilityRestrictedTravellerDuration());
			}
			if (newValue.getLinkType() != null && !newValue.getLinkType().equals(oldValue.getLinkType())) {
				oldValue.setLinkType(newValue.getLinkType());
			}
			if (newValue.getIntUserNeeds() != null && !newValue.getIntUserNeeds().equals(oldValue.getIntUserNeeds())) {
				oldValue.setIntUserNeeds(newValue.getIntUserNeeds());
			}
		}
		
		// Check stop areas value in correspondance update
		twoConnectionLinkOneTest(validationReporter, context, oldValue, newValue, data);
		if (newValue.getStartOfLink() != null) {

			String objectId = newValue.getStartOfLink().getObjectId();
			StopArea startOfLink = cache.getStopAreas().get(objectId);
			if (startOfLink == null) {
				log.info("search connectionLink starts in DB " + objectId);
				startOfLink = stopAreaDAO.findByObjectId(objectId);
				if (startOfLink != null) {
					cache.getStopAreas().put(objectId, startOfLink);
				}
			}

			if (startOfLink != null) {
				// log.info("update connectionLink starts"+startOfLink.getName());
				if (context.containsKey(CONNECTION_LINK_BLOC))
					oldValue.forceStartOfLink(startOfLink);
				else
					oldValue.setStartOfLink(startOfLink);
			}
		}

		if (newValue.getEndOfLink() != null) {
			String objectId = newValue.getEndOfLink().getObjectId();
			StopArea endOfLink = cache.getStopAreas().get(objectId);
			if (endOfLink == null) {
				log.info("search connectionLink ends in DB " + objectId);
				endOfLink = stopAreaDAO.findByObjectId(objectId);
				if (endOfLink != null) {
					cache.getStopAreas().put(objectId, endOfLink);
				}
			}

			if (endOfLink != null) {
				// log.info("update connectionLink ends"+endOfLink.getName());
				if (context.containsKey(CONNECTION_LINK_BLOC))
					oldValue.forceEndOfLink(endOfLink);
				else
					oldValue.setEndOfLink(endOfLink);
			}
		}
		monitor.stop();
	}
	

	/**
	 * Test 2-ConnectionLink-1
	 * @param validationReporter
	 * @param context
	 * @param oldCL
	 * @param newCL
	 */
	private void twoConnectionLinkOneTest(ValidationReporter validationReporter, Context context, ConnectionLink oldCL, ConnectionLink newCL, ValidationData data) {
		if(!NeptuneUtil.sameValue(oldCL.getStartOfLink(), newCL.getStartOfLink()) || !NeptuneUtil.sameValue(oldCL.getEndOfLink(), newCL.getEndOfLink()))
			validationReporter.addCheckPointReportError(context, CONNECTION_LINK_1, data.getDataLocations().get(newCL.getObjectId()));
		else
			validationReporter.reportSuccess(context, CONNECTION_LINK_1);
	}

}
