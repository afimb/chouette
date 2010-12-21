/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Class VehicleJourneyAtStopTypeChoice.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class VehicleJourneyAtStopTypeChoice extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _vehicleJourneyAtStopTypeChoiceSequence.
     */
    private chouette.schema.VehicleJourneyAtStopTypeChoiceSequence _vehicleJourneyAtStopTypeChoiceSequence;

    /**
     * Field _vehicleJourneyAtStopTypeChoiceSequence2.
     */
    private chouette.schema.VehicleJourneyAtStopTypeChoiceSequence2 _vehicleJourneyAtStopTypeChoiceSequence2;


      //----------------/
     //- Constructors -/
    //----------------/

    public VehicleJourneyAtStopTypeChoice() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field
     * 'vehicleJourneyAtStopTypeChoiceSequence'.
     * 
     * @return the value of field
     * 'VehicleJourneyAtStopTypeChoiceSequence'.
     */
    public chouette.schema.VehicleJourneyAtStopTypeChoiceSequence getVehicleJourneyAtStopTypeChoiceSequence(
    ) {
        return this._vehicleJourneyAtStopTypeChoiceSequence;
    }

    /**
     * Returns the value of field
     * 'vehicleJourneyAtStopTypeChoiceSequence2'.
     * 
     * @return the value of field
     * 'VehicleJourneyAtStopTypeChoiceSequence2'.
     */
    public chouette.schema.VehicleJourneyAtStopTypeChoiceSequence2 getVehicleJourneyAtStopTypeChoiceSequence2(
    ) {
        return this._vehicleJourneyAtStopTypeChoiceSequence2;
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
     * Sets the value of field
     * 'vehicleJourneyAtStopTypeChoiceSequence'.
     * 
     * @param vehicleJourneyAtStopTypeChoiceSequence the value of
     * field 'vehicleJourneyAtStopTypeChoiceSequence'.
     */
    public void setVehicleJourneyAtStopTypeChoiceSequence(
            final chouette.schema.VehicleJourneyAtStopTypeChoiceSequence vehicleJourneyAtStopTypeChoiceSequence) {
        this._vehicleJourneyAtStopTypeChoiceSequence = vehicleJourneyAtStopTypeChoiceSequence;
    }

    /**
     * Sets the value of field
     * 'vehicleJourneyAtStopTypeChoiceSequence2'.
     * 
     * @param vehicleJourneyAtStopTypeChoiceSequence2 the value of
     * field 'vehicleJourneyAtStopTypeChoiceSequence2'.
     */
    public void setVehicleJourneyAtStopTypeChoiceSequence2(
            final chouette.schema.VehicleJourneyAtStopTypeChoiceSequence2 vehicleJourneyAtStopTypeChoiceSequence2) {
        this._vehicleJourneyAtStopTypeChoiceSequence2 = vehicleJourneyAtStopTypeChoiceSequence2;
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
     * chouette.schema.VehicleJourneyAtStopTypeChoice
     */
    public static chouette.schema.VehicleJourneyAtStopTypeChoice unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (chouette.schema.VehicleJourneyAtStopTypeChoice) org.exolab.castor.xml.Unmarshaller.unmarshal(chouette.schema.VehicleJourneyAtStopTypeChoice.class, reader);
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
