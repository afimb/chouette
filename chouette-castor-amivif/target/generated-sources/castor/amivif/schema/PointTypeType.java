/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * General point used to build any kind of point
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class PointTypeType extends amivif.schema.LocationTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _longitude.
     */
    private java.math.BigDecimal _longitude;

    /**
     * Field _latitude.
     */
    private java.math.BigDecimal _latitude;

    /**
     * Field _longLatType.
     */
    private amivif.schema.types.LongLatTypeType _longLatType;

    /**
     * Field _languageCode.
     */
    private java.lang.String _languageCode;

    /**
     * Field _address.
     */
    private amivif.schema.Address _address;

    /**
     * Field _pointOfInterest.
     */
    private amivif.schema.PointOfInterest _pointOfInterest;

    /**
     * Field _projectedPoint.
     */
    private amivif.schema.ProjectedPoint _projectedPoint;

    /**
     * Field _containedInList.
     */
    private java.util.List<java.lang.String> _containedInList;


      //----------------/
     //- Constructors -/
    //----------------/

    public PointTypeType() {
        super();
        this._containedInList = new java.util.ArrayList<java.lang.String>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vContainedIn
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addContainedIn(
            final java.lang.String vContainedIn)
    throws java.lang.IndexOutOfBoundsException {
        this._containedInList.add(vContainedIn);
    }

    /**
     * 
     * 
     * @param index
     * @param vContainedIn
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addContainedIn(
            final int index,
            final java.lang.String vContainedIn)
    throws java.lang.IndexOutOfBoundsException {
        this._containedInList.add(index, vContainedIn);
    }

    /**
     * Method enumerateContainedIn.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumerateContainedIn(
    ) {
        return java.util.Collections.enumeration(this._containedInList);
    }

    /**
     * Returns the value of field 'address'.
     * 
     * @return the value of field 'Address'.
     */
    public amivif.schema.Address getAddress(
    ) {
        return this._address;
    }

    /**
     * Method getContainedIn.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getContainedIn(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._containedInList.size()) {
            throw new IndexOutOfBoundsException("getContainedIn: Index value '" + index + "' not in range [0.." + (this._containedInList.size() - 1) + "]");
        }

        return (java.lang.String) _containedInList.get(index);
    }

    /**
     * Method getContainedIn.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getContainedIn(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._containedInList.toArray(array);
    }

    /**
     * Method getContainedInAsReference.Returns a reference to
     * '_containedInList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getContainedInAsReference(
    ) {
        return this._containedInList;
    }

    /**
     * Method getContainedInCount.
     * 
     * @return the size of this collection
     */
    public int getContainedInCount(
    ) {
        return this._containedInList.size();
    }

    /**
     * Returns the value of field 'languageCode'.
     * 
     * @return the value of field 'LanguageCode'.
     */
    public java.lang.String getLanguageCode(
    ) {
        return this._languageCode;
    }

    /**
     * Returns the value of field 'latitude'.
     * 
     * @return the value of field 'Latitude'.
     */
    public java.math.BigDecimal getLatitude(
    ) {
        return this._latitude;
    }

    /**
     * Returns the value of field 'longLatType'.
     * 
     * @return the value of field 'LongLatType'.
     */
    public amivif.schema.types.LongLatTypeType getLongLatType(
    ) {
        return this._longLatType;
    }

    /**
     * Returns the value of field 'longitude'.
     * 
     * @return the value of field 'Longitude'.
     */
    public java.math.BigDecimal getLongitude(
    ) {
        return this._longitude;
    }

    /**
     * Returns the value of field 'pointOfInterest'.
     * 
     * @return the value of field 'PointOfInterest'.
     */
    public amivif.schema.PointOfInterest getPointOfInterest(
    ) {
        return this._pointOfInterest;
    }

    /**
     * Returns the value of field 'projectedPoint'.
     * 
     * @return the value of field 'ProjectedPoint'.
     */
    public amivif.schema.ProjectedPoint getProjectedPoint(
    ) {
        return this._projectedPoint;
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
     * Method iterateContainedIn.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iterateContainedIn(
    ) {
        return this._containedInList.iterator();
    }

    /**
     */
    public void removeAllContainedIn(
    ) {
        this._containedInList.clear();
    }

    /**
     * Method removeContainedIn.
     * 
     * @param vContainedIn
     * @return true if the object was removed from the collection.
     */
    public boolean removeContainedIn(
            final java.lang.String vContainedIn) {
        boolean removed = _containedInList.remove(vContainedIn);
        return removed;
    }

    /**
     * Method removeContainedInAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removeContainedInAt(
            final int index) {
        java.lang.Object obj = this._containedInList.remove(index);
        return (java.lang.String) obj;
    }

    /**
     * Sets the value of field 'address'.
     * 
     * @param address the value of field 'address'.
     */
    public void setAddress(
            final amivif.schema.Address address) {
        this._address = address;
    }

    /**
     * 
     * 
     * @param index
     * @param vContainedIn
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setContainedIn(
            final int index,
            final java.lang.String vContainedIn)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._containedInList.size()) {
            throw new IndexOutOfBoundsException("setContainedIn: Index value '" + index + "' not in range [0.." + (this._containedInList.size() - 1) + "]");
        }

        this._containedInList.set(index, vContainedIn);
    }

    /**
     * 
     * 
     * @param vContainedInArray
     */
    public void setContainedIn(
            final java.lang.String[] vContainedInArray) {
        //-- copy array
        _containedInList.clear();

        for (int i = 0; i < vContainedInArray.length; i++) {
                this._containedInList.add(vContainedInArray[i]);
        }
    }

    /**
     * Sets the value of '_containedInList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vContainedInList the Vector to copy.
     */
    public void setContainedIn(
            final java.util.List<java.lang.String> vContainedInList) {
        // copy vector
        this._containedInList.clear();

        this._containedInList.addAll(vContainedInList);
    }

    /**
     * Sets the value of '_containedInList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param containedInList the Vector to set.
     */
    public void setContainedInAsReference(
            final java.util.List<java.lang.String> containedInList) {
        this._containedInList = containedInList;
    }

    /**
     * Sets the value of field 'languageCode'.
     * 
     * @param languageCode the value of field 'languageCode'.
     */
    public void setLanguageCode(
            final java.lang.String languageCode) {
        this._languageCode = languageCode;
    }

    /**
     * Sets the value of field 'latitude'.
     * 
     * @param latitude the value of field 'latitude'.
     */
    public void setLatitude(
            final java.math.BigDecimal latitude) {
        this._latitude = latitude;
    }

    /**
     * Sets the value of field 'longLatType'.
     * 
     * @param longLatType the value of field 'longLatType'.
     */
    public void setLongLatType(
            final amivif.schema.types.LongLatTypeType longLatType) {
        this._longLatType = longLatType;
    }

    /**
     * Sets the value of field 'longitude'.
     * 
     * @param longitude the value of field 'longitude'.
     */
    public void setLongitude(
            final java.math.BigDecimal longitude) {
        this._longitude = longitude;
    }

    /**
     * Sets the value of field 'pointOfInterest'.
     * 
     * @param pointOfInterest the value of field 'pointOfInterest'.
     */
    public void setPointOfInterest(
            final amivif.schema.PointOfInterest pointOfInterest) {
        this._pointOfInterest = pointOfInterest;
    }

    /**
     * Sets the value of field 'projectedPoint'.
     * 
     * @param projectedPoint the value of field 'projectedPoint'.
     */
    public void setProjectedPoint(
            final amivif.schema.ProjectedPoint projectedPoint) {
        this._projectedPoint = projectedPoint;
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
