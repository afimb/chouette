/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Class UserNeedGroup.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class UserNeedGroup extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Internal choice value storage
     */
    private java.lang.Object _choiceValue;

    /**
     * Passenger mobility need for which suitability is specified.
     */
    private chouette.schema.types.MobilityEnumeration _mobilityNeed;

    /**
     * Passenger mobility need for which suitability is specified.
     */
    private chouette.schema.types.PyschosensoryNeedEnumeration _psychosensoryNeed;

    /**
     * Passenger medical need for which suitability is specified.
     */
    private java.lang.String _medicalNeed;

    /**
     * Passenger enceumbrance need for which suitability is
     * specified.
     */
    private chouette.schema.types.EncumbranceEnumeration _encumbranceNeed;


      //----------------/
     //- Constructors -/
    //----------------/

    public UserNeedGroup() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'choiceValue'. The field
     * 'choiceValue' has the following description: Internal choice
     * value storage
     * 
     * @return the value of field 'ChoiceValue'.
     */
    public java.lang.Object getChoiceValue(
    ) {
        return this._choiceValue;
    }

    /**
     * Returns the value of field 'encumbranceNeed'. The field
     * 'encumbranceNeed' has the following description: Passenger
     * enceumbrance need for which suitability is specified.
     * 
     * @return the value of field 'EncumbranceNeed'.
     */
    public chouette.schema.types.EncumbranceEnumeration getEncumbranceNeed(
    ) {
        return this._encumbranceNeed;
    }

    /**
     * Returns the value of field 'medicalNeed'. The field
     * 'medicalNeed' has the following description: Passenger
     * medical need for which suitability is specified.
     * 
     * @return the value of field 'MedicalNeed'.
     */
    public java.lang.String getMedicalNeed(
    ) {
        return this._medicalNeed;
    }

    /**
     * Returns the value of field 'mobilityNeed'. The field
     * 'mobilityNeed' has the following description: Passenger
     * mobility need for which suitability is specified.
     * 
     * @return the value of field 'MobilityNeed'.
     */
    public chouette.schema.types.MobilityEnumeration getMobilityNeed(
    ) {
        return this._mobilityNeed;
    }

    /**
     * Returns the value of field 'psychosensoryNeed'. The field
     * 'psychosensoryNeed' has the following description: Passenger
     * mobility need for which suitability is specified.
     * 
     * @return the value of field 'PsychosensoryNeed'.
     */
    public chouette.schema.types.PyschosensoryNeedEnumeration getPsychosensoryNeed(
    ) {
        return this._psychosensoryNeed;
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
     * 
     * 
     * @param out
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     */
    public void marshal(
            final java.io.Writer out)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, out);
    }

    /**
     * 
     * 
     * @param handler
     * @throws java.io.IOException if an IOException occurs during
     * marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     */
    public void marshal(
            final org.xml.sax.ContentHandler handler)
    throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        org.exolab.castor.xml.Marshaller.marshal(this, handler);
    }

    /**
     * Sets the value of field 'encumbranceNeed'. The field
     * 'encumbranceNeed' has the following description: Passenger
     * enceumbrance need for which suitability is specified.
     * 
     * @param encumbranceNeed the value of field 'encumbranceNeed'.
     */
    public void setEncumbranceNeed(
            final chouette.schema.types.EncumbranceEnumeration encumbranceNeed) {
        this._encumbranceNeed = encumbranceNeed;
        this._choiceValue = encumbranceNeed;
    }

    /**
     * Sets the value of field 'medicalNeed'. The field
     * 'medicalNeed' has the following description: Passenger
     * medical need for which suitability is specified.
     * 
     * @param medicalNeed the value of field 'medicalNeed'.
     */
    public void setMedicalNeed(
            final java.lang.String medicalNeed) {
        this._medicalNeed = medicalNeed;
        this._choiceValue = medicalNeed;
    }

    /**
     * Sets the value of field 'mobilityNeed'. The field
     * 'mobilityNeed' has the following description: Passenger
     * mobility need for which suitability is specified.
     * 
     * @param mobilityNeed the value of field 'mobilityNeed'.
     */
    public void setMobilityNeed(
            final chouette.schema.types.MobilityEnumeration mobilityNeed) {
        this._mobilityNeed = mobilityNeed;
        this._choiceValue = mobilityNeed;
    }

    /**
     * Sets the value of field 'psychosensoryNeed'. The field
     * 'psychosensoryNeed' has the following description: Passenger
     * mobility need for which suitability is specified.
     * 
     * @param psychosensoryNeed the value of field
     * 'psychosensoryNeed'.
     */
    public void setPsychosensoryNeed(
            final chouette.schema.types.PyschosensoryNeedEnumeration psychosensoryNeed) {
        this._psychosensoryNeed = psychosensoryNeed;
        this._choiceValue = psychosensoryNeed;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled chouette.schema.UserNeedGroup
     */
    public static chouette.schema.UserNeedGroup unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (chouette.schema.UserNeedGroup) org.exolab.castor.xml.Unmarshaller.unmarshal(chouette.schema.UserNeedGroup.class, reader);
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
