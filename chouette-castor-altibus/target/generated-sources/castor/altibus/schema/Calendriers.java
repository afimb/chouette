/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package altibus.schema;

/**
 * Class Calendriers.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Calendriers extends java.lang.Object 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _calendrierList.
     */
    private java.util.List<altibus.schema.Calendrier> _calendrierList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Calendriers() {
        super();
        this._calendrierList = new java.util.ArrayList<altibus.schema.Calendrier>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vCalendrier
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCalendrier(
            final altibus.schema.Calendrier vCalendrier)
    throws java.lang.IndexOutOfBoundsException {
        this._calendrierList.add(vCalendrier);
    }

    /**
     * 
     * 
     * @param index
     * @param vCalendrier
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCalendrier(
            final int index,
            final altibus.schema.Calendrier vCalendrier)
    throws java.lang.IndexOutOfBoundsException {
        this._calendrierList.add(index, vCalendrier);
    }

    /**
     * Method enumerateCalendrier.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends altibus.schema.Calendrier> enumerateCalendrier(
    ) {
        return java.util.Collections.enumeration(this._calendrierList);
    }

    /**
     * Method getCalendrier.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the altibus.schema.Calendrier at the
     * given index
     */
    public altibus.schema.Calendrier getCalendrier(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._calendrierList.size()) {
            throw new IndexOutOfBoundsException("getCalendrier: Index value '" + index + "' not in range [0.." + (this._calendrierList.size() - 1) + "]");
        }

        return (altibus.schema.Calendrier) _calendrierList.get(index);
    }

    /**
     * Method getCalendrier.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public altibus.schema.Calendrier[] getCalendrier(
    ) {
        altibus.schema.Calendrier[] array = new altibus.schema.Calendrier[0];
        return (altibus.schema.Calendrier[]) this._calendrierList.toArray(array);
    }

    /**
     * Method getCalendrierAsReference.Returns a reference to
     * '_calendrierList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<altibus.schema.Calendrier> getCalendrierAsReference(
    ) {
        return this._calendrierList;
    }

    /**
     * Method getCalendrierCount.
     * 
     * @return the size of this collection
     */
    public int getCalendrierCount(
    ) {
        return this._calendrierList.size();
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
     * Method iterateCalendrier.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends altibus.schema.Calendrier> iterateCalendrier(
    ) {
        return this._calendrierList.iterator();
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
    public void removeAllCalendrier(
    ) {
        this._calendrierList.clear();
    }

    /**
     * Method removeCalendrier.
     * 
     * @param vCalendrier
     * @return true if the object was removed from the collection.
     */
    public boolean removeCalendrier(
            final altibus.schema.Calendrier vCalendrier) {
        boolean removed = _calendrierList.remove(vCalendrier);
        return removed;
    }

    /**
     * Method removeCalendrierAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public altibus.schema.Calendrier removeCalendrierAt(
            final int index) {
        java.lang.Object obj = this._calendrierList.remove(index);
        return (altibus.schema.Calendrier) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vCalendrier
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setCalendrier(
            final int index,
            final altibus.schema.Calendrier vCalendrier)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._calendrierList.size()) {
            throw new IndexOutOfBoundsException("setCalendrier: Index value '" + index + "' not in range [0.." + (this._calendrierList.size() - 1) + "]");
        }

        this._calendrierList.set(index, vCalendrier);
    }

    /**
     * 
     * 
     * @param vCalendrierArray
     */
    public void setCalendrier(
            final altibus.schema.Calendrier[] vCalendrierArray) {
        //-- copy array
        _calendrierList.clear();

        for (int i = 0; i < vCalendrierArray.length; i++) {
                this._calendrierList.add(vCalendrierArray[i]);
        }
    }

    /**
     * Sets the value of '_calendrierList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vCalendrierList the Vector to copy.
     */
    public void setCalendrier(
            final java.util.List<altibus.schema.Calendrier> vCalendrierList) {
        // copy vector
        this._calendrierList.clear();

        this._calendrierList.addAll(vCalendrierList);
    }

    /**
     * Sets the value of '_calendrierList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param calendrierList the Vector to set.
     */
    public void setCalendrierAsReference(
            final java.util.List<altibus.schema.Calendrier> calendrierList) {
        this._calendrierList = calendrierList;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled altibus.schema.Calendriers
     */
    public static altibus.schema.Calendriers unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (altibus.schema.Calendriers) org.exolab.castor.xml.Unmarshaller.unmarshal(altibus.schema.Calendriers.class, reader);
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
