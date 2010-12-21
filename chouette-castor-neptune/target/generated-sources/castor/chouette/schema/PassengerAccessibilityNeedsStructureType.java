/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Type for accessibility needs. Records the requirementrs of a
 * passenger that may affect chocie of facilities
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class PassengerAccessibilityNeedsStructureType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Specific pyschosensory need that may constrain choice of
     * services and facilities.
     */
    private java.util.List<chouette.schema.PassengerAccessibilityNeedsStructureUserNeed> _passengerAccessibilityNeedsStructureUserNeedList;

    /**
     * Whether the passenger is accompanied by a carer or assistant.
     */
    private boolean _accompaniedByCarer;

    /**
     * keeps track of state for field: _accompaniedByCarer
     */
    private boolean _has_accompaniedByCarer;


      //----------------/
     //- Constructors -/
    //----------------/

    public PassengerAccessibilityNeedsStructureType() {
        super();
        this._passengerAccessibilityNeedsStructureUserNeedList = new java.util.ArrayList<chouette.schema.PassengerAccessibilityNeedsStructureUserNeed>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vPassengerAccessibilityNeedsStructureUserNeed
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPassengerAccessibilityNeedsStructureUserNeed(
            final chouette.schema.PassengerAccessibilityNeedsStructureUserNeed vPassengerAccessibilityNeedsStructureUserNeed)
    throws java.lang.IndexOutOfBoundsException {
        this._passengerAccessibilityNeedsStructureUserNeedList.add(vPassengerAccessibilityNeedsStructureUserNeed);
    }

    /**
     * 
     * 
     * @param index
     * @param vPassengerAccessibilityNeedsStructureUserNeed
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPassengerAccessibilityNeedsStructureUserNeed(
            final int index,
            final chouette.schema.PassengerAccessibilityNeedsStructureUserNeed vPassengerAccessibilityNeedsStructureUserNeed)
    throws java.lang.IndexOutOfBoundsException {
        this._passengerAccessibilityNeedsStructureUserNeedList.add(index, vPassengerAccessibilityNeedsStructureUserNeed);
    }

    /**
     */
    public void deleteAccompaniedByCarer(
    ) {
        this._has_accompaniedByCarer= false;
    }

    /**
     * Method
     * enumeratePassengerAccessibilityNeedsStructureUserNeed.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.PassengerAccessibilityNeedsStructureUserNeed> enumeratePassengerAccessibilityNeedsStructureUserNeed(
    ) {
        return java.util.Collections.enumeration(this._passengerAccessibilityNeedsStructureUserNeedList);
    }

    /**
     * Returns the value of field 'accompaniedByCarer'. The field
     * 'accompaniedByCarer' has the following description: Whether
     * the passenger is accompanied by a carer or assistant.
     * 
     * @return the value of field 'AccompaniedByCarer'.
     */
    public boolean getAccompaniedByCarer(
    ) {
        return this._accompaniedByCarer;
    }

    /**
     * Method getPassengerAccessibilityNeedsStructureUserNeed.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the
     * chouette.schema.PassengerAccessibilityNeedsStructureUserNeed
     * at the given index
     */
    public chouette.schema.PassengerAccessibilityNeedsStructureUserNeed getPassengerAccessibilityNeedsStructureUserNeed(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._passengerAccessibilityNeedsStructureUserNeedList.size()) {
            throw new IndexOutOfBoundsException("getPassengerAccessibilityNeedsStructureUserNeed: Index value '" + index + "' not in range [0.." + (this._passengerAccessibilityNeedsStructureUserNeedList.size() - 1) + "]");
        }

        return (chouette.schema.PassengerAccessibilityNeedsStructureUserNeed) _passengerAccessibilityNeedsStructureUserNeedList.get(index);
    }

    /**
     * Method
     * getPassengerAccessibilityNeedsStructureUserNeed.Returns the
     * contents of the collection in an Array.  <p>Note:  Just in
     * case the collection contents are changing in another thread,
     * we pass a 0-length Array of the correct type into the API
     * call.  This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.PassengerAccessibilityNeedsStructureUserNeed[] getPassengerAccessibilityNeedsStructureUserNeed(
    ) {
        chouette.schema.PassengerAccessibilityNeedsStructureUserNeed[] array = new chouette.schema.PassengerAccessibilityNeedsStructureUserNeed[0];
        return (chouette.schema.PassengerAccessibilityNeedsStructureUserNeed[]) this._passengerAccessibilityNeedsStructureUserNeedList.toArray(array);
    }

    /**
     * Method
     * getPassengerAccessibilityNeedsStructureUserNeedAsReference.Returns
     * a reference to
     * '_passengerAccessibilityNeedsStructureUserNeedList'. No type
     * checking is performed on any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.PassengerAccessibilityNeedsStructureUserNeed> getPassengerAccessibilityNeedsStructureUserNeedAsReference(
    ) {
        return this._passengerAccessibilityNeedsStructureUserNeedList;
    }

    /**
     * Method getPassengerAccessibilityNeedsStructureUserNeedCount.
     * 
     * @return the size of this collection
     */
    public int getPassengerAccessibilityNeedsStructureUserNeedCount(
    ) {
        return this._passengerAccessibilityNeedsStructureUserNeedList.size();
    }

    /**
     * Method hasAccompaniedByCarer.
     * 
     * @return true if at least one AccompaniedByCarer has been adde
     */
    public boolean hasAccompaniedByCarer(
    ) {
        return this._has_accompaniedByCarer;
    }

    /**
     * Returns the value of field 'accompaniedByCarer'. The field
     * 'accompaniedByCarer' has the following description: Whether
     * the passenger is accompanied by a carer or assistant.
     * 
     * @return the value of field 'AccompaniedByCarer'.
     */
    public boolean isAccompaniedByCarer(
    ) {
        return this._accompaniedByCarer;
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
     * Method iteratePassengerAccessibilityNeedsStructureUserNeed.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.PassengerAccessibilityNeedsStructureUserNeed> iteratePassengerAccessibilityNeedsStructureUserNeed(
    ) {
        return this._passengerAccessibilityNeedsStructureUserNeedList.iterator();
    }

    /**
     */
    public void removeAllPassengerAccessibilityNeedsStructureUserNeed(
    ) {
        this._passengerAccessibilityNeedsStructureUserNeedList.clear();
    }

    /**
     * Method removePassengerAccessibilityNeedsStructureUserNeed.
     * 
     * @param vPassengerAccessibilityNeedsStructureUserNeed
     * @return true if the object was removed from the collection.
     */
    public boolean removePassengerAccessibilityNeedsStructureUserNeed(
            final chouette.schema.PassengerAccessibilityNeedsStructureUserNeed vPassengerAccessibilityNeedsStructureUserNeed) {
        boolean removed = _passengerAccessibilityNeedsStructureUserNeedList.remove(vPassengerAccessibilityNeedsStructureUserNeed);
        return removed;
    }

    /**
     * Method removePassengerAccessibilityNeedsStructureUserNeedAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.PassengerAccessibilityNeedsStructureUserNeed removePassengerAccessibilityNeedsStructureUserNeedAt(
            final int index) {
        java.lang.Object obj = this._passengerAccessibilityNeedsStructureUserNeedList.remove(index);
        return (chouette.schema.PassengerAccessibilityNeedsStructureUserNeed) obj;
    }

    /**
     * Sets the value of field 'accompaniedByCarer'. The field
     * 'accompaniedByCarer' has the following description: Whether
     * the passenger is accompanied by a carer or assistant.
     * 
     * @param accompaniedByCarer the value of field
     * 'accompaniedByCarer'.
     */
    public void setAccompaniedByCarer(
            final boolean accompaniedByCarer) {
        this._accompaniedByCarer = accompaniedByCarer;
        this._has_accompaniedByCarer = true;
    }

    /**
     * 
     * 
     * @param index
     * @param vPassengerAccessibilityNeedsStructureUserNeed
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPassengerAccessibilityNeedsStructureUserNeed(
            final int index,
            final chouette.schema.PassengerAccessibilityNeedsStructureUserNeed vPassengerAccessibilityNeedsStructureUserNeed)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._passengerAccessibilityNeedsStructureUserNeedList.size()) {
            throw new IndexOutOfBoundsException("setPassengerAccessibilityNeedsStructureUserNeed: Index value '" + index + "' not in range [0.." + (this._passengerAccessibilityNeedsStructureUserNeedList.size() - 1) + "]");
        }

        this._passengerAccessibilityNeedsStructureUserNeedList.set(index, vPassengerAccessibilityNeedsStructureUserNeed);
    }

    /**
     * 
     * 
     * @param vPassengerAccessibilityNeedsStructureUserNeedArray
     */
    public void setPassengerAccessibilityNeedsStructureUserNeed(
            final chouette.schema.PassengerAccessibilityNeedsStructureUserNeed[] vPassengerAccessibilityNeedsStructureUserNeedArray) {
        //-- copy array
        _passengerAccessibilityNeedsStructureUserNeedList.clear();

        for (int i = 0; i < vPassengerAccessibilityNeedsStructureUserNeedArray.length; i++) {
                this._passengerAccessibilityNeedsStructureUserNeedList.add(vPassengerAccessibilityNeedsStructureUserNeedArray[i]);
        }
    }

    /**
     * Sets the value of
     * '_passengerAccessibilityNeedsStructureUserNeedList' by
     * copying the given Vector. All elements will be checked for
     * type safety.
     * 
     * @param vPassengerAccessibilityNeedsStructureUserNeedList the
     * Vector to copy.
     */
    public void setPassengerAccessibilityNeedsStructureUserNeed(
            final java.util.List<chouette.schema.PassengerAccessibilityNeedsStructureUserNeed> vPassengerAccessibilityNeedsStructureUserNeedList) {
        // copy vector
        this._passengerAccessibilityNeedsStructureUserNeedList.clear();

        this._passengerAccessibilityNeedsStructureUserNeedList.addAll(vPassengerAccessibilityNeedsStructureUserNeedList);
    }

    /**
     * Sets the value of
     * '_passengerAccessibilityNeedsStructureUserNeedList' by
     * setting it to the given Vector. No type checking is
     * performed.
     * @deprecated
     * 
     * @param passengerAccessibilityNeedsStructureUserNeedList the
     * Vector to set.
     */
    public void setPassengerAccessibilityNeedsStructureUserNeedAsReference(
            final java.util.List<chouette.schema.PassengerAccessibilityNeedsStructureUserNeed> passengerAccessibilityNeedsStructureUserNeedList) {
        this._passengerAccessibilityNeedsStructureUserNeedList = passengerAccessibilityNeedsStructureUserNeedList;
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
