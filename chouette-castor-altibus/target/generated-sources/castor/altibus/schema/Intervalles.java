/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package altibus.schema;

/**
 * Class Intervalles.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Intervalles extends java.lang.Object 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _intervalleList.
     */
    private java.util.List<altibus.schema.Intervalle> _intervalleList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Intervalles() {
        super();
        this._intervalleList = new java.util.ArrayList<altibus.schema.Intervalle>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vIntervalle
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addIntervalle(
            final altibus.schema.Intervalle vIntervalle)
    throws java.lang.IndexOutOfBoundsException {
        this._intervalleList.add(vIntervalle);
    }

    /**
     * 
     * 
     * @param index
     * @param vIntervalle
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addIntervalle(
            final int index,
            final altibus.schema.Intervalle vIntervalle)
    throws java.lang.IndexOutOfBoundsException {
        this._intervalleList.add(index, vIntervalle);
    }

    /**
     * Method enumerateIntervalle.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends altibus.schema.Intervalle> enumerateIntervalle(
    ) {
        return java.util.Collections.enumeration(this._intervalleList);
    }

    /**
     * Method getIntervalle.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the altibus.schema.Intervalle at the
     * given index
     */
    public altibus.schema.Intervalle getIntervalle(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._intervalleList.size()) {
            throw new IndexOutOfBoundsException("getIntervalle: Index value '" + index + "' not in range [0.." + (this._intervalleList.size() - 1) + "]");
        }

        return (altibus.schema.Intervalle) _intervalleList.get(index);
    }

    /**
     * Method getIntervalle.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public altibus.schema.Intervalle[] getIntervalle(
    ) {
        altibus.schema.Intervalle[] array = new altibus.schema.Intervalle[0];
        return (altibus.schema.Intervalle[]) this._intervalleList.toArray(array);
    }

    /**
     * Method getIntervalleAsReference.Returns a reference to
     * '_intervalleList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<altibus.schema.Intervalle> getIntervalleAsReference(
    ) {
        return this._intervalleList;
    }

    /**
     * Method getIntervalleCount.
     * 
     * @return the size of this collection
     */
    public int getIntervalleCount(
    ) {
        return this._intervalleList.size();
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
     * Method iterateIntervalle.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends altibus.schema.Intervalle> iterateIntervalle(
    ) {
        return this._intervalleList.iterator();
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
    public void removeAllIntervalle(
    ) {
        this._intervalleList.clear();
    }

    /**
     * Method removeIntervalle.
     * 
     * @param vIntervalle
     * @return true if the object was removed from the collection.
     */
    public boolean removeIntervalle(
            final altibus.schema.Intervalle vIntervalle) {
        boolean removed = _intervalleList.remove(vIntervalle);
        return removed;
    }

    /**
     * Method removeIntervalleAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public altibus.schema.Intervalle removeIntervalleAt(
            final int index) {
        java.lang.Object obj = this._intervalleList.remove(index);
        return (altibus.schema.Intervalle) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vIntervalle
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setIntervalle(
            final int index,
            final altibus.schema.Intervalle vIntervalle)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._intervalleList.size()) {
            throw new IndexOutOfBoundsException("setIntervalle: Index value '" + index + "' not in range [0.." + (this._intervalleList.size() - 1) + "]");
        }

        this._intervalleList.set(index, vIntervalle);
    }

    /**
     * 
     * 
     * @param vIntervalleArray
     */
    public void setIntervalle(
            final altibus.schema.Intervalle[] vIntervalleArray) {
        //-- copy array
        _intervalleList.clear();

        for (int i = 0; i < vIntervalleArray.length; i++) {
                this._intervalleList.add(vIntervalleArray[i]);
        }
    }

    /**
     * Sets the value of '_intervalleList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vIntervalleList the Vector to copy.
     */
    public void setIntervalle(
            final java.util.List<altibus.schema.Intervalle> vIntervalleList) {
        // copy vector
        this._intervalleList.clear();

        this._intervalleList.addAll(vIntervalleList);
    }

    /**
     * Sets the value of '_intervalleList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param intervalleList the Vector to set.
     */
    public void setIntervalleAsReference(
            final java.util.List<altibus.schema.Intervalle> intervalleList) {
        this._intervalleList = intervalleList;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled altibus.schema.Intervalles
     */
    public static altibus.schema.Intervalles unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (altibus.schema.Intervalles) org.exolab.castor.xml.Unmarshaller.unmarshal(altibus.schema.Intervalles.class, reader);
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
