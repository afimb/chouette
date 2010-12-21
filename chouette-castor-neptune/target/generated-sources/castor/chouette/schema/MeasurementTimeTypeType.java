/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Informations on the time of a measurement
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class MeasurementTimeTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _measurementTime.
     */
    private java.util.Date _measurementTime;

    /**
     * Field _measurementPeriod.
     */
    private chouette.schema.MeasurementPeriod _measurementPeriod;


      //----------------/
     //- Constructors -/
    //----------------/

    public MeasurementTimeTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'measurementPeriod'.
     * 
     * @return the value of field 'MeasurementPeriod'.
     */
    public chouette.schema.MeasurementPeriod getMeasurementPeriod(
    ) {
        return this._measurementPeriod;
    }

    /**
     * Returns the value of field 'measurementTime'.
     * 
     * @return the value of field 'MeasurementTime'.
     */
    public java.util.Date getMeasurementTime(
    ) {
        return this._measurementTime;
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
     * Sets the value of field 'measurementPeriod'.
     * 
     * @param measurementPeriod the value of field
     * 'measurementPeriod'.
     */
    public void setMeasurementPeriod(
            final chouette.schema.MeasurementPeriod measurementPeriod) {
        this._measurementPeriod = measurementPeriod;
    }

    /**
     * Sets the value of field 'measurementTime'.
     * 
     * @param measurementTime the value of field 'measurementTime'.
     */
    public void setMeasurementTime(
            final java.util.Date measurementTime) {
        this._measurementTime = measurementTime;
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
