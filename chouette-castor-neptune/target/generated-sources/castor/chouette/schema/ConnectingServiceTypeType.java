/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Connecting service description
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class ConnectingServiceTypeType extends chouette.schema.TridentObjectTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _minimumConnectingTime.
     */
    private org.exolab.castor.types.Duration _minimumConnectingTime;

    /**
     * Field _comment.
     */
    private java.lang.String _comment;


      //----------------/
     //- Constructors -/
    //----------------/

    public ConnectingServiceTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'comment'.
     * 
     * @return the value of field 'Comment'.
     */
    public java.lang.String getComment(
    ) {
        return this._comment;
    }

    /**
     * Returns the value of field 'minimumConnectingTime'.
     * 
     * @return the value of field 'MinimumConnectingTime'.
     */
    public org.exolab.castor.types.Duration getMinimumConnectingTime(
    ) {
        return this._minimumConnectingTime;
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
     * Sets the value of field 'comment'.
     * 
     * @param comment the value of field 'comment'.
     */
    public void setComment(
            final java.lang.String comment) {
        this._comment = comment;
    }

    /**
     * Sets the value of field 'minimumConnectingTime'.
     * 
     * @param minimumConnectingTime the value of field
     * 'minimumConnectingTime'.
     */
    public void setMinimumConnectingTime(
            final org.exolab.castor.types.Duration minimumConnectingTime) {
        this._minimumConnectingTime = minimumConnectingTime;
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
