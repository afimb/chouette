/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * The facility has to be attached to a StopAres (Quay,
 * BoardingPosition or Stop Place), a line (meaning vehicles
 * operating this line), a connection link or a Stop Point on Route
 * (stopPoint))
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ChouetteFacilityTypeChoice extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _stopAreaId.
     */
    private java.lang.String _stopAreaId;

    /**
     * Field _lineId.
     */
    private java.lang.String _lineId;

    /**
     * Field _connectionLinkId.
     */
    private java.lang.String _connectionLinkId;

    /**
     * Field _stopPointId.
     */
    private java.lang.String _stopPointId;


      //----------------/
     //- Constructors -/
    //----------------/

    public ChouetteFacilityTypeChoice() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'connectionLinkId'.
     * 
     * @return the value of field 'ConnectionLinkId'.
     */
    public java.lang.String getConnectionLinkId(
    ) {
        return this._connectionLinkId;
    }

    /**
     * Returns the value of field 'lineId'.
     * 
     * @return the value of field 'LineId'.
     */
    public java.lang.String getLineId(
    ) {
        return this._lineId;
    }

    /**
     * Returns the value of field 'stopAreaId'.
     * 
     * @return the value of field 'StopAreaId'.
     */
    public java.lang.String getStopAreaId(
    ) {
        return this._stopAreaId;
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
     * Sets the value of field 'connectionLinkId'.
     * 
     * @param connectionLinkId the value of field 'connectionLinkId'
     */
    public void setConnectionLinkId(
            final java.lang.String connectionLinkId) {
        this._connectionLinkId = connectionLinkId;
    }

    /**
     * Sets the value of field 'lineId'.
     * 
     * @param lineId the value of field 'lineId'.
     */
    public void setLineId(
            final java.lang.String lineId) {
        this._lineId = lineId;
    }

    /**
     * Sets the value of field 'stopAreaId'.
     * 
     * @param stopAreaId the value of field 'stopAreaId'.
     */
    public void setStopAreaId(
            final java.lang.String stopAreaId) {
        this._stopAreaId = stopAreaId;
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
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled
     * chouette.schema.ChouetteFacilityTypeChoice
     */
    public static chouette.schema.ChouetteFacilityTypeChoice unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (chouette.schema.ChouetteFacilityTypeChoice) org.exolab.castor.xml.Unmarshaller.unmarshal(chouette.schema.ChouetteFacilityTypeChoice.class, reader);
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
