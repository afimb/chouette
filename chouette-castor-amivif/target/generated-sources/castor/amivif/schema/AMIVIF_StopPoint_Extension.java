/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * Extensions AMIVIF sur les points d'arret
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class AMIVIF_StopPoint_Extension extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _codeUIC.
     */
    private java.lang.String _codeUIC;

    /**
     * Field _upFarZone.
     */
    private long _upFarZone;

    /**
     * keeps track of state for field: _upFarZone
     */
    private boolean _has_upFarZone;

    /**
     * Field _downFarZone.
     */
    private long _downFarZone;

    /**
     * keeps track of state for field: _downFarZone
     */
    private boolean _has_downFarZone;


      //----------------/
     //- Constructors -/
    //----------------/

    public AMIVIF_StopPoint_Extension() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteDownFarZone(
    ) {
        this._has_downFarZone= false;
    }

    /**
     */
    public void deleteUpFarZone(
    ) {
        this._has_upFarZone= false;
    }

    /**
     * Returns the value of field 'codeUIC'.
     * 
     * @return the value of field 'CodeUIC'.
     */
    public java.lang.String getCodeUIC(
    ) {
        return this._codeUIC;
    }

    /**
     * Returns the value of field 'downFarZone'.
     * 
     * @return the value of field 'DownFarZone'.
     */
    public long getDownFarZone(
    ) {
        return this._downFarZone;
    }

    /**
     * Returns the value of field 'upFarZone'.
     * 
     * @return the value of field 'UpFarZone'.
     */
    public long getUpFarZone(
    ) {
        return this._upFarZone;
    }

    /**
     * Method hasDownFarZone.
     * 
     * @return true if at least one DownFarZone has been added
     */
    public boolean hasDownFarZone(
    ) {
        return this._has_downFarZone;
    }

    /**
     * Method hasUpFarZone.
     * 
     * @return true if at least one UpFarZone has been added
     */
    public boolean hasUpFarZone(
    ) {
        return this._has_upFarZone;
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
     * Sets the value of field 'codeUIC'.
     * 
     * @param codeUIC the value of field 'codeUIC'.
     */
    public void setCodeUIC(
            final java.lang.String codeUIC) {
        this._codeUIC = codeUIC;
    }

    /**
     * Sets the value of field 'downFarZone'.
     * 
     * @param downFarZone the value of field 'downFarZone'.
     */
    public void setDownFarZone(
            final long downFarZone) {
        this._downFarZone = downFarZone;
        this._has_downFarZone = true;
    }

    /**
     * Sets the value of field 'upFarZone'.
     * 
     * @param upFarZone the value of field 'upFarZone'.
     */
    public void setUpFarZone(
            final long upFarZone) {
        this._upFarZone = upFarZone;
        this._has_upFarZone = true;
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
     * amivif.schema.AMIVIF_StopPoint_Extension
     */
    public static amivif.schema.AMIVIF_StopPoint_Extension unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (amivif.schema.AMIVIF_StopPoint_Extension) org.exolab.castor.xml.Unmarshaller.unmarshal(amivif.schema.AMIVIF_StopPoint_Extension.class, reader);
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
