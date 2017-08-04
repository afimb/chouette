package mobi.chouette.exchange.netexprofile.exporter.producer;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.rutebanken.netex.model.DayOfWeekEnumeration;
import org.rutebanken.netex.model.OrganisationTypeEnumeration;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.type.OrganisationTypeEnum;

@Log4j
public class NetexProducerUtils {

    private static final String OBJECT_ID_SPLIT_CHAR = ":";
    public static final ZoneId LOCAL_ZONE_ID = ZoneId.of("Europe/Oslo");

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

    public static String[] generateIdSequence(int totalInSequence) {
        String[] idSequence = new String[totalInSequence];
        AtomicInteger incrementor = new AtomicInteger(1);

        for (int i = 0; i < totalInSequence; i++) {
            idSequence[i] = String.valueOf(incrementor.getAndAdd(1));
        }

        return idSequence;
    }

    public static ZoneOffset getZoneOffset(ZoneId zoneId) {
        return zoneId == null ? null : zoneId.getRules().getOffset(Instant.now(Clock.system(zoneId)));
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
            return Collections.singletonList(DayOfWeekEnumeration.NONE);
        } else if (actualDaysOfWeek.equals(EnumSet.of(DayTypeEnum.Monday, DayTypeEnum.Tuesday,
                DayTypeEnum.Wednesday, DayTypeEnum.Thursday, DayTypeEnum.Friday))) {
            return Collections.singletonList(DayOfWeekEnumeration.WEEKDAYS);
        } else if (actualDaysOfWeek.equals(EnumSet.of(DayTypeEnum.Saturday, DayTypeEnum.Sunday))) {
            return Collections.singletonList(DayOfWeekEnumeration.WEEKEND);
        } else if (actualDaysOfWeek.equals(EnumSet.of(DayTypeEnum.Monday, DayTypeEnum.Tuesday, DayTypeEnum.Wednesday,
                DayTypeEnum.Thursday, DayTypeEnum.Friday, DayTypeEnum.Saturday, DayTypeEnum.Sunday))) {
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
            }
        }

        return dayOfWeekEnumerations;
    }

    private static String netexModelName(NeptuneIdentifiedObject model) {
        if (model == null)
            return null;
        if (model instanceof StopArea) {
            return "StopArea";
        } else if (model instanceof AccessPoint) {
            return "AccessPoint";
        } else if (model instanceof Company) {
            return "Operator";
        } else if (model instanceof AccessLink) {
            return "AccessLink";
        } else if (model instanceof StopPoint) {
            return "StopPoint";
        } else if (model instanceof Network) {
            return "GroupOfLine";
        } else if (model instanceof Line) {
            return "Line";
        } else if (model instanceof Route) {
            return "Route";
        } else if (model instanceof GroupOfLine) {
            return "GroupOfLine";
        } else if (model instanceof JourneyPattern) {
            return "JourneyPattern";
        } else if (model instanceof ConnectionLink) {
            return "ConnectionLink";
        } else if (model instanceof Timetable) {
            return "Timetable";
        } else if (model instanceof VehicleJourney) {
            return "ServiceJourney";
        } else {
            return null;
        }
    }

    public static final int DEFAULT_START_INCLUSIVE = 1111111;
    public static final int DEFAULT_END_EXCLUSIVE = 8888888;

    public static int generateRandomId() {
        return generateRandomId(DEFAULT_START_INCLUSIVE, DEFAULT_END_EXCLUSIVE);
    }

    public static int generateRandomId(int startInclusive, int endExclusive) {
        return Math.abs((startInclusive) + new Random().nextInt(endExclusive));
    }

    public static String netexId(NeptuneIdentifiedObject model) {
        return model == null ? null : model.objectIdPrefix() + OBJECT_ID_SPLIT_CHAR + netexModelName(model) + OBJECT_ID_SPLIT_CHAR + model.objectIdSuffix();
    }

    public static String netexId(String objectIdPrefix, String elementName, String objectIdSuffix) {
        return objectIdPrefix + OBJECT_ID_SPLIT_CHAR + elementName + OBJECT_ID_SPLIT_CHAR + objectIdSuffix;
    }

    public static String objectIdPrefix(String objectId) {
        return objectId.split(OBJECT_ID_SPLIT_CHAR).length > 2 ? objectId.split(OBJECT_ID_SPLIT_CHAR)[0].trim() : "";
    }

    public static String objectIdSuffix(String objectId) {
        return objectId.split(OBJECT_ID_SPLIT_CHAR).length > 2 ? objectId.split(OBJECT_ID_SPLIT_CHAR)[2].trim() : "";
    }


}
