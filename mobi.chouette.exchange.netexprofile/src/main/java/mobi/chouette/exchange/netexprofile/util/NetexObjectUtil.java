package mobi.chouette.exchange.netexprofile.util;

import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

public class NetexObjectUtil {


    public static void addDayTypeRef(NetexReferential referential, String objectId, DayType dayType) {
        if (dayType == null) {
            throw new NullPointerException("Unknown day type : " + objectId);
        }
        if (!referential.getDayTypes().containsKey(objectId)) {
            referential.getDayTypes().put(objectId, dayType);
        }
    }

    public static DayType getDayType(NetexReferential referential, String objectId) {
        DayType dayType = referential.getDayTypes().get(objectId);
        if (dayType == null) {
            throw new NullPointerException("Unknown day type : " + objectId);
        }
        return dayType;
    }

    public static void addDayTypeAssignmentRef(NetexReferential referential, String objectId, DayTypeAssignment dayTypeAssignment) {
        if (dayTypeAssignment == null) {
            throw new NullPointerException("Unknown day type assignment : " + objectId);
        }
        referential.getDayTypeAssignments().add(dayTypeAssignment);
    }

    public static List<DayTypeAssignment> getDayTypeAssignment(NetexReferential referential) {
       
        return referential.getDayTypeAssignments();
    }

    public static void addOperatingPeriodRef(NetexReferential referential, String objectId, OperatingPeriod operatingPeriod) {
        if (operatingPeriod == null) {
            throw new NullPointerException("Unknown operating period : " + objectId);
        }
        if (!referential.getOperatingPeriods().containsKey(objectId)) {
            referential.getOperatingPeriods().put(objectId, operatingPeriod);
        }
    }

    public static OperatingPeriod getOperatingPeriod(NetexReferential referential, String objectId) {
        OperatingPeriod operatingPeriod = referential.getOperatingPeriods().get(objectId);
        if (operatingPeriod == null) {
            throw new NullPointerException("Unknown operating period : " + objectId);
        }
        return operatingPeriod;
    }

    public static void addOperatingDayRef(NetexReferential referential, String objectId, OperatingDay operatingDay) {
        if (operatingDay == null) {
            throw new NullPointerException("Unknown operating day : " + objectId);
        }
        if (!referential.getOperatingDays().containsKey(objectId)) {
            referential.getOperatingDays().put(objectId, operatingDay);
        }
    }

    public static OperatingDay getOperatingDay(NetexReferential referential, String objectId) {
        OperatingDay operatingDay = referential.getOperatingDays().get(objectId);
        if (operatingDay == null) {
            throw new NullPointerException("Unknown operating day : " + objectId);
        }
        return operatingDay;
    }

  

    public static <T> List<T> getFrames(Class<T> clazz, List<JAXBElement<? extends Common_VersionFrameStructure>> dataObjectFrames) {
        List<T> foundFrames = new ArrayList<>();

        for (JAXBElement<? extends Common_VersionFrameStructure> frame : dataObjectFrames) {
            if (frame.getValue().getClass().equals(clazz)) {
                foundFrames.add(clazz.cast(frame.getValue()));
            }
        }

        return foundFrames;
    }

}
