/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * An metro stop point
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class MetroStopPointTypeType extends chouette.schema.StopPointTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _lineName.
     */
    private java.lang.String _lineName;

    /**
     * Field _lineNumber.
     */
    private java.lang.String _lineNumber;

    /**
     * Field _platformIdentifier.
     */
    private java.lang.String _platformIdentifier;

    /**
     * Field _ptDirection.
     */
    private chouette.schema.types.PTDirectionType _ptDirection;


      //----------------/
     //- Constructors -/
    //----------------/

    public MetroStopPointTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'lineName'.
     * 
     * @return the value of field 'LineName'.
     */
    public java.lang.String getLineName(
    ) {
        return this._lineName;
    }

    /**
     * Returns the value of field 'lineNumber'.
     * 
     * @return the value of field 'LineNumber'.
     */
    public java.lang.String getLineNumber(
    ) {
        return this._lineNumber;
    }

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
    public chouette.schema.types.PTDirectionType getPtDirection(
    ) {
        return this._ptDirection;
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
     * Sets the value of field 'lineName'.
     * 
     * @param lineName the value of field 'lineName'.
     */
    public void setLineName(
            final java.lang.String lineName) {
        this._lineName = lineName;
    }

    /**
     * Sets the value of field 'lineNumber'.
     * 
     * @param lineNumber the value of field 'lineNumber'.
     */
    public void setLineNumber(
            final java.lang.String lineNumber) {
        this._lineNumber = lineNumber;
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
            final chouette.schema.types.PTDirectionType ptDirection) {
        this._ptDirection = ptDirection;
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
