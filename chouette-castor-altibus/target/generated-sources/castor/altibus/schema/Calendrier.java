/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package altibus.schema;

/**
 * Class Calendrier.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Calendrier extends java.lang.Object 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _refCalendrier.
     */
    private java.lang.String _refCalendrier;

    /**
     * Field _nomCalendrier.
     */
    private java.lang.String _nomCalendrier;


      //----------------/
     //- Constructors -/
    //----------------/

    public Calendrier() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'nomCalendrier'.
     * 
     * @return the value of field 'NomCalendrier'.
     */
    public java.lang.String getNomCalendrier(
    ) {
        return this._nomCalendrier;
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
     * Sets the value of field 'nomCalendrier'.
     * 
     * @param nomCalendrier the value of field 'nomCalendrier'.
     */
    public void setNomCalendrier(
            final java.lang.String nomCalendrier) {
        this._nomCalendrier = nomCalendrier;
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
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled altibus.schema.Calendrier
     */
    public static altibus.schema.Calendrier unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (altibus.schema.Calendrier) org.exolab.castor.xml.Unmarshaller.unmarshal(altibus.schema.Calendrier.class, reader);
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
