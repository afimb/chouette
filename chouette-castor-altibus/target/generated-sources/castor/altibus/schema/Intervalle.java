/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package altibus.schema;

/**
 * Class Intervalle.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Intervalle extends java.lang.Object 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _refIntervalle.
     */
    private java.lang.String _refIntervalle;

    /**
     * Field _refCalendrier.
     */
    private java.lang.String _refCalendrier;

    /**
     * Field _debut.
     */
    private org.exolab.castor.types.Date _debut;

    /**
     * Field _fin.
     */
    private org.exolab.castor.types.Date _fin;

    /**
     * Field _lundi.
     */
    private boolean _lundi;

    /**
     * keeps track of state for field: _lundi
     */
    private boolean _has_lundi;

    /**
     * Field _mardi.
     */
    private boolean _mardi;

    /**
     * keeps track of state for field: _mardi
     */
    private boolean _has_mardi;

    /**
     * Field _mercredi.
     */
    private boolean _mercredi;

    /**
     * keeps track of state for field: _mercredi
     */
    private boolean _has_mercredi;

    /**
     * Field _jeudi.
     */
    private boolean _jeudi;

    /**
     * keeps track of state for field: _jeudi
     */
    private boolean _has_jeudi;

    /**
     * Field _vendredi.
     */
    private boolean _vendredi;

    /**
     * keeps track of state for field: _vendredi
     */
    private boolean _has_vendredi;

    /**
     * Field _samedi.
     */
    private boolean _samedi;

    /**
     * keeps track of state for field: _samedi
     */
    private boolean _has_samedi;

    /**
     * Field _dimanche.
     */
    private boolean _dimanche;

    /**
     * keeps track of state for field: _dimanche
     */
    private boolean _has_dimanche;

    /**
     * Field _type.
     */
    private altibus.schema.types.EType _type;


      //----------------/
     //- Constructors -/
    //----------------/

    public Intervalle() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteDimanche(
    ) {
        this._has_dimanche= false;
    }

    /**
     */
    public void deleteJeudi(
    ) {
        this._has_jeudi= false;
    }

    /**
     */
    public void deleteLundi(
    ) {
        this._has_lundi= false;
    }

    /**
     */
    public void deleteMardi(
    ) {
        this._has_mardi= false;
    }

    /**
     */
    public void deleteMercredi(
    ) {
        this._has_mercredi= false;
    }

    /**
     */
    public void deleteSamedi(
    ) {
        this._has_samedi= false;
    }

    /**
     */
    public void deleteVendredi(
    ) {
        this._has_vendredi= false;
    }

    /**
     * Returns the value of field 'debut'.
     * 
     * @return the value of field 'Debut'.
     */
    public org.exolab.castor.types.Date getDebut(
    ) {
        return this._debut;
    }

    /**
     * Returns the value of field 'dimanche'.
     * 
     * @return the value of field 'Dimanche'.
     */
    public boolean getDimanche(
    ) {
        return this._dimanche;
    }

    /**
     * Returns the value of field 'fin'.
     * 
     * @return the value of field 'Fin'.
     */
    public org.exolab.castor.types.Date getFin(
    ) {
        return this._fin;
    }

    /**
     * Returns the value of field 'jeudi'.
     * 
     * @return the value of field 'Jeudi'.
     */
    public boolean getJeudi(
    ) {
        return this._jeudi;
    }

    /**
     * Returns the value of field 'lundi'.
     * 
     * @return the value of field 'Lundi'.
     */
    public boolean getLundi(
    ) {
        return this._lundi;
    }

    /**
     * Returns the value of field 'mardi'.
     * 
     * @return the value of field 'Mardi'.
     */
    public boolean getMardi(
    ) {
        return this._mardi;
    }

    /**
     * Returns the value of field 'mercredi'.
     * 
     * @return the value of field 'Mercredi'.
     */
    public boolean getMercredi(
    ) {
        return this._mercredi;
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
     * Returns the value of field 'refIntervalle'.
     * 
     * @return the value of field 'RefIntervalle'.
     */
    public java.lang.String getRefIntervalle(
    ) {
        return this._refIntervalle;
    }

    /**
     * Returns the value of field 'samedi'.
     * 
     * @return the value of field 'Samedi'.
     */
    public boolean getSamedi(
    ) {
        return this._samedi;
    }

    /**
     * Returns the value of field 'type'.
     * 
     * @return the value of field 'Type'.
     */
    public altibus.schema.types.EType getType(
    ) {
        return this._type;
    }

    /**
     * Returns the value of field 'vendredi'.
     * 
     * @return the value of field 'Vendredi'.
     */
    public boolean getVendredi(
    ) {
        return this._vendredi;
    }

    /**
     * Method hasDimanche.
     * 
     * @return true if at least one Dimanche has been added
     */
    public boolean hasDimanche(
    ) {
        return this._has_dimanche;
    }

    /**
     * Method hasJeudi.
     * 
     * @return true if at least one Jeudi has been added
     */
    public boolean hasJeudi(
    ) {
        return this._has_jeudi;
    }

    /**
     * Method hasLundi.
     * 
     * @return true if at least one Lundi has been added
     */
    public boolean hasLundi(
    ) {
        return this._has_lundi;
    }

    /**
     * Method hasMardi.
     * 
     * @return true if at least one Mardi has been added
     */
    public boolean hasMardi(
    ) {
        return this._has_mardi;
    }

    /**
     * Method hasMercredi.
     * 
     * @return true if at least one Mercredi has been added
     */
    public boolean hasMercredi(
    ) {
        return this._has_mercredi;
    }

    /**
     * Method hasSamedi.
     * 
     * @return true if at least one Samedi has been added
     */
    public boolean hasSamedi(
    ) {
        return this._has_samedi;
    }

    /**
     * Method hasVendredi.
     * 
     * @return true if at least one Vendredi has been added
     */
    public boolean hasVendredi(
    ) {
        return this._has_vendredi;
    }

    /**
     * Returns the value of field 'dimanche'.
     * 
     * @return the value of field 'Dimanche'.
     */
    public boolean isDimanche(
    ) {
        return this._dimanche;
    }

    /**
     * Returns the value of field 'jeudi'.
     * 
     * @return the value of field 'Jeudi'.
     */
    public boolean isJeudi(
    ) {
        return this._jeudi;
    }

    /**
     * Returns the value of field 'lundi'.
     * 
     * @return the value of field 'Lundi'.
     */
    public boolean isLundi(
    ) {
        return this._lundi;
    }

    /**
     * Returns the value of field 'mardi'.
     * 
     * @return the value of field 'Mardi'.
     */
    public boolean isMardi(
    ) {
        return this._mardi;
    }

    /**
     * Returns the value of field 'mercredi'.
     * 
     * @return the value of field 'Mercredi'.
     */
    public boolean isMercredi(
    ) {
        return this._mercredi;
    }

    /**
     * Returns the value of field 'samedi'.
     * 
     * @return the value of field 'Samedi'.
     */
    public boolean isSamedi(
    ) {
        return this._samedi;
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
     * Returns the value of field 'vendredi'.
     * 
     * @return the value of field 'Vendredi'.
     */
    public boolean isVendredi(
    ) {
        return this._vendredi;
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
     * Sets the value of field 'debut'.
     * 
     * @param debut the value of field 'debut'.
     */
    public void setDebut(
            final org.exolab.castor.types.Date debut) {
        this._debut = debut;
    }

    /**
     * Sets the value of field 'dimanche'.
     * 
     * @param dimanche the value of field 'dimanche'.
     */
    public void setDimanche(
            final boolean dimanche) {
        this._dimanche = dimanche;
        this._has_dimanche = true;
    }

    /**
     * Sets the value of field 'fin'.
     * 
     * @param fin the value of field 'fin'.
     */
    public void setFin(
            final org.exolab.castor.types.Date fin) {
        this._fin = fin;
    }

    /**
     * Sets the value of field 'jeudi'.
     * 
     * @param jeudi the value of field 'jeudi'.
     */
    public void setJeudi(
            final boolean jeudi) {
        this._jeudi = jeudi;
        this._has_jeudi = true;
    }

    /**
     * Sets the value of field 'lundi'.
     * 
     * @param lundi the value of field 'lundi'.
     */
    public void setLundi(
            final boolean lundi) {
        this._lundi = lundi;
        this._has_lundi = true;
    }

    /**
     * Sets the value of field 'mardi'.
     * 
     * @param mardi the value of field 'mardi'.
     */
    public void setMardi(
            final boolean mardi) {
        this._mardi = mardi;
        this._has_mardi = true;
    }

    /**
     * Sets the value of field 'mercredi'.
     * 
     * @param mercredi the value of field 'mercredi'.
     */
    public void setMercredi(
            final boolean mercredi) {
        this._mercredi = mercredi;
        this._has_mercredi = true;
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
     * Sets the value of field 'refIntervalle'.
     * 
     * @param refIntervalle the value of field 'refIntervalle'.
     */
    public void setRefIntervalle(
            final java.lang.String refIntervalle) {
        this._refIntervalle = refIntervalle;
    }

    /**
     * Sets the value of field 'samedi'.
     * 
     * @param samedi the value of field 'samedi'.
     */
    public void setSamedi(
            final boolean samedi) {
        this._samedi = samedi;
        this._has_samedi = true;
    }

    /**
     * Sets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(
            final altibus.schema.types.EType type) {
        this._type = type;
    }

    /**
     * Sets the value of field 'vendredi'.
     * 
     * @param vendredi the value of field 'vendredi'.
     */
    public void setVendredi(
            final boolean vendredi) {
        this._vendredi = vendredi;
        this._has_vendredi = true;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled altibus.schema.Intervalle
     */
    public static altibus.schema.Intervalle unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (altibus.schema.Intervalle) org.exolab.castor.xml.Unmarshaller.unmarshal(altibus.schema.Intervalle.class, reader);
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
