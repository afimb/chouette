/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * Time and duration information
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class TimePeriodTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _dateTime.
     */
    private java.util.Date _dateTime;

    /**
     * Field _duration.
     */
    private org.exolab.castor.types.Duration _duration;


      //----------------/
     //- Constructors -/
    //----------------/

    public TimePeriodTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'dateTime'.
     * 
     * @return the value of field 'DateTime'.
     */
    public java.util.Date getDateTime(
    ) {
        return this._dateTime;
    }

    /**
     * Returns the value of field 'duration'.
     * 
     * @return the value of field 'Duration'.
     */
    public org.exolab.castor.types.Duration getDuration(
    ) {
        return this._duration;
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
     * Sets the value of field 'dateTime'.
     * 
     * @param dateTime the value of field 'dateTime'.
     */
    public void setDateTime(
            final java.util.Date dateTime) {
        this._dateTime = dateTime;
    }

    /**
     * Sets the value of field 'duration'.
     * 
     * @param duration the value of field 'duration'.
     */
    public void setDuration(
            final org.exolab.castor.types.Duration duration) {
        this._duration = duration;
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
