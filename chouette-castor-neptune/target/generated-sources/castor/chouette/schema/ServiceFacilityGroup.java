/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Facilities that apply to services.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ServiceFacilityGroup extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _commonFacilityGroup.
     */
    private chouette.schema.CommonFacilityGroup _commonFacilityGroup;

    /**
     * Classification of Accomodation Facility type - Tpeg pti23.
     */
    private java.util.List<chouette.schema.types.AccommodationFacilityEnumeration> _accommodationFacilityList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ServiceFacilityGroup() {
        super();
        this._accommodationFacilityList = new java.util.ArrayList<chouette.schema.types.AccommodationFacilityEnumeration>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vAccommodationFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAccommodationFacility(
            final chouette.schema.types.AccommodationFacilityEnumeration vAccommodationFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._accommodationFacilityList.add(vAccommodationFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vAccommodationFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAccommodationFacility(
            final int index,
            final chouette.schema.types.AccommodationFacilityEnumeration vAccommodationFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._accommodationFacilityList.add(index, vAccommodationFacility);
    }

    /**
     * Method enumerateAccommodationFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.types.AccommodationFacilityEnumeration> enumerateAccommodationFacility(
    ) {
        return java.util.Collections.enumeration(this._accommodationFacilityList);
    }

    /**
     * Method getAccommodationFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.types.AccommodationFacilityEnumeration at
     * the given index
     */
    public chouette.schema.types.AccommodationFacilityEnumeration getAccommodationFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._accommodationFacilityList.size()) {
            throw new IndexOutOfBoundsException("getAccommodationFacility: Index value '" + index + "' not in range [0.." + (this._accommodationFacilityList.size() - 1) + "]");
        }

        return (chouette.schema.types.AccommodationFacilityEnumeration) _accommodationFacilityList.get(index);
    }

    /**
     * Method getAccommodationFacility.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.types.AccommodationFacilityEnumeration[] getAccommodationFacility(
    ) {
        chouette.schema.types.AccommodationFacilityEnumeration[] array = new chouette.schema.types.AccommodationFacilityEnumeration[0];
        return (chouette.schema.types.AccommodationFacilityEnumeration[]) this._accommodationFacilityList.toArray(array);
    }

    /**
     * Method getAccommodationFacilityAsReference.Returns a
     * reference to '_accommodationFacilityList'. No type checking
     * is performed on any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.types.AccommodationFacilityEnumeration> getAccommodationFacilityAsReference(
    ) {
        return this._accommodationFacilityList;
    }

    /**
     * Method getAccommodationFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getAccommodationFacilityCount(
    ) {
        return this._accommodationFacilityList.size();
    }

    /**
     * Returns the value of field 'commonFacilityGroup'.
     * 
     * @return the value of field 'CommonFacilityGroup'.
     */
    public chouette.schema.CommonFacilityGroup getCommonFacilityGroup(
    ) {
        return this._commonFacilityGroup;
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
     * Method iterateAccommodationFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.types.AccommodationFacilityEnumeration> iterateAccommodationFacility(
    ) {
        return this._accommodationFacilityList.iterator();
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
     * Method removeAccommodationFacility.
     * 
     * @param vAccommodationFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removeAccommodationFacility(
            final chouette.schema.types.AccommodationFacilityEnumeration vAccommodationFacility) {
        boolean removed = _accommodationFacilityList.remove(vAccommodationFacility);
        return removed;
    }

    /**
     * Method removeAccommodationFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.types.AccommodationFacilityEnumeration removeAccommodationFacilityAt(
            final int index) {
        java.lang.Object obj = this._accommodationFacilityList.remove(index);
        return (chouette.schema.types.AccommodationFacilityEnumeration) obj;
    }

    /**
     */
    public void removeAllAccommodationFacility(
    ) {
        this._accommodationFacilityList.clear();
    }

    /**
     * 
     * 
     * @param index
     * @param vAccommodationFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setAccommodationFacility(
            final int index,
            final chouette.schema.types.AccommodationFacilityEnumeration vAccommodationFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._accommodationFacilityList.size()) {
            throw new IndexOutOfBoundsException("setAccommodationFacility: Index value '" + index + "' not in range [0.." + (this._accommodationFacilityList.size() - 1) + "]");
        }

        this._accommodationFacilityList.set(index, vAccommodationFacility);
    }

    /**
     * 
     * 
     * @param vAccommodationFacilityArray
     */
    public void setAccommodationFacility(
            final chouette.schema.types.AccommodationFacilityEnumeration[] vAccommodationFacilityArray) {
        //-- copy array
        _accommodationFacilityList.clear();

        for (int i = 0; i < vAccommodationFacilityArray.length; i++) {
                this._accommodationFacilityList.add(vAccommodationFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_accommodationFacilityList' by copying
     * the given Vector. All elements will be checked for type
     * safety.
     * 
     * @param vAccommodationFacilityList the Vector to copy.
     */
    public void setAccommodationFacility(
            final java.util.List<chouette.schema.types.AccommodationFacilityEnumeration> vAccommodationFacilityList) {
        // copy vector
        this._accommodationFacilityList.clear();

        this._accommodationFacilityList.addAll(vAccommodationFacilityList);
    }

    /**
     * Sets the value of '_accommodationFacilityList' by setting it
     * to the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param accommodationFacilityList the Vector to set.
     */
    public void setAccommodationFacilityAsReference(
            final java.util.List<chouette.schema.types.AccommodationFacilityEnumeration> accommodationFacilityList) {
        this._accommodationFacilityList = accommodationFacilityList;
    }

    /**
     * Sets the value of field 'commonFacilityGroup'.
     * 
     * @param commonFacilityGroup the value of field
     * 'commonFacilityGroup'.
     */
    public void setCommonFacilityGroup(
            final chouette.schema.CommonFacilityGroup commonFacilityGroup) {
        this._commonFacilityGroup = commonFacilityGroup;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled chouette.schema.ServiceFacilityGroup
     */
    public static chouette.schema.ServiceFacilityGroup unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (chouette.schema.ServiceFacilityGroup) org.exolab.castor.xml.Unmarshaller.unmarshal(chouette.schema.ServiceFacilityGroup.class, reader);
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
