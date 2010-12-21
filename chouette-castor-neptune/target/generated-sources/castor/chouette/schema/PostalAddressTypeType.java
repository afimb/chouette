/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Full Description of an address
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class PostalAddressTypeType extends chouette.schema.AddressTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _province.
     */
    private java.lang.String _province;

    /**
     * Field _region.
     */
    private java.lang.String _region;

    /**
     * Field _town.
     */
    private java.lang.String _town;

    /**
     * Field _roadNumber.
     */
    private java.lang.String _roadNumber;

    /**
     * Field _houseNumber.
     */
    private java.lang.String _houseNumber;

    /**
     * Field _postalCode.
     */
    private java.lang.String _postalCode;


      //----------------/
     //- Constructors -/
    //----------------/

    public PostalAddressTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'houseNumber'.
     * 
     * @return the value of field 'HouseNumber'.
     */
    public java.lang.String getHouseNumber(
    ) {
        return this._houseNumber;
    }

    /**
     * Returns the value of field 'postalCode'.
     * 
     * @return the value of field 'PostalCode'.
     */
    public java.lang.String getPostalCode(
    ) {
        return this._postalCode;
    }

    /**
     * Returns the value of field 'province'.
     * 
     * @return the value of field 'Province'.
     */
    public java.lang.String getProvince(
    ) {
        return this._province;
    }

    /**
     * Returns the value of field 'region'.
     * 
     * @return the value of field 'Region'.
     */
    public java.lang.String getRegion(
    ) {
        return this._region;
    }

    /**
     * Returns the value of field 'roadNumber'.
     * 
     * @return the value of field 'RoadNumber'.
     */
    public java.lang.String getRoadNumber(
    ) {
        return this._roadNumber;
    }

    /**
     * Returns the value of field 'town'.
     * 
     * @return the value of field 'Town'.
     */
    public java.lang.String getTown(
    ) {
        return this._town;
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
     * Sets the value of field 'houseNumber'.
     * 
     * @param houseNumber the value of field 'houseNumber'.
     */
    public void setHouseNumber(
            final java.lang.String houseNumber) {
        this._houseNumber = houseNumber;
    }

    /**
     * Sets the value of field 'postalCode'.
     * 
     * @param postalCode the value of field 'postalCode'.
     */
    public void setPostalCode(
            final java.lang.String postalCode) {
        this._postalCode = postalCode;
    }

    /**
     * Sets the value of field 'province'.
     * 
     * @param province the value of field 'province'.
     */
    public void setProvince(
            final java.lang.String province) {
        this._province = province;
    }

    /**
     * Sets the value of field 'region'.
     * 
     * @param region the value of field 'region'.
     */
    public void setRegion(
            final java.lang.String region) {
        this._region = region;
    }

    /**
     * Sets the value of field 'roadNumber'.
     * 
     * @param roadNumber the value of field 'roadNumber'.
     */
    public void setRoadNumber(
            final java.lang.String roadNumber) {
        this._roadNumber = roadNumber;
    }

    /**
     * Sets the value of field 'town'.
     * 
     * @param town the value of field 'town'.
     */
    public void setTown(
            final java.lang.String town) {
        this._town = town;
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
