/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * Extensions AMIVIF sur les correspondances
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class AMIVIF_ConnectionLink_Extension extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _display.
     */
    private boolean _display;

    /**
     * keeps track of state for field: _display
     */
    private boolean _has_display;


      //----------------/
     //- Constructors -/
    //----------------/

    public AMIVIF_ConnectionLink_Extension() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteDisplay(
    ) {
        this._has_display= false;
    }

    /**
     * Returns the value of field 'display'.
     * 
     * @return the value of field 'Display'.
     */
    public boolean getDisplay(
    ) {
        return this._display;
    }

    /**
     * Method hasDisplay.
     * 
     * @return true if at least one Display has been added
     */
    public boolean hasDisplay(
    ) {
        return this._has_display;
    }

    /**
     * Returns the value of field 'display'.
     * 
     * @return the value of field 'Display'.
     */
    public boolean isDisplay(
    ) {
        return this._display;
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
     * Sets the value of field 'display'.
     * 
     * @param display the value of field 'display'.
     */
    public void setDisplay(
            final boolean display) {
        this._display = display;
        this._has_display = true;
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
     * amivif.schema.AMIVIF_ConnectionLink_Extension
     */
    public static amivif.schema.AMIVIF_ConnectionLink_Extension unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (amivif.schema.AMIVIF_ConnectionLink_Extension) org.exolab.castor.xml.Unmarshaller.unmarshal(amivif.schema.AMIVIF_ConnectionLink_Extension.class, reader);
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
