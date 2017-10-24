package mobi.chouette.exchange.netexprofile.exporter.producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.rutebanken.netex.model.DayOfWeekEnumeration;
import org.rutebanken.netex.model.EntityInVersionStructure;
import org.rutebanken.netex.model.OrganisationTypeEnumeration;
import org.rutebanken.netex.model.VersionOfObjectRefStructure;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.NetexprofileExportParameters;
import mobi.chouette.model.Company;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.NeptuneObject;
import mobi.chouette.model.Network;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.type.OrganisationTypeEnum;

@Log4j
public class NetexProducerUtils {

	private static final String OBJECT_ID_SPLIT_CHAR = ":";
	
	public static boolean isSet(Object... objects) {
		for (Object val : objects) {
			if (val != null) {
				if (val instanceof String) {
					if (!((String) val).isEmpty())
						return true;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	public static OrganisationTypeEnumeration getOrganisationTypeEnumeration(OrganisationTypeEnum organisationTypeEnum) {
		if (organisationTypeEnum == null)
			return null;
		switch (organisationTypeEnum) {
		case Authority:
			return OrganisationTypeEnumeration.AUTHORITY;
		case Operator:
			return OrganisationTypeEnumeration.OPERATOR;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<DayOfWeekEnumeration> toDayOfWeekEnumeration(List<DayTypeEnum> dayTypeEnums) {
		EnumSet actualDaysOfWeek = EnumSet.noneOf(DayTypeEnum.class);
		for (DayTypeEnum dayTypeEnum : dayTypeEnums) {
			actualDaysOfWeek.add(dayTypeEnum);
		}

		if (actualDaysOfWeek.isEmpty()) {
			return Collections.EMPTY_LIST;
		} else if (actualDaysOfWeek
				.equals(EnumSet.of(DayTypeEnum.Monday, DayTypeEnum.Tuesday, DayTypeEnum.Wednesday, DayTypeEnum.Thursday, DayTypeEnum.Friday))) {
			return Collections.singletonList(DayOfWeekEnumeration.WEEKDAYS);
		} else if (actualDaysOfWeek.equals(EnumSet.of(DayTypeEnum.Saturday, DayTypeEnum.Sunday))) {
			return Collections.singletonList(DayOfWeekEnumeration.WEEKEND);
		} else if (actualDaysOfWeek.equals(EnumSet.of(DayTypeEnum.Monday, DayTypeEnum.Tuesday, DayTypeEnum.Wednesday, DayTypeEnum.Thursday, DayTypeEnum.Friday,
				DayTypeEnum.Saturday, DayTypeEnum.Sunday))) {
			return Collections.singletonList(DayOfWeekEnumeration.EVERYDAY);
		}

		List<DayOfWeekEnumeration> dayOfWeekEnumerations = new ArrayList<>();

		for (DayTypeEnum dayTypeEnum : dayTypeEnums) {
			switch (dayTypeEnum) {
			case Monday:
				dayOfWeekEnumerations.add(DayOfWeekEnumeration.MONDAY);
				break;
			case Tuesday:
				dayOfWeekEnumerations.add(DayOfWeekEnumeration.TUESDAY);
				break;
			case Wednesday:
				dayOfWeekEnumerations.add(DayOfWeekEnumeration.WEDNESDAY);
				break;
			case Thursday:
				dayOfWeekEnumerations.add(DayOfWeekEnumeration.THURSDAY);
				break;
			case Friday:
				dayOfWeekEnumerations.add(DayOfWeekEnumeration.FRIDAY);
				break;
			case Saturday:
				dayOfWeekEnumerations.add(DayOfWeekEnumeration.SATURDAY);
				break;
			case Sunday:
				dayOfWeekEnumerations.add(DayOfWeekEnumeration.SUNDAY);
				break;
			default:
				// None
			}
		}

		return dayOfWeekEnumerations;
	}

	private static AtomicInteger idCounter = new AtomicInteger(0);

	public static String netexId(String objectIdPrefix, String elementName, String objectIdSuffix) {
		return objectIdPrefix + OBJECT_ID_SPLIT_CHAR + elementName + OBJECT_ID_SPLIT_CHAR + objectIdSuffix;
	}

	public static String translateObjectId(String original, String newType) {
		String[] splittedParts = original.split(":");
		if(splittedParts.length == 3) {
			return original.replaceAll(splittedParts[1], newType);
		} else {
			log.warn("Could not transform identifier "+original+" to type "+newType+" as it does not conform to id standard (XXX:Type:YYY)");
			return original;
		}
	}
	
	public static String createUniqueId(Context context, String type) {
		NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(Constant.CONFIGURATION);
		return configuration.getDefaultCodespacePrefix()+OBJECT_ID_SPLIT_CHAR+type+OBJECT_ID_SPLIT_CHAR+idCounter.incrementAndGet();
	}

	public static String translateType(NeptuneObject v) {
		if (v instanceof Timetable) {
			return "DayType";
		} else if (v instanceof Company) {
			Company c = (Company) v;
			if (OrganisationTypeEnum.Authority.equals(c.getOrganisationType())) {
				return "Authority";
			} else if (OrganisationTypeEnum.Operator.equals(c.getOrganisationType())) {
				return "Operator";
			} else {
				return "GeneralOrganisation";
			}
		} else if (v instanceof VehicleJourney) {
			return "ServiceJourney";
		} else if (v instanceof JourneyPattern) {
			return "JourneyPattern";
		} else if (v instanceof StopArea) {
			StopArea sa = (StopArea) v;
			if (ChouetteAreaEnum.BoardingPosition.equals(sa.getAreaType())) {
				return "Quay";
			} else if (ChouetteAreaEnum.CommercialStopPoint.equals(sa.getAreaType())) {
				return "StopPlace";
			}
		} else if(v instanceof Footnote) {
			return "Notice";
		} else if(v instanceof StopPoint) {
			return "StopPointInJourneyPattern";
		} else if(v instanceof VehicleJourneyAtStop) {
			return "TimetabledPassingTime";
		} else if(v instanceof Network) {
			return "Network";
		}

		return null;

		
	}
	
	
	public static String generateNetexId(NeptuneIdentifiedObject source) {
		if(source == null) {
			log.error("Cannot generate netexid as source is null");
			return null;
		}
		String newType = translateType(source);
		if(newType != null) {
			return translateObjectId(source.getObjectId(), newType);
		} else {
			return source.getObjectId();
		}
	}

	public static void populateId(NeptuneIdentifiedObject source, EntityInVersionStructure destination) {
		if(source == null || destination == null) {
			log.error("Cannot set id since either source or destination is null");
			return;
		}
		String newType = translateType(source);
		if (newType != null) {
			destination.setId(translateObjectId(source.getObjectId(), newType));
		} else {
			destination.setId(source.getObjectId());
		}
		destination.setVersion(source.getObjectVersion() == null ? "1" : source.getObjectVersion().toString());
	}

	public static void populateReference(NeptuneIdentifiedObject source, VersionOfObjectRefStructure destination, boolean withVersion) {
		if(source == null || destination == null) {
			log.error("Cannot set reference since either source or destination is null");
			return;
		}
		String newType = translateType(source);
		if (newType != null) {
			destination.setRef(translateObjectId(source.getObjectId(), newType));
		} else {
			destination.setRef(source.getObjectId());
		}
		if (withVersion) {
			destination.setVersion(source.getObjectVersion() == null ? "1" : source.getObjectVersion().toString());
		}

	}

	public static void populateReference(EntityInVersionStructure source, VersionOfObjectRefStructure destination, boolean withVersion) {
		if(source == null || destination == null) {
			log.error("Cannot set reference since either source or destination is null");
			return;
		}
		destination.setRef(source.getId());
		if (withVersion) {
			destination.setVersion(source.getVersion());
		}

	}
}
