/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * Registration informations
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class RegistrationTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _registrationNumber.
     */
    private java.lang.String _registrationNumber;

    /**
     * Field _ptNetworkIDList.
     */
    private java.util.List<java.lang.String> _ptNetworkIDList;

    /**
     * Field _lineIdList.
     */
    private java.util.List<java.lang.String> _lineIdList;

    /**
     * Field _companyId.
     */
    private java.lang.String _companyId;


      //----------------/
     //- Constructors -/
    //----------------/

    public RegistrationTypeType() {
        super();
        this._ptNetworkIDList = new java.util.ArrayList<java.lang.String>();
        this._lineIdList = new java.util.ArrayList<java.lang.String>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vLineId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addLineId(
            final java.lang.String vLineId)
    throws java.lang.IndexOutOfBoundsException {
        this._lineIdList.add(vLineId);
    }

    /**
     * 
     * 
     * @param index
     * @param vLineId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addLineId(
            final int index,
            final java.lang.String vLineId)
    throws java.lang.IndexOutOfBoundsException {
        this._lineIdList.add(index, vLineId);
    }

    /**
     * 
     * 
     * @param vPtNetworkID
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPtNetworkID(
            final java.lang.String vPtNetworkID)
    throws java.lang.IndexOutOfBoundsException {
        this._ptNetworkIDList.add(vPtNetworkID);
    }

    /**
     * 
     * 
     * @param index
     * @param vPtNetworkID
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPtNetworkID(
            final int index,
            final java.lang.String vPtNetworkID)
    throws java.lang.IndexOutOfBoundsException {
        this._ptNetworkIDList.add(index, vPtNetworkID);
    }

    /**
     * Method enumerateLineId.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumerateLineId(
    ) {
        return java.util.Collections.enumeration(this._lineIdList);
    }

    /**
     * Method enumeratePtNetworkID.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumeratePtNetworkID(
    ) {
        return java.util.Collections.enumeration(this._ptNetworkIDList);
    }

    /**
     * Returns the value of field 'companyId'.
     * 
     * @return the value of field 'CompanyId'.
     */
    public java.lang.String getCompanyId(
    ) {
        return this._companyId;
    }

    /**
     * Method getLineId.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getLineId(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._lineIdList.size()) {
            throw new IndexOutOfBoundsException("getLineId: Index value '" + index + "' not in range [0.." + (this._lineIdList.size() - 1) + "]");
        }

        return (java.lang.String) _lineIdList.get(index);
    }

    /**
     * Method getLineId.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getLineId(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._lineIdList.toArray(array);
    }

    /**
     * Method getLineIdAsReference.Returns a reference to
     * '_lineIdList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getLineIdAsReference(
    ) {
        return this._lineIdList;
    }

    /**
     * Method getLineIdCount.
     * 
     * @return the size of this collection
     */
    public int getLineIdCount(
    ) {
        return this._lineIdList.size();
    }

    /**
     * Method getPtNetworkID.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getPtNetworkID(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ptNetworkIDList.size()) {
            throw new IndexOutOfBoundsException("getPtNetworkID: Index value '" + index + "' not in range [0.." + (this._ptNetworkIDList.size() - 1) + "]");
        }

        return (java.lang.String) _ptNetworkIDList.get(index);
    }

    /**
     * Method getPtNetworkID.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getPtNetworkID(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._ptNetworkIDList.toArray(array);
    }

    /**
     * Method getPtNetworkIDAsReference.Returns a reference to
     * '_ptNetworkIDList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getPtNetworkIDAsReference(
    ) {
        return this._ptNetworkIDList;
    }

    /**
     * Method getPtNetworkIDCount.
     * 
     * @return the size of this collection
     */
    public int getPtNetworkIDCount(
    ) {
        return this._ptNetworkIDList.size();
    }

    /**
     * Returns the value of field 'registrationNumber'.
     * 
     * @return the value of field 'RegistrationNumber'.
     */
    public java.lang.String getRegistrationNumber(
    ) {
        return this._registrationNumber;
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
     * Method iterateLineId.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iterateLineId(
    ) {
        return this._lineIdList.iterator();
    }

    /**
     * Method iteratePtNetworkID.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iteratePtNetworkID(
    ) {
        return this._ptNetworkIDList.iterator();
    }

    /**
     */
    public void removeAllLineId(
    ) {
        this._lineIdList.clear();
    }

    /**
     */
    public void removeAllPtNetworkID(
    ) {
        this._ptNetworkIDList.clear();
    }

    /**
     * Method removeLineId.
     * 
     * @param vLineId
     * @return true if the object was removed from the collection.
     */
    public boolean removeLineId(
            final java.lang.String vLineId) {
        boolean removed = _lineIdList.remove(vLineId);
        return removed;
    }

    /**
     * Method removeLineIdAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removeLineIdAt(
            final int index) {
        java.lang.Object obj = this._lineIdList.remove(index);
        return (java.lang.String) obj;
    }

    /**
     * Method removePtNetworkID.
     * 
     * @param vPtNetworkID
     * @return true if the object was removed from the collection.
     */
    public boolean removePtNetworkID(
            final java.lang.String vPtNetworkID) {
        boolean removed = _ptNetworkIDList.remove(vPtNetworkID);
        return removed;
    }

    /**
     * Method removePtNetworkIDAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removePtNetworkIDAt(
            final int index) {
        java.lang.Object obj = this._ptNetworkIDList.remove(index);
        return (java.lang.String) obj;
    }

    /**
     * Sets the value of field 'companyId'.
     * 
     * @param companyId the value of field 'companyId'.
     */
    public void setCompanyId(
            final java.lang.String companyId) {
        this._companyId = companyId;
    }

    /**
     * 
     * 
     * @param index
     * @param vLineId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setLineId(
            final int index,
            final java.lang.String vLineId)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._lineIdList.size()) {
            throw new IndexOutOfBoundsException("setLineId: Index value '" + index + "' not in range [0.." + (this._lineIdList.size() - 1) + "]");
        }

        this._lineIdList.set(index, vLineId);
    }

    /**
     * 
     * 
     * @param vLineIdArray
     */
    public void setLineId(
            final java.lang.String[] vLineIdArray) {
        //-- copy array
        _lineIdList.clear();

        for (int i = 0; i < vLineIdArray.length; i++) {
                this._lineIdList.add(vLineIdArray[i]);
        }
    }

    /**
     * Sets the value of '_lineIdList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vLineIdList the Vector to copy.
     */
    public void setLineId(
            final java.util.List<java.lang.String> vLineIdList) {
        // copy vector
        this._lineIdList.clear();

        this._lineIdList.addAll(vLineIdList);
    }

    /**
     * Sets the value of '_lineIdList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param lineIdList the Vector to set.
     */
    public void setLineIdAsReference(
            final java.util.List<java.lang.String> lineIdList) {
        this._lineIdList = lineIdList;
    }

    /**
     * 
     * 
     * @param index
     * @param vPtNetworkID
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPtNetworkID(
            final int index,
            final java.lang.String vPtNetworkID)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ptNetworkIDList.size()) {
            throw new IndexOutOfBoundsException("setPtNetworkID: Index value '" + index + "' not in range [0.." + (this._ptNetworkIDList.size() - 1) + "]");
        }

        this._ptNetworkIDList.set(index, vPtNetworkID);
    }

    /**
     * 
     * 
     * @param vPtNetworkIDArray
     */
    public void setPtNetworkID(
            final java.lang.String[] vPtNetworkIDArray) {
        //-- copy array
        _ptNetworkIDList.clear();

        for (int i = 0; i < vPtNetworkIDArray.length; i++) {
                this._ptNetworkIDList.add(vPtNetworkIDArray[i]);
        }
    }

    /**
     * Sets the value of '_ptNetworkIDList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vPtNetworkIDList the Vector to copy.
     */
    public void setPtNetworkID(
            final java.util.List<java.lang.String> vPtNetworkIDList) {
        // copy vector
        this._ptNetworkIDList.clear();

        this._ptNetworkIDList.addAll(vPtNetworkIDList);
    }

    /**
     * Sets the value of '_ptNetworkIDList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param ptNetworkIDList the Vector to set.
     */
    public void setPtNetworkIDAsReference(
            final java.util.List<java.lang.String> ptNetworkIDList) {
        this._ptNetworkIDList = ptNetworkIDList;
    }

    /**
     * Sets the value of field 'registrationNumber'.
     * 
     * @param registrationNumber the value of field
     * 'registrationNumber'.
     */
    public void setRegistrationNumber(
            final java.lang.String registrationNumber) {
        this._registrationNumber = registrationNumber;
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
