/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * Class JourneyRunningTime.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class JourneyRunningTime extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _stopPointId.
     */
    private java.lang.String _stopPointId;

    /**
     * Field _departureToStopPointTime.
     */
    private org.exolab.castor.types.Duration _departureToStopPointTime;

    /**
     * Field _stopPointOrder.
     */
    private long _stopPointOrder;

    /**
     * keeps track of state for field: _stopPointOrder
     */
    private boolean _has_stopPointOrder;


      //----------------/
     //- Constructors -/
    //----------------/

    public JourneyRunningTime() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteStopPointOrder(
    ) {
        this._has_stopPointOrder= false;
    }

    /**
     * Returns the value of field 'departureToStopPointTime'.
     * 
     * @return the value of field 'DepartureToStopPointTime'.
     */
    public org.exolab.castor.types.Duration getDepartureToStopPointTime(
    ) {
        return this._departureToStopPointTime;
    }

    /**
     * Returns the value of field 'stopPointId'.
     * 
     * @return the value of field 'StopPointId'.
     */
    public java.lang.String getStopPointId(
    ) {
        return this._stopPointId;
    }

    /**
     * Returns the value of field 'stopPointOrder'.
     * 
     * @return the value of field 'StopPointOrder'.
     */
    public long getStopPointOrder(
    ) {
        return this._stopPointOrder;
    }

    /**
     * Method hasStopPointOrder.
     * 
     * @return true if at least one StopPointOrder has been added
     */
    public boolean hasStopPointOrder(
    ) {
        return this._has_stopPointOrder;
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
     * Sets the value of field 'departureToStopPointTime'.
     * 
     * @param departureToStopPointTime the value of field
     * 'departureToStopPointTime'.
     */
    public void setDepartureToStopPointTime(
            final org.exolab.castor.types.Duration departureToStopPointTime) {
        this._departureToStopPointTime = departureToStopPointTime;
    }

    /**
     * Sets the value of field 'stopPointId'.
     * 
     * @param stopPointId the value of field 'stopPointId'.
     */
    public void setStopPointId(
            final java.lang.String stopPointId) {
        this._stopPointId = stopPointId;
    }

    /**
     * Sets the value of field 'stopPointOrder'.
     * 
     * @param stopPointOrder the value of field 'stopPointOrder'.
     */
    public void setStopPointOrder(
            final long stopPointOrder) {
        this._stopPointOrder = stopPointOrder;
        this._has_stopPointOrder = true;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled amivif.schema.JourneyRunningTime
     */
    public static amivif.schema.JourneyRunningTime unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (amivif.schema.JourneyRunningTime) org.exolab.castor.xml.Unmarshaller.unmarshal(amivif.schema.JourneyRunningTime.class, reader);
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
