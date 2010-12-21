/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * General point used to build any kind of point
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class PointTypeType extends chouette.schema.LocationTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _longitude.
     */
    private java.math.BigDecimal _longitude;

    /**
     * Field _latitude.
     */
    private java.math.BigDecimal _latitude;

    /**
     * Field _longLatType.
     */
    private chouette.schema.types.LongLatTypeType _longLatType;

    /**
     * Field _address.
     */
    private chouette.schema.Address _address;

    /**
     * Field _projectedPoint.
     */
    private chouette.schema.ProjectedPoint _projectedPoint;

    /**
     * Field _containedIn.
     */
    private java.lang.String _containedIn;


      //----------------/
     //- Constructors -/
    //----------------/

    public PointTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'address'.
     * 
     * @return the value of field 'Address'.
     */
    public chouette.schema.Address getAddress(
    ) {
        return this._address;
    }

    /**
     * Returns the value of field 'containedIn'.
     * 
     * @return the value of field 'ContainedIn'.
     */
    public java.lang.String getContainedIn(
    ) {
        return this._containedIn;
    }

    /**
     * Returns the value of field 'latitude'.
     * 
     * @return the value of field 'Latitude'.
     */
    public java.math.BigDecimal getLatitude(
    ) {
        return this._latitude;
    }

    /**
     * Returns the value of field 'longLatType'.
     * 
     * @return the value of field 'LongLatType'.
     */
    public chouette.schema.types.LongLatTypeType getLongLatType(
    ) {
        return this._longLatType;
    }

    /**
     * Returns the value of field 'longitude'.
     * 
     * @return the value of field 'Longitude'.
     */
    public java.math.BigDecimal getLongitude(
    ) {
        return this._longitude;
    }

    /**
     * Returns the value of field 'projectedPoint'.
     * 
     * @return the value of field 'ProjectedPoint'.
     */
    public chouette.schema.ProjectedPoint getProjectedPoint(
    ) {
        return this._projectedPoint;
    }

    /**
     * Method isValid.
     * 
     * @return true if this object is valid according to the schema
     */
    public boolean isValid(
    ) {
        try {
            validate();
        } catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    }

    /**
     * Sets the value of field 'address'.
     * 
     * @param address the value of field 'address'.
     */
    public void setAddress(
            final chouette.schema.Address address) {
        this._address = address;
    }

    /**
     * Sets the value of field 'containedIn'.
     * 
     * @param containedIn the value of field 'containedIn'.
     */
    public void setContainedIn(
            final java.lang.String containedIn) {
        this._containedIn = containedIn;
    }

    /**
     * Sets the value of field 'latitude'.
     * 
     * @param latitude the value of field 'latitude'.
     */
    public void setLatitude(
            final java.math.BigDecimal latitude) {
        this._latitude = latitude;
    }

    /**
     * Sets the value of field 'longLatType'.
     * 
     * @param longLatType the value of field 'longLatType'.
     */
    public void setLongLatType(
            final chouette.schema.types.LongLatTypeType longLatType) {
        this._longLatType = longLatType;
    }

    /**
     * Sets the value of field 'longitude'.
     * 
     * @param longitude the value of field 'longitude'.
     */
    public void setLongitude(
            final java.math.BigDecimal longitude) {
        this._longitude = longitude;
    }

    /**
     * Sets the value of field 'projectedPoint'.
     * 
     * @param projectedPoint the value of field 'projectedPoint'.
     */
    public void setProjectedPoint(
            final chouette.schema.ProjectedPoint projectedPoint) {
        this._projectedPoint = projectedPoint;
    }

    /**
     * 
     * 
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void validate(
    )
    throws org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    }

}
