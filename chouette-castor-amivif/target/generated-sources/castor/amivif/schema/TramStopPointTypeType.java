/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * An TRAM stop point
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class TramStopPointTypeType extends amivif.schema.StopPointTypeType 
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
     * Field _streetNumber.
     */
    private java.lang.String _streetNumber;

    /**
     * Field _ptDirection.
     */
    private amivif.schema.types.PTDirectionType _ptDirection;

    /**
     * Field _platformIdentifier.
     */
    private java.lang.String _platformIdentifier;


      //----------------/
     //- Constructors -/
    //----------------/

    public TramStopPointTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'platformIdentifier'.
     * 
     * @return the value of field 'PlatformIdentifier'.
     */
    public java.lang.String getPlatformIdentifier(
    ) {
        return this._platformIdentifier;
    }

    /**
     * Returns the value of field 'ptDirection'.
     * 
     * @return the value of field 'PtDirection'.
     */
    public amivif.schema.types.PTDirectionType getPtDirection(
    ) {
        return this._ptDirection;
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
     * Returns the value of field 'streetNumber'.
     * 
     * @return the value of field 'StreetNumber'.
     */
    public java.lang.String getStreetNumber(
    ) {
        return this._streetNumber;
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
     * Sets the value of field 'platformIdentifier'.
     * 
     * @param platformIdentifier the value of field
     * 'platformIdentifier'.
     */
    public void setPlatformIdentifier(
            final java.lang.String platformIdentifier) {
        this._platformIdentifier = platformIdentifier;
    }

    /**
     * Sets the value of field 'ptDirection'.
     * 
     * @param ptDirection the value of field 'ptDirection'.
     */
    public void setPtDirection(
            final amivif.schema.types.PTDirectionType ptDirection) {
        this._ptDirection = ptDirection;
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
     * Sets the value of field 'streetNumber'.
     * 
     * @param streetNumber the value of field 'streetNumber'.
     */
    public void setStreetNumber(
            final java.lang.String streetNumber) {
        this._streetNumber = streetNumber;
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
