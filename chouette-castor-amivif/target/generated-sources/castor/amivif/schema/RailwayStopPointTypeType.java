/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * An Railwaystop point
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class RailwayStopPointTypeType extends amivif.schema.StopPointTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _stationInternalDivision.
     */
    private java.lang.String _stationInternalDivision;

    /**
     * Field _platformIdentifier.
     */
    private java.lang.String _platformIdentifier;


      //----------------/
     //- Constructors -/
    //----------------/

    public RailwayStopPointTypeType() {
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
     * Returns the value of field 'stationInternalDivision'.
     * 
     * @return the value of field 'StationInternalDivision'.
     */
    public java.lang.String getStationInternalDivision(
    ) {
        return this._stationInternalDivision;
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
     * Sets the value of field 'stationInternalDivision'.
     * 
     * @param stationInternalDivision the value of field
     * 'stationInternalDivision'.
     */
    public void setStationInternalDivision(
            final java.lang.String stationInternalDivision) {
        this._stationInternalDivision = stationInternalDivision;
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
