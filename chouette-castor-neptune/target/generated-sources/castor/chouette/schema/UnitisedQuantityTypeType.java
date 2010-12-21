/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Value with its type 
 * for road trafic_
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class UnitisedQuantityTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _value.
     */
    private java.math.BigDecimal _value;

    /**
     * Field _unit.
     */
    private chouette.schema.types.UnitType _unit;

    /**
     * Field _accuracy.
     */
    private chouette.schema.Accuracy _accuracy;

    /**
     * Field _measurementTime.
     */
    private chouette.schema.MeasurementTime _measurementTime;

    /**
     * Field _measurementLocation.
     */
    private chouette.schema.MeasurementLocation _measurementLocation;


      //----------------/
     //- Constructors -/
    //----------------/

    public UnitisedQuantityTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'accuracy'.
     * 
     * @return the value of field 'Accuracy'.
     */
    public chouette.schema.Accuracy getAccuracy(
    ) {
        return this._accuracy;
    }

    /**
     * Returns the value of field 'measurementLocation'.
     * 
     * @return the value of field 'MeasurementLocation'.
     */
    public chouette.schema.MeasurementLocation getMeasurementLocation(
    ) {
        return this._measurementLocation;
    }

    /**
     * Returns the value of field 'measurementTime'.
     * 
     * @return the value of field 'MeasurementTime'.
     */
    public chouette.schema.MeasurementTime getMeasurementTime(
    ) {
        return this._measurementTime;
    }

    /**
     * Returns the value of field 'unit'.
     * 
     * @return the value of field 'Unit'.
     */
    public chouette.schema.types.UnitType getUnit(
    ) {
        return this._unit;
    }

    /**
     * Returns the value of field 'value'.
     * 
     * @return the value of field 'Value'.
     */
    public java.math.BigDecimal getValue(
    ) {
        return this._value;
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
     * Sets the value of field 'accuracy'.
     * 
     * @param accuracy the value of field 'accuracy'.
     */
    public void setAccuracy(
            final chouette.schema.Accuracy accuracy) {
        this._accuracy = accuracy;
    }

    /**
     * Sets the value of field 'measurementLocation'.
     * 
     * @param measurementLocation the value of field
     * 'measurementLocation'.
     */
    public void setMeasurementLocation(
            final chouette.schema.MeasurementLocation measurementLocation) {
        this._measurementLocation = measurementLocation;
    }

    /**
     * Sets the value of field 'measurementTime'.
     * 
     * @param measurementTime the value of field 'measurementTime'.
     */
    public void setMeasurementTime(
            final chouette.schema.MeasurementTime measurementTime) {
        this._measurementTime = measurementTime;
    }

    /**
     * Sets the value of field 'unit'.
     * 
     * @param unit the value of field 'unit'.
     */
    public void setUnit(
            final chouette.schema.types.UnitType unit) {
        this._unit = unit;
    }

    /**
     * Sets the value of field 'value'.
     * 
     * @param value the value of field 'value'.
     */
    public void setValue(
            final java.math.BigDecimal value) {
        this._value = value;
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
