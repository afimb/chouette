package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.NeptuneIdentifiedObject;
import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;

import java.sql.Time;
import java.time.*;
import java.util.Date;


public abstract class AbstractJaxbNetexProducer<T extends DataManagedObjectStructure, U extends NeptuneIdentifiedObject> {

    public static final String NETEX_DATA_OJBECT_VERSION = "0";
    public static final ZoneId LOCAL_ZONE_ID = ZoneId.of("Europe/Oslo");
    public static final String DEFAULT_ZONE_ID = "UTC";

    public static ObjectFactory netexFactory = null;

    static {
        try {
            netexFactory = new ObjectFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO consider adding an optional collection parameter, e.g. routes on a line, and enable this again
    //public abstract T produce(U modelObject, boolean addExtension, E... array);

    public void populateFromModel(T target, U source) {
        target.setId(source.getObjectId());
        target.setVersion(source.getObjectVersion() > 0 ? String.valueOf(source.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        // TODO make this a parameter option
/*
        if (source.getCreationTime() != null) {
            target.setCreated(toOffsetDateTime(source.getCreationTime()));
        }
*/
/*
        if (source.getCreatorId() != null) {
            target.setCreatorId(source.getCreatorId());
        }
*/
    }

    protected ZoneOffset getZoneOffset(ZoneId zoneId) {
        return zoneId == null ? null : zoneId.getRules().getOffset(Instant.now(Clock.system(zoneId)));
    }

    protected OffsetDateTime toOffsetDateTime(Date date) {
        return date == null ? null : OffsetDateTime.ofInstant(date.toInstant(), ZoneId.of(DEFAULT_ZONE_ID));
    }

    protected OffsetTime toOffsetTimeLocal(Time time) {
        return time == null ? null : OffsetTime.of(time.toLocalTime(), ZoneOffset.UTC);
    }

    protected OffsetTime toOffsetTimeUtc(Time time) {
        return time == null ? null : time.toLocalTime().atOffset(getZoneOffset(LOCAL_ZONE_ID)).withOffsetSameInstant(ZoneOffset.UTC);
    }

    public static AllVehicleModesOfTransportEnumeration toVehicleModeOfTransportEnum(String value) {
        if (value == null)
            return null;
        else if (value.equals("Air"))
            return AllVehicleModesOfTransportEnumeration.AIR;
        else if (value.equals("Train"))
            return AllVehicleModesOfTransportEnumeration.RAIL;
        else if (value.equals("LongDistanceTrain"))
            return AllVehicleModesOfTransportEnumeration.INTERCITY_RAIL;
        else if (value.equals("LocalTrain"))
            return AllVehicleModesOfTransportEnumeration.URBAN_RAIL;
        else if (value.equals("Metro"))
            return AllVehicleModesOfTransportEnumeration.METRO;
        else if (value.equals("Tramway"))
            return AllVehicleModesOfTransportEnumeration.TRAM;
        else if (value.equals("Coach"))
            return AllVehicleModesOfTransportEnumeration.COACH;
        else if (value.equals("Bus"))
            return AllVehicleModesOfTransportEnumeration.BUS;
        else if (value.equals("Ferry"))
            return AllVehicleModesOfTransportEnumeration.WATER;
        else if (value.equals("Walk"))
            return AllVehicleModesOfTransportEnumeration.SELF_DRIVE;
        else if (value.equals("Trolleybus"))
            return AllVehicleModesOfTransportEnumeration.TROLLEY_BUS;
        else if (value.equals("Taxi"))
            return AllVehicleModesOfTransportEnumeration.TAXI;
        else if (value.equals("Other"))
            return AllVehicleModesOfTransportEnumeration.UNKNOWN;
        else
            return AllVehicleModesOfTransportEnumeration.UNKNOWN;
    }

    protected MultilingualString getMultilingualString(String value) {
        return netexFactory.createMultilingualString()
                .withValue(value);
    }

    protected String getNotEmptyString(String value) {
        return value == null || value.trim().isEmpty() ? null : value;
    }

}