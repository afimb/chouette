/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package altibus.schema;

/**
 * Class Bus.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Bus extends java.lang.Object 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _ABusList.
     */
    private java.util.List<altibus.schema.ABus> _ABusList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Bus() {
        super();
        this._ABusList = new java.util.ArrayList<altibus.schema.ABus>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vABus
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addABus(
            final altibus.schema.ABus vABus)
    throws java.lang.IndexOutOfBoundsException {
        this._ABusList.add(vABus);
    }

    /**
     * 
     * 
     * @param index
     * @param vABus
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addABus(
            final int index,
            final altibus.schema.ABus vABus)
    throws java.lang.IndexOutOfBoundsException {
        this._ABusList.add(index, vABus);
    }

    /**
     * Method enumerateABus.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends altibus.schema.ABus> enumerateABus(
    ) {
        return java.util.Collections.enumeration(this._ABusList);
    }

    /**
     * Method getABus.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the altibus.schema.ABus at the given
     * index
     */
    public altibus.schema.ABus getABus(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ABusList.size()) {
            throw new IndexOutOfBoundsException("getABus: Index value '" + index + "' not in range [0.." + (this._ABusList.size() - 1) + "]");
        }

        return (altibus.schema.ABus) _ABusList.get(index);
    }

    /**
     * Method getABus.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public altibus.schema.ABus[] getABus(
    ) {
        altibus.schema.ABus[] array = new altibus.schema.ABus[0];
        return (altibus.schema.ABus[]) this._ABusList.toArray(array);
    }

    /**
     * Method getABusAsReference.Returns a reference to
     * '_ABusList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<altibus.schema.ABus> getABusAsReference(
    ) {
        return this._ABusList;
    }

    /**
     * Method getABusCount.
     * 
     * @return the size of this collection
     */
    public int getABusCount(
    ) {
        return this._ABusList.size();
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
     * Method iterateABus.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends altibus.schema.ABus> iterateABus(
    ) {
        return this._ABusList.iterator();
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
     * Method removeABus.
     * 
     * @param vABus
     * @return true if the object was removed from the collection.
     */
    public boolean removeABus(
            final altibus.schema.ABus vABus) {
        boolean removed = _ABusList.remove(vABus);
        return removed;
    }

    /**
     * Method removeABusAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public altibus.schema.ABus removeABusAt(
            final int index) {
        java.lang.Object obj = this._ABusList.remove(index);
        return (altibus.schema.ABus) obj;
    }

    /**
     */
    public void removeAllABus(
    ) {
        this._ABusList.clear();
    }

    /**
     * 
     * 
     * @param index
     * @param vABus
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setABus(
            final int index,
            final altibus.schema.ABus vABus)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ABusList.size()) {
            throw new IndexOutOfBoundsException("setABus: Index value '" + index + "' not in range [0.." + (this._ABusList.size() - 1) + "]");
        }

        this._ABusList.set(index, vABus);
    }

    /**
     * 
     * 
     * @param vABusArray
     */
    public void setABus(
            final altibus.schema.ABus[] vABusArray) {
        //-- copy array
        _ABusList.clear();

        for (int i = 0; i < vABusArray.length; i++) {
                this._ABusList.add(vABusArray[i]);
        }
    }

    /**
     * Sets the value of '_ABusList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vABusList the Vector to copy.
     */
    public void setABus(
            final java.util.List<altibus.schema.ABus> vABusList) {
        // copy vector
        this._ABusList.clear();

        this._ABusList.addAll(vABusList);
    }

    /**
     * Sets the value of '_ABusList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param ABusList the Vector to set.
     */
    public void setABusAsReference(
            final java.util.List<altibus.schema.ABus> ABusList) {
        this._ABusList = ABusList;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled altibus.schema.Bus
     */
    public static altibus.schema.Bus unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (altibus.schema.Bus) org.exolab.castor.xml.Unmarshaller.unmarshal(altibus.schema.Bus.class, reader);
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
