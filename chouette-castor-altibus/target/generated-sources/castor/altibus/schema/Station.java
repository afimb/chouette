/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package altibus.schema;

/**
 * Class Station.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Station extends java.lang.Object 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _refStation.
     */
    private java.lang.String _refStation;

    /**
     * Field _nomStation.
     */
    private java.lang.String _nomStation;

    /**
     * Field _numEnregistrementStation.
     */
    private java.lang.String _numEnregistrementStation;


      //----------------/
     //- Constructors -/
    //----------------/

    public Station() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'nomStation'.
     * 
     * @return the value of field 'NomStation'.
     */
    public java.lang.String getNomStation(
    ) {
        return this._nomStation;
    }

    /**
     * Returns the value of field 'numEnregistrementStation'.
     * 
     * @return the value of field 'NumEnregistrementStation'.
     */
    public java.lang.String getNumEnregistrementStation(
    ) {
        return this._numEnregistrementStation;
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
     * Sets the value of field 'nomStation'.
     * 
     * @param nomStation the value of field 'nomStation'.
     */
    public void setNomStation(
            final java.lang.String nomStation) {
        this._nomStation = nomStation;
    }

    /**
     * Sets the value of field 'numEnregistrementStation'.
     * 
     * @param numEnregistrementStation the value of field
     * 'numEnregistrementStation'.
     */
    public void setNumEnregistrementStation(
            final java.lang.String numEnregistrementStation) {
        this._numEnregistrementStation = numEnregistrementStation;
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
     * @return the unmarshaled altibus.schema.Station
     */
    public static altibus.schema.Station unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (altibus.schema.Station) org.exolab.castor.xml.Unmarshaller.unmarshal(altibus.schema.Station.class, reader);
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
