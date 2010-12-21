/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * PT Network description, and link to all the entry point
 * for this network in the Data Model.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class RoadNetworkTypeType extends amivif.schema.TransportNetworkTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name.
     */
    private java.lang.String _name;

    /**
     * Field _junctionIdList.
     */
    private java.util.List<java.lang.String> _junctionIdList;

    /**
     * Field _roadElementIdList.
     */
    private java.util.List<java.lang.String> _roadElementIdList;

    /**
     * Field _comment.
     */
    private java.lang.String _comment;


      //----------------/
     //- Constructors -/
    //----------------/

    public RoadNetworkTypeType() {
        super();
        this._junctionIdList = new java.util.ArrayList<java.lang.String>();
        this._roadElementIdList = new java.util.ArrayList<java.lang.String>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vJunctionId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addJunctionId(
            final java.lang.String vJunctionId)
    throws java.lang.IndexOutOfBoundsException {
        this._junctionIdList.add(vJunctionId);
    }

    /**
     * 
     * 
     * @param index
     * @param vJunctionId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addJunctionId(
            final int index,
            final java.lang.String vJunctionId)
    throws java.lang.IndexOutOfBoundsException {
        this._junctionIdList.add(index, vJunctionId);
    }

    /**
     * 
     * 
     * @param vRoadElementId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addRoadElementId(
            final java.lang.String vRoadElementId)
    throws java.lang.IndexOutOfBoundsException {
        this._roadElementIdList.add(vRoadElementId);
    }

    /**
     * 
     * 
     * @param index
     * @param vRoadElementId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addRoadElementId(
            final int index,
            final java.lang.String vRoadElementId)
    throws java.lang.IndexOutOfBoundsException {
        this._roadElementIdList.add(index, vRoadElementId);
    }

    /**
     * Method enumerateJunctionId.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumerateJunctionId(
    ) {
        return java.util.Collections.enumeration(this._junctionIdList);
    }

    /**
     * Method enumerateRoadElementId.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumerateRoadElementId(
    ) {
        return java.util.Collections.enumeration(this._roadElementIdList);
    }

    /**
     * Returns the value of field 'comment'.
     * 
     * @return the value of field 'Comment'.
     */
    public java.lang.String getComment(
    ) {
        return this._comment;
    }

    /**
     * Method getJunctionId.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getJunctionId(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._junctionIdList.size()) {
            throw new IndexOutOfBoundsException("getJunctionId: Index value '" + index + "' not in range [0.." + (this._junctionIdList.size() - 1) + "]");
        }

        return (java.lang.String) _junctionIdList.get(index);
    }

    /**
     * Method getJunctionId.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getJunctionId(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._junctionIdList.toArray(array);
    }

    /**
     * Method getJunctionIdAsReference.Returns a reference to
     * '_junctionIdList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getJunctionIdAsReference(
    ) {
        return this._junctionIdList;
    }

    /**
     * Method getJunctionIdCount.
     * 
     * @return the size of this collection
     */
    public int getJunctionIdCount(
    ) {
        return this._junctionIdList.size();
    }

    /**
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'Name'.
     */
    public java.lang.String getName(
    ) {
        return this._name;
    }

    /**
     * Method getRoadElementId.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getRoadElementId(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._roadElementIdList.size()) {
            throw new IndexOutOfBoundsException("getRoadElementId: Index value '" + index + "' not in range [0.." + (this._roadElementIdList.size() - 1) + "]");
        }

        return (java.lang.String) _roadElementIdList.get(index);
    }

    /**
     * Method getRoadElementId.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getRoadElementId(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._roadElementIdList.toArray(array);
    }

    /**
     * Method getRoadElementIdAsReference.Returns a reference to
     * '_roadElementIdList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getRoadElementIdAsReference(
    ) {
        return this._roadElementIdList;
    }

    /**
     * Method getRoadElementIdCount.
     * 
     * @return the size of this collection
     */
    public int getRoadElementIdCount(
    ) {
        return this._roadElementIdList.size();
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
     * Method iterateJunctionId.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iterateJunctionId(
    ) {
        return this._junctionIdList.iterator();
    }

    /**
     * Method iterateRoadElementId.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iterateRoadElementId(
    ) {
        return this._roadElementIdList.iterator();
    }

    /**
     */
    public void removeAllJunctionId(
    ) {
        this._junctionIdList.clear();
    }

    /**
     */
    public void removeAllRoadElementId(
    ) {
        this._roadElementIdList.clear();
    }

    /**
     * Method removeJunctionId.
     * 
     * @param vJunctionId
     * @return true if the object was removed from the collection.
     */
    public boolean removeJunctionId(
            final java.lang.String vJunctionId) {
        boolean removed = _junctionIdList.remove(vJunctionId);
        return removed;
    }

    /**
     * Method removeJunctionIdAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removeJunctionIdAt(
            final int index) {
        java.lang.Object obj = this._junctionIdList.remove(index);
        return (java.lang.String) obj;
    }

    /**
     * Method removeRoadElementId.
     * 
     * @param vRoadElementId
     * @return true if the object was removed from the collection.
     */
    public boolean removeRoadElementId(
            final java.lang.String vRoadElementId) {
        boolean removed = _roadElementIdList.remove(vRoadElementId);
        return removed;
    }

    /**
     * Method removeRoadElementIdAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removeRoadElementIdAt(
            final int index) {
        java.lang.Object obj = this._roadElementIdList.remove(index);
        return (java.lang.String) obj;
    }

    /**
     * Sets the value of field 'comment'.
     * 
     * @param comment the value of field 'comment'.
     */
    public void setComment(
            final java.lang.String comment) {
        this._comment = comment;
    }

    /**
     * 
     * 
     * @param index
     * @param vJunctionId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setJunctionId(
            final int index,
            final java.lang.String vJunctionId)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._junctionIdList.size()) {
            throw new IndexOutOfBoundsException("setJunctionId: Index value '" + index + "' not in range [0.." + (this._junctionIdList.size() - 1) + "]");
        }

        this._junctionIdList.set(index, vJunctionId);
    }

    /**
     * 
     * 
     * @param vJunctionIdArray
     */
    public void setJunctionId(
            final java.lang.String[] vJunctionIdArray) {
        //-- copy array
        _junctionIdList.clear();

        for (int i = 0; i < vJunctionIdArray.length; i++) {
                this._junctionIdList.add(vJunctionIdArray[i]);
        }
    }

    /**
     * Sets the value of '_junctionIdList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vJunctionIdList the Vector to copy.
     */
    public void setJunctionId(
            final java.util.List<java.lang.String> vJunctionIdList) {
        // copy vector
        this._junctionIdList.clear();

        this._junctionIdList.addAll(vJunctionIdList);
    }

    /**
     * Sets the value of '_junctionIdList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param junctionIdList the Vector to set.
     */
    public void setJunctionIdAsReference(
            final java.util.List<java.lang.String> junctionIdList) {
        this._junctionIdList = junctionIdList;
    }

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(
            final java.lang.String name) {
        this._name = name;
    }

    /**
     * 
     * 
     * @param index
     * @param vRoadElementId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setRoadElementId(
            final int index,
            final java.lang.String vRoadElementId)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._roadElementIdList.size()) {
            throw new IndexOutOfBoundsException("setRoadElementId: Index value '" + index + "' not in range [0.." + (this._roadElementIdList.size() - 1) + "]");
        }

        this._roadElementIdList.set(index, vRoadElementId);
    }

    /**
     * 
     * 
     * @param vRoadElementIdArray
     */
    public void setRoadElementId(
            final java.lang.String[] vRoadElementIdArray) {
        //-- copy array
        _roadElementIdList.clear();

        for (int i = 0; i < vRoadElementIdArray.length; i++) {
                this._roadElementIdList.add(vRoadElementIdArray[i]);
        }
    }

    /**
     * Sets the value of '_roadElementIdList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vRoadElementIdList the Vector to copy.
     */
    public void setRoadElementId(
            final java.util.List<java.lang.String> vRoadElementIdList) {
        // copy vector
        this._roadElementIdList.clear();

        this._roadElementIdList.addAll(vRoadElementIdList);
    }

    /**
     * Sets the value of '_roadElementIdList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param roadElementIdList the Vector to set.
     */
    public void setRoadElementIdAsReference(
            final java.util.List<java.lang.String> roadElementIdList) {
        this._roadElementIdList = roadElementIdList;
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
