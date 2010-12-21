/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Class VehicleJourneyAtStopTypeChoiceSequence2.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class VehicleJourneyAtStopTypeChoiceSequence2 extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _elapseDuration.
     */
    private org.exolab.castor.types.Duration _elapseDuration;


      //----------------/
     //- Constructors -/
    //----------------/

    public VehicleJourneyAtStopTypeChoiceSequence2() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'elapseDuration'.
     * 
     * @return the value of field 'ElapseDuration'.
     */
    public org.exolab.castor.types.Duration getElapseDuration(
    ) {
        return this._elapseDuration;
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
     * Sets the value of field 'elapseDuration'.
     * 
     * @param elapseDuration the value of field 'elapseDuration'.
     */
    public void setElapseDuration(
            final org.exolab.castor.types.Duration elapseDuration) {
        this._elapseDuration = elapseDuration;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled
     * chouette.schema.VehicleJourneyAtStopTypeChoiceSequence2
     */
    public static chouette.schema.VehicleJourneyAtStopTypeChoiceSequence2 unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (chouette.schema.VehicleJourneyAtStopTypeChoiceSequence2) org.exolab.castor.xml.Unmarshaller.unmarshal(chouette.schema.VehicleJourneyAtStopTypeChoiceSequence2.class, reader);
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
