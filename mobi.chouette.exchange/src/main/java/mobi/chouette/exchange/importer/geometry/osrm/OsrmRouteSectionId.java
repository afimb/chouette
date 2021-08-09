package mobi.chouette.exchange.importer.geometry.osrm;

import com.vividsolutions.jts.geom.Coordinate;
import mobi.chouette.model.type.TransportModeNameEnum;

import java.util.Objects;

/**
 * A unique id for a route section between two geographic points for a given transport mode.
 */

public class OsrmRouteSectionId {

    private final Coordinate from;
    private final Coordinate to;
    private final TransportModeNameEnum transportMode;

    public OsrmRouteSectionId(Coordinate from, Coordinate to, TransportModeNameEnum transportMode) {
        this.from = from;
        this.to = to;
        this.transportMode = transportMode;
    }


    public Coordinate getFrom() {
        return from;
    }

    public Coordinate getTo() {
        return to;
    }

    public TransportModeNameEnum getTransportMode() {
        return transportMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OsrmRouteSectionId that = (OsrmRouteSectionId) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) &&
                transportMode == that.transportMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, transportMode);
    }


}
