/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * La liste de toutes les correspondances sur la ligne.
 * Les correspondances relient un arr�t de la ligne avec un autre
 * arr�t de cette m�me ligne ou d'une autre ligne.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ConnectionLink extends chouette.schema.ConnectionLinkTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _connectionLinkExtension.
     */
    private chouette.schema.ConnectionLinkExtension _connectionLinkExtension;


      //----------------/
     //- Constructors -/
    //----------------/

    public ConnectionLink() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'connectionLinkExtension'.
     * 
     * @return the value of field 'ConnectionLinkExtension'.
     */
    public chouette.schema.ConnectionLinkExtension getConnectionLinkExtension(
    ) {
        return this._connectionLinkExtension;
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
     * Sets the value of field 'connectionLinkExtension'.
     * 
     * @param connectionLinkExtension the value of field
     * 'connectionLinkExtension'.
     */
    public void setConnectionLinkExtension(
            final chouette.schema.ConnectionLinkExtension connectionLinkExtension) {
        this._connectionLinkExtension = connectionLinkExtension;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled chouette.schema.ConnectionLink
     */
    public static chouette.schema.ConnectionLink unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (chouette.schema.ConnectionLink) org.exolab.castor.xml.Unmarshaller.unmarshal(chouette.schema.ConnectionLink.class, reader);
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
