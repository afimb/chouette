/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * A line is a set of Route _.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class LineTypeType extends amivif.schema.LogicalLocationTypeType 
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
     * Field _number.
     */
    private java.lang.String _number;

    /**
     * Field _publishedName.
     */
    private java.lang.String _publishedName;

    /**
     * Field _transportModeName.
     */
    private amivif.schema.types.TransportModeNameType _transportModeName;

    /**
     * Field _lineEndList.
     */
    private java.util.List<java.lang.String> _lineEndList;

    /**
     * Field _routeIdList.
     */
    private java.util.List<java.lang.String> _routeIdList;

    /**
     * Field _registration.
     */
    private amivif.schema.Registration _registration;

    /**
     * Field _ptNetworkIdShortcut.
     */
    private java.lang.String _ptNetworkIdShortcut;

    /**
     * Field _comment.
     */
    private java.lang.String _comment;


      //----------------/
     //- Constructors -/
    //----------------/

    public LineTypeType() {
        super();
        this._lineEndList = new java.util.ArrayList<java.lang.String>();
        this._routeIdList = new java.util.ArrayList<java.lang.String>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vLineEnd
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addLineEnd(
            final java.lang.String vLineEnd)
    throws java.lang.IndexOutOfBoundsException {
        this._lineEndList.add(vLineEnd);
    }

    /**
     * 
     * 
     * @param index
     * @param vLineEnd
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addLineEnd(
            final int index,
            final java.lang.String vLineEnd)
    throws java.lang.IndexOutOfBoundsException {
        this._lineEndList.add(index, vLineEnd);
    }

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
     * Method enumerateLineEnd.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumerateLineEnd(
    ) {
        return java.util.Collections.enumeration(this._lineEndList);
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
     * Method getLineEnd.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getLineEnd(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._lineEndList.size()) {
            throw new IndexOutOfBoundsException("getLineEnd: Index value '" + index + "' not in range [0.." + (this._lineEndList.size() - 1) + "]");
        }

        return (java.lang.String) _lineEndList.get(index);
    }

    /**
     * Method getLineEnd.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getLineEnd(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._lineEndList.toArray(array);
    }

    /**
     * Method getLineEndAsReference.Returns a reference to
     * '_lineEndList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getLineEndAsReference(
    ) {
        return this._lineEndList;
    }

    /**
     * Method getLineEndCount.
     * 
     * @return the size of this collection
     */
    public int getLineEndCount(
    ) {
        return this._lineEndList.size();
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
     * Returns the value of field 'number'.
     * 
     * @return the value of field 'Number'.
     */
    public java.lang.String getNumber(
    ) {
        return this._number;
    }

    /**
     * Returns the value of field 'ptNetworkIdShortcut'.
     * 
     * @return the value of field 'PtNetworkIdShortcut'.
     */
    public java.lang.String getPtNetworkIdShortcut(
    ) {
        return this._ptNetworkIdShortcut;
    }

    /**
     * Returns the value of field 'publishedName'.
     * 
     * @return the value of field 'PublishedName'.
     */
    public java.lang.String getPublishedName(
    ) {
        return this._publishedName;
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
     * Returns the value of field 'transportModeName'.
     * 
     * @return the value of field 'TransportModeName'.
     */
    public amivif.schema.types.TransportModeNameType getTransportModeName(
    ) {
        return this._transportModeName;
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
     * Method iterateLineEnd.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iterateLineEnd(
    ) {
        return this._lineEndList.iterator();
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
    public void removeAllLineEnd(
    ) {
        this._lineEndList.clear();
    }

    /**
     */
    public void removeAllRouteId(
    ) {
        this._routeIdList.clear();
    }

    /**
     * Method removeLineEnd.
     * 
     * @param vLineEnd
     * @return true if the object was removed from the collection.
     */
    public boolean removeLineEnd(
            final java.lang.String vLineEnd) {
        boolean removed = _lineEndList.remove(vLineEnd);
        return removed;
    }

    /**
     * Method removeLineEndAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removeLineEndAt(
            final int index) {
        java.lang.Object obj = this._lineEndList.remove(index);
        return (java.lang.String) obj;
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
     * 
     * 
     * @param index
     * @param vLineEnd
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setLineEnd(
            final int index,
            final java.lang.String vLineEnd)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._lineEndList.size()) {
            throw new IndexOutOfBoundsException("setLineEnd: Index value '" + index + "' not in range [0.." + (this._lineEndList.size() - 1) + "]");
        }

        this._lineEndList.set(index, vLineEnd);
    }

    /**
     * 
     * 
     * @param vLineEndArray
     */
    public void setLineEnd(
            final java.lang.String[] vLineEndArray) {
        //-- copy array
        _lineEndList.clear();

        for (int i = 0; i < vLineEndArray.length; i++) {
                this._lineEndList.add(vLineEndArray[i]);
        }
    }

    /**
     * Sets the value of '_lineEndList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vLineEndList the Vector to copy.
     */
    public void setLineEnd(
            final java.util.List<java.lang.String> vLineEndList) {
        // copy vector
        this._lineEndList.clear();

        this._lineEndList.addAll(vLineEndList);
    }

    /**
     * Sets the value of '_lineEndList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param lineEndList the Vector to set.
     */
    public void setLineEndAsReference(
            final java.util.List<java.lang.String> lineEndList) {
        this._lineEndList = lineEndList;
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
     * Sets the value of field 'number'.
     * 
     * @param number the value of field 'number'.
     */
    public void setNumber(
            final java.lang.String number) {
        this._number = number;
    }

    /**
     * Sets the value of field 'ptNetworkIdShortcut'.
     * 
     * @param ptNetworkIdShortcut the value of field
     * 'ptNetworkIdShortcut'.
     */
    public void setPtNetworkIdShortcut(
            final java.lang.String ptNetworkIdShortcut) {
        this._ptNetworkIdShortcut = ptNetworkIdShortcut;
    }

    /**
     * Sets the value of field 'publishedName'.
     * 
     * @param publishedName the value of field 'publishedName'.
     */
    public void setPublishedName(
            final java.lang.String publishedName) {
        this._publishedName = publishedName;
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
     * Sets the value of field 'transportModeName'.
     * 
     * @param transportModeName the value of field
     * 'transportModeName'.
     */
    public void setTransportModeName(
            final amivif.schema.types.TransportModeNameType transportModeName) {
        this._transportModeName = transportModeName;
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
