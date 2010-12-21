/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * A Subline is a subset of Route from a line ...
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class SubLineTypeType extends amivif.schema.TridentObjectTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _sublineName.
     */
    private java.lang.String _sublineName;

    /**
     * Field _lineName.
     */
    private java.lang.String _lineName;

    /**
     * Field _registration.
     */
    private amivif.schema.Registration _registration;

    /**
     * Field _routeIdList.
     */
    private java.util.List<java.lang.String> _routeIdList;

    /**
     * Field _lineId.
     */
    private java.lang.String _lineId;

    /**
     * Field _comment.
     */
    private java.lang.String _comment;


      //----------------/
     //- Constructors -/
    //----------------/

    public SubLineTypeType() {
        super();
        this._routeIdList = new java.util.ArrayList<java.lang.String>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vRouteId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addRouteId(
            final java.lang.String vRouteId)
    throws java.lang.IndexOutOfBoundsException {
        this._routeIdList.add(vRouteId);
    }

    /**
     * 
     * 
     * @param index
     * @param vRouteId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addRouteId(
            final int index,
            final java.lang.String vRouteId)
    throws java.lang.IndexOutOfBoundsException {
        this._routeIdList.add(index, vRouteId);
    }

    /**
     * Method enumerateRouteId.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumerateRouteId(
    ) {
        return java.util.Collections.enumeration(this._routeIdList);
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
     * Returns the value of field 'lineId'.
     * 
     * @return the value of field 'LineId'.
     */
    public java.lang.String getLineId(
    ) {
        return this._lineId;
    }

    /**
     * Returns the value of field 'lineName'.
     * 
     * @return the value of field 'LineName'.
     */
    public java.lang.String getLineName(
    ) {
        return this._lineName;
    }

    /**
     * Returns the value of field 'registration'.
     * 
     * @return the value of field 'Registration'.
     */
    public amivif.schema.Registration getRegistration(
    ) {
        return this._registration;
    }

    /**
     * Method getRouteId.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getRouteId(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._routeIdList.size()) {
            throw new IndexOutOfBoundsException("getRouteId: Index value '" + index + "' not in range [0.." + (this._routeIdList.size() - 1) + "]");
        }

        return (java.lang.String) _routeIdList.get(index);
    }

    /**
     * Method getRouteId.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getRouteId(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._routeIdList.toArray(array);
    }

    /**
     * Method getRouteIdAsReference.Returns a reference to
     * '_routeIdList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getRouteIdAsReference(
    ) {
        return this._routeIdList;
    }

    /**
     * Method getRouteIdCount.
     * 
     * @return the size of this collection
     */
    public int getRouteIdCount(
    ) {
        return this._routeIdList.size();
    }

    /**
     * Returns the value of field 'sublineName'.
     * 
     * @return the value of field 'SublineName'.
     */
    public java.lang.String getSublineName(
    ) {
        return this._sublineName;
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
     * Method iterateRouteId.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iterateRouteId(
    ) {
        return this._routeIdList.iterator();
    }

    /**
     */
    public void removeAllRouteId(
    ) {
        this._routeIdList.clear();
    }

    /**
     * Method removeRouteId.
     * 
     * @param vRouteId
     * @return true if the object was removed from the collection.
     */
    public boolean removeRouteId(
            final java.lang.String vRouteId) {
        boolean removed = _routeIdList.remove(vRouteId);
        return removed;
    }

    /**
     * Method removeRouteIdAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removeRouteIdAt(
            final int index) {
        java.lang.Object obj = this._routeIdList.remove(index);
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
     * Sets the value of field 'lineId'.
     * 
     * @param lineId the value of field 'lineId'.
     */
    public void setLineId(
            final java.lang.String lineId) {
        this._lineId = lineId;
    }

    /**
     * Sets the value of field 'lineName'.
     * 
     * @param lineName the value of field 'lineName'.
     */
    public void setLineName(
            final java.lang.String lineName) {
        this._lineName = lineName;
    }

    /**
     * Sets the value of field 'registration'.
     * 
     * @param registration the value of field 'registration'.
     */
    public void setRegistration(
            final amivif.schema.Registration registration) {
        this._registration = registration;
    }

    /**
     * 
     * 
     * @param index
     * @param vRouteId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setRouteId(
            final int index,
            final java.lang.String vRouteId)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._routeIdList.size()) {
            throw new IndexOutOfBoundsException("setRouteId: Index value '" + index + "' not in range [0.." + (this._routeIdList.size() - 1) + "]");
        }

        this._routeIdList.set(index, vRouteId);
    }

    /**
     * 
     * 
     * @param vRouteIdArray
     */
    public void setRouteId(
            final java.lang.String[] vRouteIdArray) {
        //-- copy array
        _routeIdList.clear();

        for (int i = 0; i < vRouteIdArray.length; i++) {
                this._routeIdList.add(vRouteIdArray[i]);
        }
    }

    /**
     * Sets the value of '_routeIdList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vRouteIdList the Vector to copy.
     */
    public void setRouteId(
            final java.util.List<java.lang.String> vRouteIdList) {
        // copy vector
        this._routeIdList.clear();

        this._routeIdList.addAll(vRouteIdList);
    }

    /**
     * Sets the value of '_routeIdList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param routeIdList the Vector to set.
     */
    public void setRouteIdAsReference(
            final java.util.List<java.lang.String> routeIdList) {
        this._routeIdList = routeIdList;
    }

    /**
     * Sets the value of field 'sublineName'.
     * 
     * @param sublineName the value of field 'sublineName'.
     */
    public void setSublineName(
            final java.lang.String sublineName) {
        this._sublineName = sublineName;
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
