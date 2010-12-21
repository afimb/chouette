/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Accuracy of a measure
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class AccuracyTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _standardDeviation.
     */
    private java.math.BigDecimal _standardDeviation;

    /**
     * Field _accuracy.
     */
    private java.math.BigDecimal _accuracy;

    /**
     * Field _dataClass.
     */
    private java.lang.String _dataClass;

    /**
     * Field _accuracyRange.
     */
    private java.lang.String _accuracyRange;


      //----------------/
     //- Constructors -/
    //----------------/

    public AccuracyTypeType() {
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
    public java.math.BigDecimal getAccuracy(
    ) {
        return this._accuracy;
    }

    /**
     * Returns the value of field 'accuracyRange'.
     * 
     * @return the value of field 'AccuracyRange'.
     */
    public java.lang.String getAccuracyRange(
    ) {
        return this._accuracyRange;
    }

    /**
     * Returns the value of field 'dataClass'.
     * 
     * @return the value of field 'DataClass'.
     */
    public java.lang.String getDataClass(
    ) {
        return this._dataClass;
    }

    /**
     * Returns the value of field 'standardDeviation'.
     * 
     * @return the value of field 'StandardDeviation'.
     */
    public java.math.BigDecimal getStandardDeviation(
    ) {
        return this._standardDeviation;
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
            final java.math.BigDecimal accuracy) {
        this._accuracy = accuracy;
    }

    /**
     * Sets the value of field 'accuracyRange'.
     * 
     * @param accuracyRange the value of field 'accuracyRange'.
     */
    public void setAccuracyRange(
            final java.lang.String accuracyRange) {
        this._accuracyRange = accuracyRange;
    }

    /**
     * Sets the value of field 'dataClass'.
     * 
     * @param dataClass the value of field 'dataClass'.
     */
    public void setDataClass(
            final java.lang.String dataClass) {
        this._dataClass = dataClass;
    }

    /**
     * Sets the value of field 'standardDeviation'.
     * 
     * @param standardDeviation the value of field
     * 'standardDeviation'.
     */
    public void setStandardDeviation(
            final java.math.BigDecimal standardDeviation) {
        this._standardDeviation = standardDeviation;
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
