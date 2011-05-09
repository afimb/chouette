package fr.certu.chouette.modele;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
//import com.vividsolutions.jts.geom.LineString;
import org.exolab.castor.types.Duration;

import chouette.schema.ConnectionLink;
import chouette.schema.types.ConnectionLinkTypeType;

public class Correspondance extends BaseObjet {

    private ConnectionLink connectionLink;
    private Long idDepart;
    private Long idArrivee;

    /*private LineString geom;
    
    public void setGeom(LineString geom) {
    this.geom = geom;
    }
    
    public LineString getGeom() {
    return this.geom;
    }*/
    public Correspondance() {
        connectionLink = new ConnectionLink();
    }

    public void setConnectionlink(final ConnectionLink connectionLink) {
        this.connectionLink = connectionLink;
    }

    public ConnectionLink getConnectionLink() {
        return connectionLink;
    }

    public Long getIdArrivee() {
        return idArrivee;
    }

    public void setIdArrivee(Long idArrivee) {
        this.idArrivee = idArrivee;
    }

    public Long getIdDepart() {
        return idDepart;
    }

    public void setIdDepart(Long idDepart) {
        this.idDepart = idDepart;
    }

    public void setIdDepartArrivee(Long idDepart, Long idArrivee) {
        if ((idDepart == null && idArrivee != null)
                || (idDepart != null && idArrivee == null)) {
            throw new IllegalArgumentException("Les références aux positions sont toutes 2 nulles ou toutes 2 définies");
        }

        this.idDepart = idDepart;
        this.idArrivee = idArrivee;
    }

    public String getObjectId() {
        return connectionLink.getObjectId();
    }

    public String getComment() {
        return connectionLink.getComment();
    }

    public Date getCreationTime() {
        return connectionLink.getCreationTime();
    }

    public String getCreatorId() {
        return connectionLink.getCreatorId();
    }

    public Date getDefaultDuration() {
        return toDate(connectionLink.getDefaultDuration());
    }

    public String getEndOfLink() {
        return connectionLink.getEndOfLink();
    }

    public Date getFrequentTravellerDuration() {
        return toDate(connectionLink.getFrequentTravellerDuration());
    }

    public boolean getLiftAvailability() {
        return connectionLink.getLiftAvailability();
    }

    public BigDecimal getLinkDistance() {
        return connectionLink.getLinkDistance();
    }

    public ConnectionLinkTypeType getLinkType() {
        return connectionLink.getLinkType();
    }

    public boolean getMobilityRestrictedSuitability() {
        return connectionLink.getMobilityRestrictedSuitability();
    }

    public Date getMobilityRestrictedTravellerDuration() {
        return toDate(connectionLink.getMobilityRestrictedTravellerDuration());
    }

    public String getName() {
        return connectionLink.getName();
    }

    public int getObjectVersion() {
        setObjectVersion((int) connectionLink.getObjectVersion());
        return (int) connectionLink.getObjectVersion();
    }

    public Date getOccasionalTravellerDuration() {
        return toDate(connectionLink.getOccasionalTravellerDuration());
    }

    public boolean getStairsAvailability() {
        return connectionLink.getStairsAvailability();
    }

    public String getStartOfLink() {
        return connectionLink.getStartOfLink();
    }

    public void setComment(String comment) {
        connectionLink.setComment(comment);
    }

    public void setCreationTime(Date creationTime) {
        connectionLink.setCreationTime(creationTime);
    }

    public void setCreatorId(String creatorId) {
        connectionLink.setCreatorId(creatorId);
    }

    public void setDefaultDuration(Date defaultDuration) {
        connectionLink.setDefaultDuration(toCastorDuration(defaultDuration));
    }

    public void setEndOfLink(String endOfLink) {
        connectionLink.setEndOfLink(endOfLink);
    }

    public void setFrequentTravellerDuration(Date frequentTravellerDuration) {
        connectionLink.setFrequentTravellerDuration(toCastorDuration(frequentTravellerDuration));
    }

    public void setLiftAvailability(boolean liftAvailability) {
        connectionLink.setLiftAvailability(liftAvailability);
    }

    public void setLinkDistance(BigDecimal linkDistance) {
        connectionLink.setLinkDistance(linkDistance);
    }

    public void setLinkType(ConnectionLinkTypeType linkType) {
        connectionLink.setLinkType(linkType);
    }

    public void setMobilityRestrictedSuitability(boolean mobilityRestrictedSuitability) {
        connectionLink.setMobilityRestrictedSuitability(mobilityRestrictedSuitability);
    }

    public void setMobilityRestrictedTravellerDuration(Date mobilityRestrictedTravellerDuration) {
        connectionLink.setMobilityRestrictedTravellerDuration(toCastorDuration(mobilityRestrictedTravellerDuration));
    }

    public void setName(String name) {
        connectionLink.setName(name);
    }

    public void setObjectId(String objectId) {
        connectionLink.setObjectId(objectId);
    }

    public void setObjectVersion(int objectVersion) {
        if (objectVersion >= 1) {
            connectionLink.setObjectVersion(objectVersion);
        } else {
            connectionLink.setObjectVersion(1);
        }
    }

    public void setOccasionalTravellerDuration(Date occasionalTravellerDuration) {
        connectionLink.setOccasionalTravellerDuration(toCastorDuration(occasionalTravellerDuration));
    }

    public void setStairsAvailability(boolean stairsAvailability) {
        connectionLink.setStairsAvailability(stairsAvailability);
    }

    public void setStartOfLink(String startOfLink) {
        connectionLink.setStartOfLink(startOfLink);
    }

    private Date toDate(Duration duration) {
        if (duration == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, duration.getHour());
        calendar.set(Calendar.MINUTE, duration.getMinute());
        calendar.set(Calendar.SECOND, duration.getSeconds());
        return calendar.getTime();
    }

    private Duration toCastorDuration(Date date) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Duration duration = new Duration(0L);
        duration.setHour((short) calendar.get(Calendar.HOUR_OF_DAY));
        duration.setMinute((short) calendar.get(Calendar.MINUTE));
        duration.setSeconds((short) calendar.get(Calendar.SECOND));
        return duration;
    }
}
