package mobi.chouette.exchange.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.UserNeedEnum;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.trident.schema.trident.ChouettePTNetworkType;
import org.trident.schema.trident.LineExtensionType;
import org.trident.schema.trident.LineExtensionType.AccessibilitySuitabilityDetails;
import org.trident.schema.trident.TransportModeNameType;

import uk.org.ifopt.acsb.EncumbranceEnumeration;
import uk.org.ifopt.acsb.MedicalNeedEnumeration;
import uk.org.ifopt.acsb.MobilityEnumeration;
import uk.org.ifopt.acsb.PyschosensoryNeedEnumeration;
import uk.org.ifopt.acsb.UserNeedStructure;

public class LineProducer extends AbstractJaxbNeptuneProducer<ChouettePTNetworkType.ChouetteLineDescription.Line, Line>
		implements JsonExtension {

	//@Override
	public ChouettePTNetworkType.ChouetteLineDescription.Line produce(Line line, Collection<Route> exportableRoutes, boolean addExtension) {
		ChouettePTNetworkType.ChouetteLineDescription.Line jaxbLine = tridentFactory
				.createChouettePTNetworkTypeChouetteLineDescriptionLine();

		//
		populateFromModel(jaxbLine, line);

		jaxbLine.setComment(buildComment(line, addExtension));
		jaxbLine.setName(line.getName());
		jaxbLine.setNumber(line.getNumber());
		jaxbLine.setPublishedName(line.getPublishedName());
		jaxbLine.setPtNetworkIdShortcut(getNonEmptyObjectId(line.getNetwork()));

		try {
			TransportModeNameEnum transportModeName = line.getTransportModeName();
			if (transportModeName != null) {
				jaxbLine.setTransportModeName(TransportModeNameType.fromValue(transportModeName.name()));
			}
		} catch (IllegalArgumentException e) {
			// should not arrive after xsd validation
		}

		jaxbLine.setRegistration(getRegistration(line.getRegistrationNumber()));

		for (Route route : line.getRoutes()) {
			if (exportableRoutes.contains(route))
			{
				jaxbLine.getRouteId().add(route.getObjectId());
			}
		}

		boolean hasExtensions = false;
		LineExtensionType jaxbLineExtension = tridentFactory.createLineExtensionType();
		if (line.getUserNeeds() != null && !line.getUserNeeds().isEmpty()) {
			jaxbLineExtension.setAccessibilitySuitabilityDetails(extractAccessibilitySuitabilityDetails(line
					.getUserNeeds()));
			hasExtensions = true;
		}
		if (line.getMobilityRestrictedSuitable() != null && line.getMobilityRestrictedSuitable()) {
			jaxbLineExtension.setMobilityRestrictedSuitability(line.getMobilityRestrictedSuitable());
			hasExtensions = true;
		}
		// jaxbLineExtension.setStableId(stableId); ???
		if (hasExtensions)
			jaxbLine.setLineExtension(jaxbLineExtension);

		return jaxbLine;
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

	protected String buildComment(Line line, boolean addExtension) {
		if (!addExtension)
			return getNotEmptyString(line.getComment());

		try {

			JSONObject jsonComment = new JSONObject();
			if (!isEmpty(line.getColor())) {
				jsonComment.put(LINE_COLOR, line.getColor());
			}
			if (line.getFlexibleService() != null) {
				jsonComment.put(FLEXIBLE_SERVICE, line.getFlexibleService());
			}
			if (!isEmpty(line.getTextColor())) {
				jsonComment.put(TEXT_COLOR, line.getTextColor());
			}
			if (!isEmpty(line.getUrl())) {
				jsonComment.put(URL_REF, line.getUrl());
			}
			if (!isEmpty(line.getFootnotes())) {
				JSONArray notes = new JSONArray();
				int i = 1;
				for (Footnote footnote : line.getFootnotes()) {
					footnote.setKey(Integer.toString(i++));
					JSONObject note = new JSONObject();
					note.put(KEY, footnote.getKey());
					note.put(CODE, footnote.getCode());
					note.put(LABEL, footnote.getLabel());
					notes.put(note);
				}
				jsonComment.put(FOOTNOTES, notes);
			}
			if (jsonComment.length() == 0) {
				return getNotEmptyString(line.getComment());
			} else {
				if (!isEmpty(line.getComment())) {
					jsonComment.put(COMMENT, line.getComment().trim());
				}
			}
			return jsonComment.toString();
		} catch (Exception e) {
			return getNotEmptyString(line.getComment());
		}
	}

}
