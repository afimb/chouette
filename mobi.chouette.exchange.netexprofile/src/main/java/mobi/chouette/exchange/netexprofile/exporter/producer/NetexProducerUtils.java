package mobi.chouette.exchange.netexprofile.exporter.producer;

import lombok.extern.log4j.Log4j;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j
public class NetexProducerUtils {

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

    public static OffsetDateTime convertToOffsetDateTime(Date date) {
        return date == null ? null : OffsetDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }

}
