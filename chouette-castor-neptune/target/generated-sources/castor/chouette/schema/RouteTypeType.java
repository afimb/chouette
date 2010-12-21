/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * An ordered list of Stop Points on wich Journey 
 *  pattern are applied
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class RouteTypeType extends chouette.schema.TridentObjectTypeType 
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
     * Field _publishedName.
     */
    private java.lang.String _publishedName;

    /**
     * Field _number.
     */
    private java.lang.String _number;

    /**
     * Field _direction.
     */
    private chouette.schema.types.PTDirectionType _direction;

    /**
     * Field _ptLinkIdList.
     */
    private java.util.List<java.lang.String> _ptLinkIdList;

    /**
     * Field _journeyPatternIdList.
     */
    private java.util.List<java.lang.String> _journeyPatternIdList;

    /**
     * Field _wayBackRouteId.
     */
    private java.lang.String _wayBackRouteId;

    /**
     * Field _comment.
     */
    private java.lang.String _comment;


      //----------------/
     //- Constructors -/
    //----------------/

    public RouteTypeType() {
        super();
        this._ptLinkIdList = new java.util.ArrayList<java.lang.String>();
        this._journeyPatternIdList = new java.util.ArrayList<java.lang.String>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vJourneyPatternId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addJourneyPatternId(
            final java.lang.String vJourneyPatternId)
    throws java.lang.IndexOutOfBoundsException {
        this._journeyPatternIdList.add(vJourneyPatternId);
    }

    /**
     * 
     * 
     * @param index
     * @param vJourneyPatternId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addJourneyPatternId(
            final int index,
            final java.lang.String vJourneyPatternId)
    throws java.lang.IndexOutOfBoundsException {
        this._journeyPatternIdList.add(index, vJourneyPatternId);
    }

    /**
     * 
     * 
     * @param vPtLinkId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPtLinkId(
            final java.lang.String vPtLinkId)
    throws java.lang.IndexOutOfBoundsException {
        this._ptLinkIdList.add(vPtLinkId);
    }

    /**
     * 
     * 
     * @param index
     * @param vPtLinkId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addPtLinkId(
            final int index,
            final java.lang.String vPtLinkId)
    throws java.lang.IndexOutOfBoundsException {
        this._ptLinkIdList.add(index, vPtLinkId);
    }

    /**
     * Method enumerateJourneyPatternId.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumerateJourneyPatternId(
    ) {
        return java.util.Collections.enumeration(this._journeyPatternIdList);
    }

    /**
     * Method enumeratePtLinkId.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumeratePtLinkId(
    ) {
        return java.util.Collections.enumeration(this._ptLinkIdList);
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
     * Returns the value of field 'direction'.
     * 
     * @return the value of field 'Direction'.
     */
    public chouette.schema.types.PTDirectionType getDirection(
    ) {
        return this._direction;
    }

    /**
     * Method getJourneyPatternId.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getJourneyPatternId(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._journeyPatternIdList.size()) {
            throw new IndexOutOfBoundsException("getJourneyPatternId: Index value '" + index + "' not in range [0.." + (this._journeyPatternIdList.size() - 1) + "]");
        }

        return (java.lang.String) _journeyPatternIdList.get(index);
    }

    /**
     * Method getJourneyPatternId.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getJourneyPatternId(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._journeyPatternIdList.toArray(array);
    }

    /**
     * Method getJourneyPatternIdAsReference.Returns a reference to
     * '_journeyPatternIdList'. No type checking is performed on
     * any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getJourneyPatternIdAsReference(
    ) {
        return this._journeyPatternIdList;
    }

    /**
     * Method getJourneyPatternIdCount.
     * 
     * @return the size of this collection
     */
    public int getJourneyPatternIdCount(
    ) {
        return this._journeyPatternIdList.size();
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
     * Method getPtLinkId.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getPtLinkId(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ptLinkIdList.size()) {
            throw new IndexOutOfBoundsException("getPtLinkId: Index value '" + index + "' not in range [0.." + (this._ptLinkIdList.size() - 1) + "]");
        }

        return (java.lang.String) _ptLinkIdList.get(index);
    }

    /**
     * Method getPtLinkId.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getPtLinkId(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._ptLinkIdList.toArray(array);
    }

    /**
     * Method getPtLinkIdAsReference.Returns a reference to
     * '_ptLinkIdList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getPtLinkIdAsReference(
    ) {
        return this._ptLinkIdList;
    }

    /**
     * Method getPtLinkIdCount.
     * 
     * @return the size of this collection
     */
    public int getPtLinkIdCount(
    ) {
        return this._ptLinkIdList.size();
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
     * Returns the value of field 'wayBackRouteId'.
     * 
     * @return the value of field 'WayBackRouteId'.
     */
    public java.lang.String getWayBackRouteId(
    ) {
        return this._wayBackRouteId;
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
     * Method iterateJourneyPatternId.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iterateJourneyPatternId(
    ) {
        return this._journeyPatternIdList.iterator();
    }

    /**
     * Method iteratePtLinkId.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iteratePtLinkId(
    ) {
        return this._ptLinkIdList.iterator();
    }

    /**
     */
    public void removeAllJourneyPatternId(
    ) {
        this._journeyPatternIdList.clear();
    }

    /**
     */
    public void removeAllPtLinkId(
    ) {
        this._ptLinkIdList.clear();
    }

    /**
     * Method removeJourneyPatternId.
     * 
     * @param vJourneyPatternId
     * @return true if the object was removed from the collection.
     */
    public boolean removeJourneyPatternId(
            final java.lang.String vJourneyPatternId) {
        boolean removed = _journeyPatternIdList.remove(vJourneyPatternId);
        return removed;
    }

    /**
     * Method removeJourneyPatternIdAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removeJourneyPatternIdAt(
            final int index) {
        java.lang.Object obj = this._journeyPatternIdList.remove(index);
        return (java.lang.String) obj;
    }

    /**
     * Method removePtLinkId.
     * 
     * @param vPtLinkId
     * @return true if the object was removed from the collection.
     */
    public boolean removePtLinkId(
            final java.lang.String vPtLinkId) {
        boolean removed = _ptLinkIdList.remove(vPtLinkId);
        return removed;
    }

    /**
     * Method removePtLinkIdAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removePtLinkIdAt(
            final int index) {
        java.lang.Object obj = this._ptLinkIdList.remove(index);
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
     * Sets the value of field 'direction'.
     * 
     * @param direction the value of field 'direction'.
     */
    public void setDirection(
            final chouette.schema.types.PTDirectionType direction) {
        this._direction = direction;
    }

    /**
     * 
     * 
     * @param index
     * @param vJourneyPatternId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setJourneyPatternId(
            final int index,
            final java.lang.String vJourneyPatternId)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._journeyPatternIdList.size()) {
            throw new IndexOutOfBoundsException("setJourneyPatternId: Index value '" + index + "' not in range [0.." + (this._journeyPatternIdList.size() - 1) + "]");
        }

        this._journeyPatternIdList.set(index, vJourneyPatternId);
    }

    /**
     * 
     * 
     * @param vJourneyPatternIdArray
     */
    public void setJourneyPatternId(
            final java.lang.String[] vJourneyPatternIdArray) {
        //-- copy array
        _journeyPatternIdList.clear();

        for (int i = 0; i < vJourneyPatternIdArray.length; i++) {
                this._journeyPatternIdList.add(vJourneyPatternIdArray[i]);
        }
    }

    /**
     * Sets the value of '_journeyPatternIdList' by copying the
     * given Vector. All elements will be checked for type safety.
     * 
     * @param vJourneyPatternIdList the Vector to copy.
     */
    public void setJourneyPatternId(
            final java.util.List<java.lang.String> vJourneyPatternIdList) {
        // copy vector
        this._journeyPatternIdList.clear();

        this._journeyPatternIdList.addAll(vJourneyPatternIdList);
    }

    /**
     * Sets the value of '_journeyPatternIdList' by setting it to
     * the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param journeyPatternIdList the Vector to set.
     */
    public void setJourneyPatternIdAsReference(
            final java.util.List<java.lang.String> journeyPatternIdList) {
        this._journeyPatternIdList = journeyPatternIdList;
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
     * 
     * 
     * @param index
     * @param vPtLinkId
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setPtLinkId(
            final int index,
            final java.lang.String vPtLinkId)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ptLinkIdList.size()) {
            throw new IndexOutOfBoundsException("setPtLinkId: Index value '" + index + "' not in range [0.." + (this._ptLinkIdList.size() - 1) + "]");
        }

        this._ptLinkIdList.set(index, vPtLinkId);
    }

    /**
     * 
     * 
     * @param vPtLinkIdArray
     */
    public void setPtLinkId(
            final java.lang.String[] vPtLinkIdArray) {
        //-- copy array
        _ptLinkIdList.clear();

        for (int i = 0; i < vPtLinkIdArray.length; i++) {
                this._ptLinkIdList.add(vPtLinkIdArray[i]);
        }
    }

    /**
     * Sets the value of '_ptLinkIdList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vPtLinkIdList the Vector to copy.
     */
    public void setPtLinkId(
            final java.util.List<java.lang.String> vPtLinkIdList) {
        // copy vector
        this._ptLinkIdList.clear();

        this._ptLinkIdList.addAll(vPtLinkIdList);
    }

    /**
     * Sets the value of '_ptLinkIdList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param ptLinkIdList the Vector to set.
     */
    public void setPtLinkIdAsReference(
            final java.util.List<java.lang.String> ptLinkIdList) {
        this._ptLinkIdList = ptLinkIdList;
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
     * Sets the value of field 'wayBackRouteId'.
     * 
     * @param wayBackRouteId the value of field 'wayBackRouteId'.
     */
    public void setWayBackRouteId(
            final java.lang.String wayBackRouteId) {
        this._wayBackRouteId = wayBackRouteId;
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
