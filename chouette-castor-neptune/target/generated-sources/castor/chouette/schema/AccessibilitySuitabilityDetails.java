/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Class AccessibilitySuitabilityDetails.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class AccessibilitySuitabilityDetails extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _items.
     */
    private java.util.List<chouette.schema.AccessibilitySuitabilityDetailsItem> _items;


      //----------------/
     //- Constructors -/
    //----------------/

    public AccessibilitySuitabilityDetails() {
        super();
        this._items = new java.util.ArrayList<chouette.schema.AccessibilitySuitabilityDetailsItem>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vAccessibilitySuitabilityDetailsItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAccessibilitySuitabilityDetailsItem(
            final chouette.schema.AccessibilitySuitabilityDetailsItem vAccessibilitySuitabilityDetailsItem)
    throws java.lang.IndexOutOfBoundsException {
        this._items.add(vAccessibilitySuitabilityDetailsItem);
    }

    /**
     * 
     * 
     * @param index
     * @param vAccessibilitySuitabilityDetailsItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAccessibilitySuitabilityDetailsItem(
            final int index,
            final chouette.schema.AccessibilitySuitabilityDetailsItem vAccessibilitySuitabilityDetailsItem)
    throws java.lang.IndexOutOfBoundsException {
        this._items.add(index, vAccessibilitySuitabilityDetailsItem);
    }

    /**
     * Method enumerateAccessibilitySuitabilityDetailsItem.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.AccessibilitySuitabilityDetailsItem> enumerateAccessibilitySuitabilityDetailsItem(
    ) {
        return java.util.Collections.enumeration(this._items);
    }

    /**
     * Method getAccessibilitySuitabilityDetailsItem.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.AccessibilitySuitabilityDetailsItem at the
     * given index
     */
    public chouette.schema.AccessibilitySuitabilityDetailsItem getAccessibilitySuitabilityDetailsItem(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._items.size()) {
            throw new IndexOutOfBoundsException("getAccessibilitySuitabilityDetailsItem: Index value '" + index + "' not in range [0.." + (this._items.size() - 1) + "]");
        }

        return (chouette.schema.AccessibilitySuitabilityDetailsItem) _items.get(index);
    }

    /**
     * Method getAccessibilitySuitabilityDetailsItem.Returns the
     * contents of the collection in an Array.  <p>Note:  Just in
     * case the collection contents are changing in another thread,
     * we pass a 0-length Array of the correct type into the API
     * call.  This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.AccessibilitySuitabilityDetailsItem[] getAccessibilitySuitabilityDetailsItem(
    ) {
        chouette.schema.AccessibilitySuitabilityDetailsItem[] array = new chouette.schema.AccessibilitySuitabilityDetailsItem[0];
        return (chouette.schema.AccessibilitySuitabilityDetailsItem[]) this._items.toArray(array);
    }

    /**
     * Method
     * getAccessibilitySuitabilityDetailsItemAsReference.Returns a
     * reference to '_items'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.AccessibilitySuitabilityDetailsItem> getAccessibilitySuitabilityDetailsItemAsReference(
    ) {
        return this._items;
    }

    /**
     * Method getAccessibilitySuitabilityDetailsItemCount.
     * 
     * @return the size of this collection
     */
    public int getAccessibilitySuitabilityDetailsItemCount(
    ) {
        return this._items.size();
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
     * Method iterateAccessibilitySuitabilityDetailsItem.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.AccessibilitySuitabilityDetailsItem> iterateAccessibilitySuitabilityDetailsItem(
    ) {
        return this._items.iterator();
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
     * Method removeAccessibilitySuitabilityDetailsItem.
     * 
     * @param vAccessibilitySuitabilityDetailsItem
     * @return true if the object was removed from the collection.
     */
    public boolean removeAccessibilitySuitabilityDetailsItem(
            final chouette.schema.AccessibilitySuitabilityDetailsItem vAccessibilitySuitabilityDetailsItem) {
        boolean removed = _items.remove(vAccessibilitySuitabilityDetailsItem);
        return removed;
    }

    /**
     * Method removeAccessibilitySuitabilityDetailsItemAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.AccessibilitySuitabilityDetailsItem removeAccessibilitySuitabilityDetailsItemAt(
            final int index) {
        java.lang.Object obj = this._items.remove(index);
        return (chouette.schema.AccessibilitySuitabilityDetailsItem) obj;
    }

    /**
     */
    public void removeAllAccessibilitySuitabilityDetailsItem(
    ) {
        this._items.clear();
    }

    /**
     * 
     * 
     * @param index
     * @param vAccessibilitySuitabilityDetailsItem
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setAccessibilitySuitabilityDetailsItem(
            final int index,
            final chouette.schema.AccessibilitySuitabilityDetailsItem vAccessibilitySuitabilityDetailsItem)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._items.size()) {
            throw new IndexOutOfBoundsException("setAccessibilitySuitabilityDetailsItem: Index value '" + index + "' not in range [0.." + (this._items.size() - 1) + "]");
        }

        this._items.set(index, vAccessibilitySuitabilityDetailsItem);
    }

    /**
     * 
     * 
     * @param vAccessibilitySuitabilityDetailsItemArray
     */
    public void setAccessibilitySuitabilityDetailsItem(
            final chouette.schema.AccessibilitySuitabilityDetailsItem[] vAccessibilitySuitabilityDetailsItemArray) {
        //-- copy array
        _items.clear();

        for (int i = 0; i < vAccessibilitySuitabilityDetailsItemArray.length; i++) {
                this._items.add(vAccessibilitySuitabilityDetailsItemArray[i]);
        }
    }

    /**
     * Sets the value of '_items' by copying the given Vector. All
     * elements will be checked for type safety.
     * 
     * @param vAccessibilitySuitabilityDetailsItemList the Vector
     * to copy.
     */
    public void setAccessibilitySuitabilityDetailsItem(
            final java.util.List<chouette.schema.AccessibilitySuitabilityDetailsItem> vAccessibilitySuitabilityDetailsItemList) {
        // copy vector
        this._items.clear();

        this._items.addAll(vAccessibilitySuitabilityDetailsItemList);
    }

    /**
     * Sets the value of '_items' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param accessibilitySuitabilityDetailsItemList the Vector to
     * set.
     */
    public void setAccessibilitySuitabilityDetailsItemAsReference(
            final java.util.List<chouette.schema.AccessibilitySuitabilityDetailsItem> accessibilitySuitabilityDetailsItemList) {
        this._items = accessibilitySuitabilityDetailsItemList;
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
     * chouette.schema.AccessibilitySuitabilityDetails
     */
    public static chouette.schema.AccessibilitySuitabilityDetails unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (chouette.schema.AccessibilitySuitabilityDetails) org.exolab.castor.xml.Unmarshaller.unmarshal(chouette.schema.AccessibilitySuitabilityDetails.class, reader);
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
