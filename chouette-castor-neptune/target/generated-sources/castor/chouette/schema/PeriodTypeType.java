/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Period during which a Vehicle Journey is applicable
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class PeriodTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _startOfPeriod.
     */
    private org.exolab.castor.types.Date _startOfPeriod;

    /**
     * Field _endOfPeriod.
     */
    private org.exolab.castor.types.Date _endOfPeriod;


      //----------------/
     //- Constructors -/
    //----------------/

    public PeriodTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'endOfPeriod'.
     * 
     * @return the value of field 'EndOfPeriod'.
     */
    public org.exolab.castor.types.Date getEndOfPeriod(
    ) {
        return this._endOfPeriod;
    }

    /**
     * Returns the value of field 'startOfPeriod'.
     * 
     * @return the value of field 'StartOfPeriod'.
     */
    public org.exolab.castor.types.Date getStartOfPeriod(
    ) {
        return this._startOfPeriod;
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
     * Sets the value of field 'endOfPeriod'.
     * 
     * @param endOfPeriod the value of field 'endOfPeriod'.
     */
    public void setEndOfPeriod(
            final org.exolab.castor.types.Date endOfPeriod) {
        this._endOfPeriod = endOfPeriod;
    }

    /**
     * Sets the value of field 'startOfPeriod'.
     * 
     * @param startOfPeriod the value of field 'startOfPeriod'.
     */
    public void setStartOfPeriod(
            final org.exolab.castor.types.Date startOfPeriod) {
        this._startOfPeriod = startOfPeriod;
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
