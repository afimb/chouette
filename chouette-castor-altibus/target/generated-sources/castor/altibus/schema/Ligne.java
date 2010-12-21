/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package altibus.schema;

/**
 * Class Ligne.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Ligne extends java.lang.Object 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _refLigne.
     */
    private java.lang.String _refLigne;

    /**
     * Field _refGareDepart.
     */
    private java.lang.String _refGareDepart;

    /**
     * Field _refGareArrivee.
     */
    private java.lang.String _refGareArrivee;

    /**
     * Field _nomLigne.
     */
    private java.lang.String _nomLigne;


      //----------------/
     //- Constructors -/
    //----------------/

    public Ligne() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'nomLigne'.
     * 
     * @return the value of field 'NomLigne'.
     */
    public java.lang.String getNomLigne(
    ) {
        return this._nomLigne;
    }

    /**
     * Returns the value of field 'refGareArrivee'.
     * 
     * @return the value of field 'RefGareArrivee'.
     */
    public java.lang.String getRefGareArrivee(
    ) {
        return this._refGareArrivee;
    }

    /**
     * Returns the value of field 'refGareDepart'.
     * 
     * @return the value of field 'RefGareDepart'.
     */
    public java.lang.String getRefGareDepart(
    ) {
        return this._refGareDepart;
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
     * Sets the value of field 'nomLigne'.
     * 
     * @param nomLigne the value of field 'nomLigne'.
     */
    public void setNomLigne(
            final java.lang.String nomLigne) {
        this._nomLigne = nomLigne;
    }

    /**
     * Sets the value of field 'refGareArrivee'.
     * 
     * @param refGareArrivee the value of field 'refGareArrivee'.
     */
    public void setRefGareArrivee(
            final java.lang.String refGareArrivee) {
        this._refGareArrivee = refGareArrivee;
    }

    /**
     * Sets the value of field 'refGareDepart'.
     * 
     * @param refGareDepart the value of field 'refGareDepart'.
     */
    public void setRefGareDepart(
            final java.lang.String refGareDepart) {
        this._refGareDepart = refGareDepart;
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
     * @return the unmarshaled altibus.schema.Ligne
     */
    public static altibus.schema.Ligne unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (altibus.schema.Ligne) org.exolab.castor.xml.Unmarshaller.unmarshal(altibus.schema.Ligne.class, reader);
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
