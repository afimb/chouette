/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * An airport
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class AirportStopPointTypeType extends chouette.schema.StopPointTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _terminalIdentifier.
     */
    private java.lang.String _terminalIdentifier;

    /**
     * Field _gateIdentifier.
     */
    private java.lang.String _gateIdentifier;


      //----------------/
     //- Constructors -/
    //----------------/

    public AirportStopPointTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'gateIdentifier'.
     * 
     * @return the value of field 'GateIdentifier'.
     */
    public java.lang.String getGateIdentifier(
    ) {
        return this._gateIdentifier;
    }

    /**
     * Returns the value of field 'terminalIdentifier'.
     * 
     * @return the value of field 'TerminalIdentifier'.
     */
    public java.lang.String getTerminalIdentifier(
    ) {
        return this._terminalIdentifier;
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
     * Sets the value of field 'gateIdentifier'.
     * 
     * @param gateIdentifier the value of field 'gateIdentifier'.
     */
    public void setGateIdentifier(
            final java.lang.String gateIdentifier) {
        this._gateIdentifier = gateIdentifier;
    }

    /**
     * Sets the value of field 'terminalIdentifier'.
     * 
     * @param terminalIdentifier the value of field
     * 'terminalIdentifier'.
     */
    public void setTerminalIdentifier(
            final java.lang.String terminalIdentifier) {
        this._terminalIdentifier = terminalIdentifier;
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
