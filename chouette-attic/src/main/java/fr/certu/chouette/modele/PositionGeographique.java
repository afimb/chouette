package fr.certu.chouette.modele;

import java.math.BigDecimal;
import java.util.Date;
//import com.vividsolutions.jts.geom.Point;
import chouette.schema.Address;
import chouette.schema.AreaCentroid;
import chouette.schema.ProjectedPoint;
import chouette.schema.Registration;
import chouette.schema.StopArea;
import chouette.schema.StopAreaExtension;
import chouette.schema.types.ChouetteAreaType;
import chouette.schema.types.LongLatTypeType;

public class PositionGeographique extends BaseObjet {

    private AreaCentroid areaCentroid;
    public StopArea stopArea;
    public Long idParent;
    /*public Point geom;
    
    public void setGeom(Point geom) {
    this.geom = geom;
    }
    
    public Point getGeom() {
    return this.geom;
    }*/
    
    public PositionGeographique() {
        super();
        
        areaCentroid = new AreaCentroid();
        areaCentroid.setProjectedPoint(new ProjectedPoint());
        areaCentroid.setAddress(new Address());
        
        stopArea = new StopArea();
        StopAreaExtension extension = new StopAreaExtension();
        extension.setRegistration(new Registration());
        stopArea.setStopAreaExtension(extension);
    }
    
    public static PositionGeographique creerArretPhysique(String name) {
        PositionGeographique positionGeographique = new PositionGeographique();
        
        positionGeographique.setName(name);
// 		ne pas affecter d'info géo par défaut		
        positionGeographique.setAreaType(ChouetteAreaType.BOARDINGPOSITION);
        
        return positionGeographique;
    }
    
    public boolean canContain(ChouetteAreaType contenu) {
        if (contenu == null) {
            return false;
        }
        
        ChouetteAreaType contenant = getAreaType();
        if (contenant == null) {
            return false;
        }
        
        return (ChouetteAreaType.STOPPLACE.equals(contenant) && PositionGeographique.isZoneCategory(contenu))
                || (PositionGeographique.isZoneCategory(contenant) && PositionGeographique.isArretPhysiqueCategory(contenu));
    }
    
    public boolean isEmptyAreaCentroid() {
        return areaCentroid.getLongitude() == null
                && areaCentroid.getLatitude() == null
                && (areaCentroid.getAddress() == null || (areaCentroid.getAddress().getStreetName() == null && areaCentroid.getAddress().getCountryCode() == null))
                && (areaCentroid.getProjectedPoint() == null || (areaCentroid.getProjectedPoint().getX() == null && areaCentroid.getProjectedPoint().getY() == null));
    }
    
    private static boolean isZoneCategory(ChouetteAreaType areaType) {
        return ChouetteAreaType.STOPPLACE.equals(areaType) || ChouetteAreaType.COMMERCIALSTOPPOINT.equals(areaType);
    }

    private static boolean isArretPhysiqueCategory(ChouetteAreaType areaType) {
        return ChouetteAreaType.QUAY.equals(areaType) || ChouetteAreaType.BOARDINGPOSITION.equals(areaType);
    }
    
    public boolean isArretPhysiqueCategory() {
        return PositionGeographique.isArretPhysiqueCategory(this.getAreaType());
    }
    
    public boolean isZoneCategory() {
        return PositionGeographique.isZoneCategory(this.getAreaType());
    }
    
    public int getFareCode() {
        return stopArea.getStopAreaExtension().getFareCode();
    }
    
    public void setFareCode(int fareCode) {
        stopArea.getStopAreaExtension().setFareCode(fareCode);
    }
    
    public String getRegistrationNumber() {
        return stopArea.getStopAreaExtension().getRegistration().getRegistrationNumber();
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        stopArea.getStopAreaExtension().getRegistration().setRegistrationNumber(registrationNumber);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getIdParent()
     */
    public Long getIdParent() {
        return idParent;
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setIdParent(java.lang.Long)
     */
    public void setIdParent(Long idParent) {
        this.idParent = idParent;
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getObjectVersion()
     */
    public int getObjectVersion() {
        setObjectVersion((int) stopArea.getObjectVersion());
        return (int) stopArea.getObjectVersion();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setObjectVersion(int)
     */
    public void setObjectVersion(int objectVersion) {
        if (objectVersion >= 1) {
            stopArea.setObjectVersion(objectVersion);
        } else {
            stopArea.setObjectVersion(1);
        }
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getCountryCode()
     */
    public String getCountryCode() {
        return areaCentroid.getAddress().getCountryCode();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getStreetName()
     */
    public String getStreetName() {
        return areaCentroid.getAddress().getStreetName();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setCountryCode(java.lang.String)
     */
    public void setCountryCode(String countryCode) {
        areaCentroid.getAddress().setCountryCode(countryCode);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setStreetName(java.lang.String)
     */
    public void setStreetName(String streetName) {
        areaCentroid.getAddress().setStreetName(streetName);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getProjectionType()
     */
    public String getProjectionType() {
        return areaCentroid.getProjectedPoint().getProjectionType();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getX()
     */
    public BigDecimal getX() {
        return areaCentroid.getProjectedPoint().getX();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getY()
     */
    public BigDecimal getY() {
        return areaCentroid.getProjectedPoint().getY();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setProjectionType(java.lang.String)
     */
    public void setProjectionType(String projectionType) {
        areaCentroid.getProjectedPoint().setProjectionType(projectionType);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setX(java.math.BigDecimal)
     */
    public void setX(BigDecimal x) {
        areaCentroid.getProjectedPoint().setX(x);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setY(java.math.BigDecimal)
     */
    public void setY(BigDecimal y) {
        areaCentroid.getProjectedPoint().setY(y);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getLatitude()
     */
    public BigDecimal getLatitude() {
        return areaCentroid.getLatitude();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getLongitude()
     */
    public BigDecimal getLongitude() {
        return areaCentroid.getLongitude();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getLongLatType()
     */
    public LongLatTypeType getLongLatType() {
        return areaCentroid.getLongLatType();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setLatitude(java.math.BigDecimal)
     */
    public void setLatitude(BigDecimal latitude) {
        areaCentroid.setLatitude(latitude);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setLongitude(java.math.BigDecimal)
     */
    public void setLongitude(BigDecimal longitude) {
        areaCentroid.setLongitude(longitude);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setLongLatType(chouette.schema.types.LongLatTypeType)
     */
    public void setLongLatType(LongLatTypeType longLatType) {
        areaCentroid.setLongLatType(longLatType);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getComment()
     */
    public String getComment() {
        return stopArea.getComment();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getCreationTime()
     */
    public Date getCreationTime() {
        return stopArea.getCreationTime();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getCreatorId()
     */
    public String getCreatorId() {
        return stopArea.getCreatorId();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getName()
     */
    public String getName() {
        return stopArea.getName();
    }

    /*
     * Return a string value containing the concatenation of :
     *  - name
     *  - zip code
     *  - street name
     */
    public String getFullName() {
        String res = stopArea.getName();
        if (areaCentroid != null && areaCentroid.getAddress() != null) {
            if (areaCentroid.getAddress().getCountryCode() != null) {
                res += " " + areaCentroid.getAddress().getCountryCode();
            }
            if (areaCentroid.getAddress().getStreetName() != null) {
                res += " " + areaCentroid.getAddress().getStreetName();
            }
        }
        return res;
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getObjectId()
     */
    public String getObjectId() {
        return stopArea.getObjectId();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setComment(java.lang.String)
     */
    public void setComment(String comment) {
        stopArea.setComment(comment);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setCreationTime(java.util.Date)
     */
    public void setCreationTime(Date creationTime) {
        stopArea.setCreationTime(creationTime);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setCreatorId(java.lang.String)
     */
    public void setCreatorId(String creatorId) {
        stopArea.setCreatorId(creatorId);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setName(java.lang.String)
     */
    public void setName(String name) {
        stopArea.setName(name);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setObjectId(java.lang.String)
     */
    public void setObjectId(String objectId) {
        stopArea.setObjectId(objectId);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getAreaType()
     */
    public ChouetteAreaType getAreaType() {
        return stopArea.getStopAreaExtension().getAreaType();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getNearestTopicName()
     */
    public String getNearestTopicName() {
        return stopArea.getStopAreaExtension().getNearestTopicName();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setAreaType(chouette.schema.types.ChouetteAreaType)
     */
    public void setAreaType(ChouetteAreaType areaType) {
        stopArea.getStopAreaExtension().setAreaType(areaType);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setNearestTopicName(java.lang.String)
     */
    public void setNearestTopicName(String nearestTopicName) {
        stopArea.getStopAreaExtension().setNearestTopicName(nearestTopicName);
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getAreaCentroid()
     */
    public AreaCentroid getAreaCentroid() {
        return areaCentroid;
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setAreaCentroid(chouette.schema.AreaCentroid)
     */
    public void setAreaCentroid(final AreaCentroid areaCentroid) {
        if (areaCentroid == null) {
            this.areaCentroid = new AreaCentroid();
            this.areaCentroid.setProjectedPoint(new ProjectedPoint());
            this.areaCentroid.setAddress(new Address());
        } else {
            this.areaCentroid = areaCentroid;
            if (areaCentroid.getProjectedPoint() == null) {
                this.areaCentroid.setProjectedPoint(new ProjectedPoint());
            }
            if (areaCentroid.getAddress() == null) {
                this.areaCentroid.setAddress(new Address());
            }
        }
    }
    
    public static boolean isExtensionDefinie(final StopArea stopArea) {
        StopAreaExtension extension = stopArea.getStopAreaExtension();
        return extension != null
                && (extension.hasFareCode()
                || (extension.getNearestTopicName() != null && !extension.getNearestTopicName().isEmpty())
                || (extension.getRegistration() != null
                && extension.getRegistration().getRegistrationNumber() != null
                && !extension.getRegistration().getRegistrationNumber().isEmpty()));
    }

    public static boolean isRegistrationNumberDefinie(final StopArea stopArea) {
        StopAreaExtension extension = stopArea.getStopAreaExtension();
        return extension != null
                && extension.getRegistration() != null
                && extension.getRegistration().getRegistrationNumber() != null
                && !extension.getRegistration().getRegistrationNumber().isEmpty();
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#getStopArea()
     */
    public StopArea getStopArea() {
        return stopArea;
    }

    /* (non-Javadoc)
     * @see fr.certu.chouette.modele.interne.IPositionGeographique#setStopArea(chouette.schema.StopArea)
     */
    public void setStopArea(StopArea stopArea) {
        if (stopArea == null) {
            this.stopArea = new StopArea();
            StopAreaExtension extension = new StopAreaExtension();
            extension.setRegistration(new Registration());
            this.stopArea.setStopAreaExtension(extension);
        } else {
            this.stopArea = stopArea;
            if (stopArea.getStopAreaExtension() == null) {
                StopAreaExtension extension = new StopAreaExtension();
                extension.setRegistration(new Registration());
                this.stopArea.setStopAreaExtension(extension);
            } else {
                this.stopArea.setStopAreaExtension(stopArea.getStopAreaExtension());
                if (stopArea.getStopAreaExtension().getRegistration() == null) {
                    this.stopArea.getStopAreaExtension().setRegistration(new Registration());
                }
            }
        }
    }
}
