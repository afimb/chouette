/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Facilities that apply to stops.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class StopFacilityGroup extends chouette.schema.castor.SchemaObject 
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
     * Classification of Assistance Facility
     */
    private java.util.List<chouette.schema.types.AssistanceFacilityEnumeration> _assistanceFacilityList;

    /**
     * Classification of Hire Facility
     */
    private java.util.List<chouette.schema.types.HireFacilityEnumeration> _hireFacilityList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StopFacilityGroup() {
        super();
        this._assistanceFacilityList = new java.util.ArrayList<chouette.schema.types.AssistanceFacilityEnumeration>();
        this._hireFacilityList = new java.util.ArrayList<chouette.schema.types.HireFacilityEnumeration>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vAssistanceFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAssistanceFacility(
            final chouette.schema.types.AssistanceFacilityEnumeration vAssistanceFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._assistanceFacilityList.add(vAssistanceFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vAssistanceFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAssistanceFacility(
            final int index,
            final chouette.schema.types.AssistanceFacilityEnumeration vAssistanceFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._assistanceFacilityList.add(index, vAssistanceFacility);
    }

    /**
     * 
     * 
     * @param vHireFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addHireFacility(
            final chouette.schema.types.HireFacilityEnumeration vHireFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._hireFacilityList.add(vHireFacility);
    }

    /**
     * 
     * 
     * @param index
     * @param vHireFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addHireFacility(
            final int index,
            final chouette.schema.types.HireFacilityEnumeration vHireFacility)
    throws java.lang.IndexOutOfBoundsException {
        this._hireFacilityList.add(index, vHireFacility);
    }

    /**
     * Method enumerateAssistanceFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.types.AssistanceFacilityEnumeration> enumerateAssistanceFacility(
    ) {
        return java.util.Collections.enumeration(this._assistanceFacilityList);
    }

    /**
     * Method enumerateHireFacility.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.types.HireFacilityEnumeration> enumerateHireFacility(
    ) {
        return java.util.Collections.enumeration(this._hireFacilityList);
    }

    /**
     * Method getAssistanceFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.types.AssistanceFacilityEnumeration at the
     * given index
     */
    public chouette.schema.types.AssistanceFacilityEnumeration getAssistanceFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._assistanceFacilityList.size()) {
            throw new IndexOutOfBoundsException("getAssistanceFacility: Index value '" + index + "' not in range [0.." + (this._assistanceFacilityList.size() - 1) + "]");
        }

        return (chouette.schema.types.AssistanceFacilityEnumeration) _assistanceFacilityList.get(index);
    }

    /**
     * Method getAssistanceFacility.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.types.AssistanceFacilityEnumeration[] getAssistanceFacility(
    ) {
        chouette.schema.types.AssistanceFacilityEnumeration[] array = new chouette.schema.types.AssistanceFacilityEnumeration[0];
        return (chouette.schema.types.AssistanceFacilityEnumeration[]) this._assistanceFacilityList.toArray(array);
    }

    /**
     * Method getAssistanceFacilityAsReference.Returns a reference
     * to '_assistanceFacilityList'. No type checking is performed
     * on any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.types.AssistanceFacilityEnumeration> getAssistanceFacilityAsReference(
    ) {
        return this._assistanceFacilityList;
    }

    /**
     * Method getAssistanceFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getAssistanceFacilityCount(
    ) {
        return this._assistanceFacilityList.size();
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
     * Method getHireFacility.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.types.HireFacilityEnumeration at the given
     * index
     */
    public chouette.schema.types.HireFacilityEnumeration getHireFacility(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._hireFacilityList.size()) {
            throw new IndexOutOfBoundsException("getHireFacility: Index value '" + index + "' not in range [0.." + (this._hireFacilityList.size() - 1) + "]");
        }

        return (chouette.schema.types.HireFacilityEnumeration) _hireFacilityList.get(index);
    }

    /**
     * Method getHireFacility.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.types.HireFacilityEnumeration[] getHireFacility(
    ) {
        chouette.schema.types.HireFacilityEnumeration[] array = new chouette.schema.types.HireFacilityEnumeration[0];
        return (chouette.schema.types.HireFacilityEnumeration[]) this._hireFacilityList.toArray(array);
    }

    /**
     * Method getHireFacilityAsReference.Returns a reference to
     * '_hireFacilityList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.types.HireFacilityEnumeration> getHireFacilityAsReference(
    ) {
        return this._hireFacilityList;
    }

    /**
     * Method getHireFacilityCount.
     * 
     * @return the size of this collection
     */
    public int getHireFacilityCount(
    ) {
        return this._hireFacilityList.size();
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
     * Method iterateAssistanceFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.types.AssistanceFacilityEnumeration> iterateAssistanceFacility(
    ) {
        return this._assistanceFacilityList.iterator();
    }

    /**
     * Method iterateHireFacility.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.types.HireFacilityEnumeration> iterateHireFacility(
    ) {
        return this._hireFacilityList.iterator();
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
    public void removeAllAssistanceFacility(
    ) {
        this._assistanceFacilityList.clear();
    }

    /**
     */
    public void removeAllHireFacility(
    ) {
        this._hireFacilityList.clear();
    }

    /**
     * Method removeAssistanceFacility.
     * 
     * @param vAssistanceFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removeAssistanceFacility(
            final chouette.schema.types.AssistanceFacilityEnumeration vAssistanceFacility) {
        boolean removed = _assistanceFacilityList.remove(vAssistanceFacility);
        return removed;
    }

    /**
     * Method removeAssistanceFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.types.AssistanceFacilityEnumeration removeAssistanceFacilityAt(
            final int index) {
        java.lang.Object obj = this._assistanceFacilityList.remove(index);
        return (chouette.schema.types.AssistanceFacilityEnumeration) obj;
    }

    /**
     * Method removeHireFacility.
     * 
     * @param vHireFacility
     * @return true if the object was removed from the collection.
     */
    public boolean removeHireFacility(
            final chouette.schema.types.HireFacilityEnumeration vHireFacility) {
        boolean removed = _hireFacilityList.remove(vHireFacility);
        return removed;
    }

    /**
     * Method removeHireFacilityAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.types.HireFacilityEnumeration removeHireFacilityAt(
            final int index) {
        java.lang.Object obj = this._hireFacilityList.remove(index);
        return (chouette.schema.types.HireFacilityEnumeration) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vAssistanceFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setAssistanceFacility(
            final int index,
            final chouette.schema.types.AssistanceFacilityEnumeration vAssistanceFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._assistanceFacilityList.size()) {
            throw new IndexOutOfBoundsException("setAssistanceFacility: Index value '" + index + "' not in range [0.." + (this._assistanceFacilityList.size() - 1) + "]");
        }

        this._assistanceFacilityList.set(index, vAssistanceFacility);
    }

    /**
     * 
     * 
     * @param vAssistanceFacilityArray
     */
    public void setAssistanceFacility(
            final chouette.schema.types.AssistanceFacilityEnumeration[] vAssistanceFacilityArray) {
        //-- copy array
        _assistanceFacilityList.clear();

        for (int i = 0; i < vAssistanceFacilityArray.length; i++) {
                this._assistanceFacilityList.add(vAssistanceFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_assistanceFacilityList' by copying the
     * given Vector. All elements will be checked for type safety.
     * 
     * @param vAssistanceFacilityList the Vector to copy.
     */
    public void setAssistanceFacility(
            final java.util.List<chouette.schema.types.AssistanceFacilityEnumeration> vAssistanceFacilityList) {
        // copy vector
        this._assistanceFacilityList.clear();

        this._assistanceFacilityList.addAll(vAssistanceFacilityList);
    }

    /**
     * Sets the value of '_assistanceFacilityList' by setting it to
     * the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param assistanceFacilityList the Vector to set.
     */
    public void setAssistanceFacilityAsReference(
            final java.util.List<chouette.schema.types.AssistanceFacilityEnumeration> assistanceFacilityList) {
        this._assistanceFacilityList = assistanceFacilityList;
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
     * 
     * 
     * @param index
     * @param vHireFacility
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setHireFacility(
            final int index,
            final chouette.schema.types.HireFacilityEnumeration vHireFacility)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._hireFacilityList.size()) {
            throw new IndexOutOfBoundsException("setHireFacility: Index value '" + index + "' not in range [0.." + (this._hireFacilityList.size() - 1) + "]");
        }

        this._hireFacilityList.set(index, vHireFacility);
    }

    /**
     * 
     * 
     * @param vHireFacilityArray
     */
    public void setHireFacility(
            final chouette.schema.types.HireFacilityEnumeration[] vHireFacilityArray) {
        //-- copy array
        _hireFacilityList.clear();

        for (int i = 0; i < vHireFacilityArray.length; i++) {
                this._hireFacilityList.add(vHireFacilityArray[i]);
        }
    }

    /**
     * Sets the value of '_hireFacilityList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vHireFacilityList the Vector to copy.
     */
    public void setHireFacility(
            final java.util.List<chouette.schema.types.HireFacilityEnumeration> vHireFacilityList) {
        // copy vector
        this._hireFacilityList.clear();

        this._hireFacilityList.addAll(vHireFacilityList);
    }

    /**
     * Sets the value of '_hireFacilityList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param hireFacilityList the Vector to set.
     */
    public void setHireFacilityAsReference(
            final java.util.List<chouette.schema.types.HireFacilityEnumeration> hireFacilityList) {
        this._hireFacilityList = hireFacilityList;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled chouette.schema.StopFacilityGroup
     */
    public static chouette.schema.StopFacilityGroup unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (chouette.schema.StopFacilityGroup) org.exolab.castor.xml.Unmarshaller.unmarshal(chouette.schema.StopFacilityGroup.class, reader);
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
