/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package altibus.schema;

/**
 * Class ABus.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ABus extends java.lang.Object 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _numBus.
     */
    private java.lang.String _numBus;

    /**
     * Field _refLigne.
     */
    private java.lang.String _refLigne;

    /**
     * Field _refCalendrier.
     */
    private java.lang.String _refCalendrier;

    /**
     * Field _infos.
     */
    private java.lang.String _infos;


      //----------------/
     //- Constructors -/
    //----------------/

    public ABus() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'infos'.
     * 
     * @return the value of field 'Infos'.
     */
    public java.lang.String getInfos(
    ) {
        return this._infos;
    }

    /**
     * Returns the value of field 'numBus'.
     * 
     * @return the value of field 'NumBus'.
     */
    public java.lang.String getNumBus(
    ) {
        return this._numBus;
    }

    /**
     * Returns the value of field 'refCalendrier'.
     * 
     * @return the value of field 'RefCalendrier'.
     */
    public java.lang.String getRefCalendrier(
    ) {
        return this._refCalendrier;
    }

    /**
     * Returns the value of field 'refLigne'.
     * 
     * @return the value of field 'RefLigne'.
     */
    public java.lang.String getRefLigne(
    ) {
        return this._refLigne;
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
     * Sets the value of field 'infos'.
     * 
     * @param infos the value of field 'infos'.
     */
    public void setInfos(
            final java.lang.String infos) {
        this._infos = infos;
    }

    /**
     * Sets the value of field 'numBus'.
     * 
     * @param numBus the value of field 'numBus'.
     */
    public void setNumBus(
            final java.lang.String numBus) {
        this._numBus = numBus;
    }

    /**
     * Sets the value of field 'refCalendrier'.
     * 
     * @param refCalendrier the value of field 'refCalendrier'.
     */
    public void setRefCalendrier(
            final java.lang.String refCalendrier) {
        this._refCalendrier = refCalendrier;
    }

    /**
     * Sets the value of field 'refLigne'.
     * 
     * @param refLigne the value of field 'refLigne'.
     */
    public void setRefLigne(
            final java.lang.String refLigne) {
        this._refLigne = refLigne;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled altibus.schema.ABus
     */
    public static altibus.schema.ABus unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (altibus.schema.ABus) org.exolab.castor.xml.Unmarshaller.unmarshal(altibus.schema.ABus.class, reader);
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
