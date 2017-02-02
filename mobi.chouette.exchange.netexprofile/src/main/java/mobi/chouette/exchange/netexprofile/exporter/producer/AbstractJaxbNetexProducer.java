package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.NeptuneIdentifiedObject;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;


public abstract class AbstractJaxbNetexProducer<T extends DataManagedObjectStructure, U extends NeptuneIdentifiedObject> {

    public static final String NETEX_DATA_OJBECT_VERSION = "1";
    public static final String DEFAULT_ZONE_ID = "UTC";

    public static ObjectFactory netexFactory = null;

    static {
        try {
            netexFactory = new ObjectFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract T produce(U modelObject, boolean addExtension);

    public void populateFromModel(T target, U source) {
        target.setId(source.getObjectId());

        if (source.getObjectVersion() > 0) {
            target.setVersion(String.valueOf(source.getObjectVersion()));
        } else {
            target.setVersion(NETEX_DATA_OJBECT_VERSION);
        }
        if (source.getCreationTime() != null) {
            target.setCreated(toOffsetDateTime(source.getCreationTime()));
        }
/*
        if (source.getCreatorId() != null) {
            target.setCreatorId(source.getCreatorId());
        }
*/
    }

    protected OffsetDateTime toOffsetDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return OffsetDateTime.ofInstant(date.toInstant(), ZoneId.of(DEFAULT_ZONE_ID));
    }

    protected MultilingualString getMultilingualString(String value) {
        return netexFactory.createMultilingualString()
                .withValue(value);
    }

    protected String getNotEmptyString(String value) {
        return value == null || value.trim().isEmpty() ? null : value;
    }

}
