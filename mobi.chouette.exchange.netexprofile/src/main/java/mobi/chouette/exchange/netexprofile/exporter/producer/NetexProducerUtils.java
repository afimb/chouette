package mobi.chouette.exchange.netexprofile.exporter.producer;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.rutebanken.netex.model.DayOfWeekEnumeration;
import org.rutebanken.netex.model.OrganisationTypeEnumeration;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.NeptuneIdentifiedObject;
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

    private static AtomicInteger idCounter = new AtomicInteger(1);

    public static int generateSequentialId() {
        return idCounter.getAndIncrement();
    }

  
    public static String netexId(String objectIdPrefix, String elementName, String objectIdSuffix) {
        return objectIdPrefix + OBJECT_ID_SPLIT_CHAR + elementName + OBJECT_ID_SPLIT_CHAR + objectIdSuffix;
    }
    
    
    
    public static String translateObjectId(String original, String newType) {
    	return original.replaceAll(original, newType);
    }
    
    public static String netexId(NeptuneIdentifiedObject chouetteObject, String newType) {
    	return translateObjectId(chouetteObject.getObjectId(), newType);
    }

    public static String objectIdPrefix(String objectId) {
        return objectId.split(OBJECT_ID_SPLIT_CHAR).length > 2 ? objectId.split(OBJECT_ID_SPLIT_CHAR)[0].trim() : "";
    }

    public static String objectIdSuffix(String objectId) {
        return objectId.split(OBJECT_ID_SPLIT_CHAR).length > 2 ? objectId.split(OBJECT_ID_SPLIT_CHAR)[2].trim() : "";
    }


}
