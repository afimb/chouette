/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * Basically, JourneyPattern are some ordered list of 
 * Stop Points, but these StopPoints have to be linked 
 * together (by couples)
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class JourneyPatternTypeType extends amivif.schema.TridentObjectTypeType 
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
     * Field _routeId.
     */
    private java.lang.String _routeId;

    /**
     * Field _origin.
     */
    private java.lang.String _origin;

    /**
     * Field _destination.
     */
    private java.lang.String _destination;

    /**
     * Field _stopPointListList.
     */
    private java.util.List<java.lang.String> _stopPointListList;

    /**
     * Field _registration.
     */
    private amivif.schema.Registration _registration;

    /**
     * Field _lineIdShortcut.
     */
    private java.lang.String _lineIdShortcut;

    /**
     * Field _comment.
     */
    private java.lang.String _comment;


      //----------------/
     //- Constructors -/
    //----------------/

    public JourneyPatternTypeType() {
        super();
        this._stopPointListList = new java.util.ArrayList<java.lang.String>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStopPointList
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStopPointList(
            final java.lang.String vStopPointList)
    throws java.lang.IndexOutOfBoundsException {
        this._stopPointListList.add(vStopPointList);
    }

    /**
     * 
     * 
     * @param index
     * @param vStopPointList
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStopPointList(
            final int index,
            final java.lang.String vStopPointList)
    throws java.lang.IndexOutOfBoundsException {
        this._stopPointListList.add(index, vStopPointList);
    }

    /**
     * Method enumerateStopPointList.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumerateStopPointList(
    ) {
        return java.util.Collections.enumeration(this._stopPointListList);
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
     * Returns the value of field 'destination'.
     * 
     * @return the value of field 'Destination'.
     */
    public java.lang.String getDestination(
    ) {
        return this._destination;
    }

    /**
     * Returns the value of field 'lineIdShortcut'.
     * 
     * @return the value of field 'LineIdShortcut'.
     */
    public java.lang.String getLineIdShortcut(
    ) {
        return this._lineIdShortcut;
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
     * Returns the value of field 'origin'.
     * 
     * @return the value of field 'Origin'.
     */
    public java.lang.String getOrigin(
    ) {
        return this._origin;
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
     * Returns the value of field 'routeId'.
     * 
     * @return the value of field 'RouteId'.
     */
    public java.lang.String getRouteId(
    ) {
        return this._routeId;
    }

    /**
     * Method getStopPointList.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getStopPointList(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stopPointListList.size()) {
            throw new IndexOutOfBoundsException("getStopPointList: Index value '" + index + "' not in range [0.." + (this._stopPointListList.size() - 1) + "]");
        }

        return (java.lang.String) _stopPointListList.get(index);
    }

    /**
     * Method getStopPointList.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getStopPointList(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._stopPointListList.toArray(array);
    }

    /**
     * Method getStopPointListAsReference.Returns a reference to
     * '_stopPointListList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getStopPointListAsReference(
    ) {
        return this._stopPointListList;
    }

    /**
     * Method getStopPointListCount.
     * 
     * @return the size of this collection
     */
    public int getStopPointListCount(
    ) {
        return this._stopPointListList.size();
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
     * Method iterateStopPointList.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iterateStopPointList(
    ) {
        return this._stopPointListList.iterator();
    }

    /**
     */
    public void removeAllStopPointList(
    ) {
        this._stopPointListList.clear();
    }

    /**
     * Method removeStopPointList.
     * 
     * @param vStopPointList
     * @return true if the object was removed from the collection.
     */
    public boolean removeStopPointList(
            final java.lang.String vStopPointList) {
        boolean removed = _stopPointListList.remove(vStopPointList);
        return removed;
    }

    /**
     * Method removeStopPointListAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removeStopPointListAt(
            final int index) {
        java.lang.Object obj = this._stopPointListList.remove(index);
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
     * Sets the value of field 'destination'.
     * 
     * @param destination the value of field 'destination'.
     */
    public void setDestination(
            final java.lang.String destination) {
        this._destination = destination;
    }

    /**
     * Sets the value of field 'lineIdShortcut'.
     * 
     * @param lineIdShortcut the value of field 'lineIdShortcut'.
     */
    public void setLineIdShortcut(
            final java.lang.String lineIdShortcut) {
        this._lineIdShortcut = lineIdShortcut;
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
     * Sets the value of field 'origin'.
     * 
     * @param origin the value of field 'origin'.
     */
    public void setOrigin(
            final java.lang.String origin) {
        this._origin = origin;
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
     * Sets the value of field 'routeId'.
     * 
     * @param routeId the value of field 'routeId'.
     */
    public void setRouteId(
            final java.lang.String routeId) {
        this._routeId = routeId;
    }

    /**
     * 
     * 
     * @param index
     * @param vStopPointList
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setStopPointList(
            final int index,
            final java.lang.String vStopPointList)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stopPointListList.size()) {
            throw new IndexOutOfBoundsException("setStopPointList: Index value '" + index + "' not in range [0.." + (this._stopPointListList.size() - 1) + "]");
        }

        this._stopPointListList.set(index, vStopPointList);
    }

    /**
     * 
     * 
     * @param vStopPointListArray
     */
    public void setStopPointList(
            final java.lang.String[] vStopPointListArray) {
        //-- copy array
        _stopPointListList.clear();

        for (int i = 0; i < vStopPointListArray.length; i++) {
                this._stopPointListList.add(vStopPointListArray[i]);
        }
    }

    /**
     * Sets the value of '_stopPointListList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vStopPointListList the Vector to copy.
     */
    public void setStopPointList(
            final java.util.List<java.lang.String> vStopPointListList) {
        // copy vector
        this._stopPointListList.clear();

        this._stopPointListList.addAll(vStopPointListList);
    }

    /**
     * Sets the value of '_stopPointListList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param stopPointListList the Vector to set.
     */
    public void setStopPointListAsReference(
            final java.util.List<java.lang.String> stopPointListList) {
        this._stopPointListList = stopPointListList;
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
