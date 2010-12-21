/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package altibus.schema;

/**
 * Class Arrets.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Arrets extends java.lang.Object 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _arretList.
     */
    private java.util.List<altibus.schema.Arret> _arretList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Arrets() {
        super();
        this._arretList = new java.util.ArrayList<altibus.schema.Arret>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vArret
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addArret(
            final altibus.schema.Arret vArret)
    throws java.lang.IndexOutOfBoundsException {
        this._arretList.add(vArret);
    }

    /**
     * 
     * 
     * @param index
     * @param vArret
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addArret(
            final int index,
            final altibus.schema.Arret vArret)
    throws java.lang.IndexOutOfBoundsException {
        this._arretList.add(index, vArret);
    }

    /**
     * Method enumerateArret.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends altibus.schema.Arret> enumerateArret(
    ) {
        return java.util.Collections.enumeration(this._arretList);
    }

    /**
     * Method getArret.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the altibus.schema.Arret at the given
     * index
     */
    public altibus.schema.Arret getArret(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._arretList.size()) {
            throw new IndexOutOfBoundsException("getArret: Index value '" + index + "' not in range [0.." + (this._arretList.size() - 1) + "]");
        }

        return (altibus.schema.Arret) _arretList.get(index);
    }

    /**
     * Method getArret.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public altibus.schema.Arret[] getArret(
    ) {
        altibus.schema.Arret[] array = new altibus.schema.Arret[0];
        return (altibus.schema.Arret[]) this._arretList.toArray(array);
    }

    /**
     * Method getArretAsReference.Returns a reference to
     * '_arretList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<altibus.schema.Arret> getArretAsReference(
    ) {
        return this._arretList;
    }

    /**
     * Method getArretCount.
     * 
     * @return the size of this collection
     */
    public int getArretCount(
    ) {
        return this._arretList.size();
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
     * Method iterateArret.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends altibus.schema.Arret> iterateArret(
    ) {
        return this._arretList.iterator();
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
     */
    public void removeAllArret(
    ) {
        this._arretList.clear();
    }

    /**
     * Method removeArret.
     * 
     * @param vArret
     * @return true if the object was removed from the collection.
     */
    public boolean removeArret(
            final altibus.schema.Arret vArret) {
        boolean removed = _arretList.remove(vArret);
        return removed;
    }

    /**
     * Method removeArretAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public altibus.schema.Arret removeArretAt(
            final int index) {
        java.lang.Object obj = this._arretList.remove(index);
        return (altibus.schema.Arret) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vArret
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setArret(
            final int index,
            final altibus.schema.Arret vArret)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._arretList.size()) {
            throw new IndexOutOfBoundsException("setArret: Index value '" + index + "' not in range [0.." + (this._arretList.size() - 1) + "]");
        }

        this._arretList.set(index, vArret);
    }

    /**
     * 
     * 
     * @param vArretArray
     */
    public void setArret(
            final altibus.schema.Arret[] vArretArray) {
        //-- copy array
        _arretList.clear();

        for (int i = 0; i < vArretArray.length; i++) {
                this._arretList.add(vArretArray[i]);
        }
    }

    /**
     * Sets the value of '_arretList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vArretList the Vector to copy.
     */
    public void setArret(
            final java.util.List<altibus.schema.Arret> vArretList) {
        // copy vector
        this._arretList.clear();

        this._arretList.addAll(vArretList);
    }

    /**
     * Sets the value of '_arretList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param arretList the Vector to set.
     */
    public void setArretAsReference(
            final java.util.List<altibus.schema.Arret> arretList) {
        this._arretList = arretList;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled altibus.schema.Arrets
     */
    public static altibus.schema.Arrets unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (altibus.schema.Arrets) org.exolab.castor.xml.Unmarshaller.unmarshal(altibus.schema.Arrets.class, reader);
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
