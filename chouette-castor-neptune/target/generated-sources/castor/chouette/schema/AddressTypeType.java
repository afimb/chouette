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
public abstract class AddressTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _streetName.
     */
    private java.lang.String _streetName;

    /**
     * Field _countryCode.
     */
    private java.lang.String _countryCode;


      //----------------/
     //- Constructors -/
    //----------------/

    public AddressTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'countryCode'.
     * 
     * @return the value of field 'CountryCode'.
     */
    public java.lang.String getCountryCode(
    ) {
        return this._countryCode;
    }

    /**
     * Returns the value of field 'streetName'.
     * 
     * @return the value of field 'StreetName'.
     */
    public java.lang.String getStreetName(
    ) {
        return this._streetName;
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
     * Sets the value of field 'countryCode'.
     * 
     * @param countryCode the value of field 'countryCode'.
     */
    public void setCountryCode(
            final java.lang.String countryCode) {
        this._countryCode = countryCode;
    }

    /**
     * Sets the value of field 'streetName'.
     * 
     * @param streetName the value of field 'streetName'.
     */
    public void setStreetName(
            final java.lang.String streetName) {
        this._streetName = streetName;
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
