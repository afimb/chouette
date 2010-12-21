/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package altibus.schema;

/**
 * Class Arret.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Arret extends java.lang.Object 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _refArret.
     */
    private java.lang.String _refArret;

    /**
     * Field _numBus.
     */
    private java.lang.String _numBus;

    /**
     * Field _numProgressionBus.
     */
    private long _numProgressionBus;

    /**
     * keeps track of state for field: _numProgressionBus
     */
    private boolean _has_numProgressionBus;

    /**
     * Field _refStation.
     */
    private java.lang.String _refStation;

    /**
     * Field _horaire.
     */
    private org.exolab.castor.types.Time _horaire;


      //----------------/
     //- Constructors -/
    //----------------/

    public Arret() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteNumProgressionBus(
    ) {
        this._has_numProgressionBus= false;
    }

    /**
     * Returns the value of field 'horaire'.
     * 
     * @return the value of field 'Horaire'.
     */
    public org.exolab.castor.types.Time getHoraire(
    ) {
        return this._horaire;
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
     * Returns the value of field 'numProgressionBus'.
     * 
     * @return the value of field 'NumProgressionBus'.
     */
    public long getNumProgressionBus(
    ) {
        return this._numProgressionBus;
    }

    /**
     * Returns the value of field 'refArret'.
     * 
     * @return the value of field 'RefArret'.
     */
    public java.lang.String getRefArret(
    ) {
        return this._refArret;
    }

    /**
     * Returns the value of field 'refStation'.
     * 
     * @return the value of field 'RefStation'.
     */
    public java.lang.String getRefStation(
    ) {
        return this._refStation;
    }

    /**
     * Method hasNumProgressionBus.
     * 
     * @return true if at least one NumProgressionBus has been added
     */
    public boolean hasNumProgressionBus(
    ) {
        return this._has_numProgressionBus;
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
     * Sets the value of field 'horaire'.
     * 
     * @param horaire the value of field 'horaire'.
     */
    public void setHoraire(
            final org.exolab.castor.types.Time horaire) {
        this._horaire = horaire;
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
     * Sets the value of field 'numProgressionBus'.
     * 
     * @param numProgressionBus the value of field
     * 'numProgressionBus'.
     */
    public void setNumProgressionBus(
            final long numProgressionBus) {
        this._numProgressionBus = numProgressionBus;
        this._has_numProgressionBus = true;
    }

    /**
     * Sets the value of field 'refArret'.
     * 
     * @param refArret the value of field 'refArret'.
     */
    public void setRefArret(
            final java.lang.String refArret) {
        this._refArret = refArret;
    }

    /**
     * Sets the value of field 'refStation'.
     * 
     * @param refStation the value of field 'refStation'.
     */
    public void setRefStation(
            final java.lang.String refStation) {
        this._refStation = refStation;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled altibus.schema.Arret
     */
    public static altibus.schema.Arret unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (altibus.schema.Arret) org.exolab.castor.xml.Unmarshaller.unmarshal(altibus.schema.Arret.class, reader);
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
