package mobi.chouette.exchange.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.UserNeedEnum;

import org.codehaus.jettison.json.JSONObject;
import org.trident.schema.trident.ChouetteAreaType;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea;
import org.trident.schema.trident.StopAreaExtensionType;
import org.trident.schema.trident.StopAreaExtensionType.AccessibilitySuitabilityDetails;

import uk.org.ifopt.acsb.EncumbranceEnumeration;
import uk.org.ifopt.acsb.MedicalNeedEnumeration;
import uk.org.ifopt.acsb.MobilityEnumeration;
import uk.org.ifopt.acsb.PyschosensoryNeedEnumeration;
import uk.org.ifopt.acsb.UserNeedStructure;

public class StopAreaProducer extends AbstractJaxbNeptuneProducer<ChouetteArea.StopArea, StopArea> implements
		JsonExtension {

	//@Override
	public ChouetteArea.StopArea produce(StopArea stopArea, boolean addExtension) {
		ChouetteArea.StopArea jaxbStopArea = tridentFactory.createChouettePTNetworkTypeChouetteAreaStopArea();

		populateFromModel(jaxbStopArea, stopArea);

		jaxbStopArea.setComment(buildComment(stopArea, addExtension));
		jaxbStopArea.setName(stopArea.getName());

		StopAreaExtensionType stopAreaExtension = tridentFactory.createStopAreaExtensionType();
		stopAreaExtension.setAccessibilitySuitabilityDetails(extractAccessibilitySuitabilityDetails(stopArea
				.getUserNeeds()));

		try {
			ChouetteAreaEnum areaType = stopArea.getAreaType();
			if (areaType != null) {
				stopAreaExtension.setAreaType(ChouetteAreaType.fromValue(areaType.name()));
			}
		} catch (IllegalArgumentException e) {
			// TODO generate report
		}

		stopAreaExtension.setNearestTopicName(getNotEmptyString(stopArea.getNearestTopicName()));
		stopAreaExtension.setRegistration(getRegistration(stopArea.getRegistrationNumber()));
		if (stopArea.getFareCode() != null)
			stopAreaExtension.setFareCode(stopArea.getFareCode());
		if (stopArea.getLiftAvailable() != null) {
			stopAreaExtension.setLiftAvailability(stopArea.getLiftAvailable().booleanValue());
		}
		if (stopArea.getMobilityRestrictedSuitable() != null) {
			stopAreaExtension.setMobilityRestrictedSuitability(stopArea.getMobilityRestrictedSuitable().booleanValue());
		}
		if (stopArea.getStairsAvailable() != null) {
			stopAreaExtension.setStairsAvailability(stopArea.getStairsAvailable().booleanValue());
		}

		jaxbStopArea.setStopAreaExtension(stopAreaExtension);

		return jaxbStopArea;
	}

	protected String buildComment(StopArea area, boolean addExtension) {
		if (!addExtension)
			return getNotEmptyString(area.getComment());

		try {

			JSONObject jsonComment = new JSONObject();
			if (!isEmpty(area.getUrl())) {
				jsonComment.put(URL_REF, area.getUrl());
			}
			if (!isEmpty(area.getTimeZone())) {
				jsonComment.put(TIME_ZONE, area.getTimeZone());
			}
			if (!isEmpty(area.getZipCode())) {
				jsonComment.put(ZIP_CODE, area.getZipCode());
			}
			if (!isEmpty(area.getCityName())) {
				jsonComment.put(CITY_NAME, area.getCityName());
			}
			if (jsonComment.length() == 0) {
				return getNotEmptyString(area.getComment());
			} else {
				if (!isEmpty(area.getComment())) {
					jsonComment.put(COMMENT, area.getComment().trim());
				}
			}
			return jsonComment.toString();
		} catch (Exception e) {
			return getNotEmptyString(area.getComment());
		}
	}

	protected AccessibilitySuitabilityDetails extractAccessibilitySuitabilityDetails(List<UserNeedEnum> userNeeds) {
		AccessibilitySuitabilityDetails details = new AccessibilitySuitabilityDetails();
		List<UserNeedStructure> detailsItems = new ArrayList<UserNeedStructure>();
		if (userNeeds != null) {
			for (UserNeedEnum userNeed : userNeeds) {
				if (userNeed != null) {
					UserNeedStructure userNeedGroup = new UserNeedStructure();

					switch (userNeed.category()) {
					case ENCUMBRANCE:
						userNeedGroup.setEncumbranceNeed(EncumbranceEnumeration.fromValue(userNeed.value()));
						break;
					case MEDICAL:
						userNeedGroup.setMedicalNeed(MedicalNeedEnumeration.fromValue(userNeed.value()));
						break;
					case PSYCHOSENSORY:
						userNeedGroup.setPsychosensoryNeed(PyschosensoryNeedEnumeration.fromValue(userNeed.value()));
						break;
					case MOBILITY:
						userNeedGroup.setMobilityNeed(MobilityEnumeration.fromValue(userNeed.value()));
						break;
					default:
						throw new IllegalArgumentException("bad value of userNeed");
					}

					detailsItems.add(userNeedGroup);

				}
			}
		}

		if (detailsItems.isEmpty())
			return null;
		details.getMobilityNeedOrPsychosensoryNeedOrMedicalNeed().addAll(detailsItems);
		return details;
	}

}
