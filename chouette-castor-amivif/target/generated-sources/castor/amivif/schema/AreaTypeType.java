/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * An area made up of a set of Points
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class AreaTypeType extends amivif.schema.LocationTypeType 
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
     * Field _containsList.
     */
    private java.util.List<java.lang.String> _containsList;

    /**
     * Field _boundaryPointList.
     */
    private java.util.List<java.lang.String> _boundaryPointList;

    /**
     * Field _centroidOfArea.
     */
    private java.lang.String _centroidOfArea;


      //----------------/
     //- Constructors -/
    //----------------/

    public AreaTypeType() {
        super();
        this._containsList = new java.util.ArrayList<java.lang.String>();
        this._boundaryPointList = new java.util.ArrayList<java.lang.String>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vBoundaryPoint
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addBoundaryPoint(
            final java.lang.String vBoundaryPoint)
    throws java.lang.IndexOutOfBoundsException {
        this._boundaryPointList.add(vBoundaryPoint);
    }

    /**
     * 
     * 
     * @param index
     * @param vBoundaryPoint
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addBoundaryPoint(
            final int index,
            final java.lang.String vBoundaryPoint)
    throws java.lang.IndexOutOfBoundsException {
        this._boundaryPointList.add(index, vBoundaryPoint);
    }

    /**
     * 
     * 
     * @param vContains
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addContains(
            final java.lang.String vContains)
    throws java.lang.IndexOutOfBoundsException {
        this._containsList.add(vContains);
    }

    /**
     * 
     * 
     * @param index
     * @param vContains
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addContains(
            final int index,
            final java.lang.String vContains)
    throws java.lang.IndexOutOfBoundsException {
        this._containsList.add(index, vContains);
    }

    /**
     * Method enumerateBoundaryPoint.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumerateBoundaryPoint(
    ) {
        return java.util.Collections.enumeration(this._boundaryPointList);
    }

    /**
     * Method enumerateContains.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends java.lang.String> enumerateContains(
    ) {
        return java.util.Collections.enumeration(this._containsList);
    }

    /**
     * Method getBoundaryPoint.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getBoundaryPoint(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._boundaryPointList.size()) {
            throw new IndexOutOfBoundsException("getBoundaryPoint: Index value '" + index + "' not in range [0.." + (this._boundaryPointList.size() - 1) + "]");
        }

        return (java.lang.String) _boundaryPointList.get(index);
    }

    /**
     * Method getBoundaryPoint.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getBoundaryPoint(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._boundaryPointList.toArray(array);
    }

    /**
     * Method getBoundaryPointAsReference.Returns a reference to
     * '_boundaryPointList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getBoundaryPointAsReference(
    ) {
        return this._boundaryPointList;
    }

    /**
     * Method getBoundaryPointCount.
     * 
     * @return the size of this collection
     */
    public int getBoundaryPointCount(
    ) {
        return this._boundaryPointList.size();
    }

    /**
     * Returns the value of field 'centroidOfArea'.
     * 
     * @return the value of field 'CentroidOfArea'.
     */
    public java.lang.String getCentroidOfArea(
    ) {
        return this._centroidOfArea;
    }

    /**
     * Method getContains.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the java.lang.String at the given index
     */
    public java.lang.String getContains(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._containsList.size()) {
            throw new IndexOutOfBoundsException("getContains: Index value '" + index + "' not in range [0.." + (this._containsList.size() - 1) + "]");
        }

        return (java.lang.String) _containsList.get(index);
    }

    /**
     * Method getContains.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public java.lang.String[] getContains(
    ) {
        java.lang.String[] array = new java.lang.String[0];
        return (java.lang.String[]) this._containsList.toArray(array);
    }

    /**
     * Method getContainsAsReference.Returns a reference to
     * '_containsList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<java.lang.String> getContainsAsReference(
    ) {
        return this._containsList;
    }

    /**
     * Method getContainsCount.
     * 
     * @return the size of this collection
     */
    public int getContainsCount(
    ) {
        return this._containsList.size();
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
     * Method iterateBoundaryPoint.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iterateBoundaryPoint(
    ) {
        return this._boundaryPointList.iterator();
    }

    /**
     * Method iterateContains.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends java.lang.String> iterateContains(
    ) {
        return this._containsList.iterator();
    }

    /**
     */
    public void removeAllBoundaryPoint(
    ) {
        this._boundaryPointList.clear();
    }

    /**
     */
    public void removeAllContains(
    ) {
        this._containsList.clear();
    }

    /**
     * Method removeBoundaryPoint.
     * 
     * @param vBoundaryPoint
     * @return true if the object was removed from the collection.
     */
    public boolean removeBoundaryPoint(
            final java.lang.String vBoundaryPoint) {
        boolean removed = _boundaryPointList.remove(vBoundaryPoint);
        return removed;
    }

    /**
     * Method removeBoundaryPointAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removeBoundaryPointAt(
            final int index) {
        java.lang.Object obj = this._boundaryPointList.remove(index);
        return (java.lang.String) obj;
    }

    /**
     * Method removeContains.
     * 
     * @param vContains
     * @return true if the object was removed from the collection.
     */
    public boolean removeContains(
            final java.lang.String vContains) {
        boolean removed = _containsList.remove(vContains);
        return removed;
    }

    /**
     * Method removeContainsAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public java.lang.String removeContainsAt(
            final int index) {
        java.lang.Object obj = this._containsList.remove(index);
        return (java.lang.String) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vBoundaryPoint
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setBoundaryPoint(
            final int index,
            final java.lang.String vBoundaryPoint)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._boundaryPointList.size()) {
            throw new IndexOutOfBoundsException("setBoundaryPoint: Index value '" + index + "' not in range [0.." + (this._boundaryPointList.size() - 1) + "]");
        }

        this._boundaryPointList.set(index, vBoundaryPoint);
    }

    /**
     * 
     * 
     * @param vBoundaryPointArray
     */
    public void setBoundaryPoint(
            final java.lang.String[] vBoundaryPointArray) {
        //-- copy array
        _boundaryPointList.clear();

        for (int i = 0; i < vBoundaryPointArray.length; i++) {
                this._boundaryPointList.add(vBoundaryPointArray[i]);
        }
    }

    /**
     * Sets the value of '_boundaryPointList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vBoundaryPointList the Vector to copy.
     */
    public void setBoundaryPoint(
            final java.util.List<java.lang.String> vBoundaryPointList) {
        // copy vector
        this._boundaryPointList.clear();

        this._boundaryPointList.addAll(vBoundaryPointList);
    }

    /**
     * Sets the value of '_boundaryPointList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param boundaryPointList the Vector to set.
     */
    public void setBoundaryPointAsReference(
            final java.util.List<java.lang.String> boundaryPointList) {
        this._boundaryPointList = boundaryPointList;
    }

    /**
     * Sets the value of field 'centroidOfArea'.
     * 
     * @param centroidOfArea the value of field 'centroidOfArea'.
     */
    public void setCentroidOfArea(
            final java.lang.String centroidOfArea) {
        this._centroidOfArea = centroidOfArea;
    }

    /**
     * 
     * 
     * @param index
     * @param vContains
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setContains(
            final int index,
            final java.lang.String vContains)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._containsList.size()) {
            throw new IndexOutOfBoundsException("setContains: Index value '" + index + "' not in range [0.." + (this._containsList.size() - 1) + "]");
        }

        this._containsList.set(index, vContains);
    }

    /**
     * 
     * 
     * @param vContainsArray
     */
    public void setContains(
            final java.lang.String[] vContainsArray) {
        //-- copy array
        _containsList.clear();

        for (int i = 0; i < vContainsArray.length; i++) {
                this._containsList.add(vContainsArray[i]);
        }
    }

    /**
     * Sets the value of '_containsList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vContainsList the Vector to copy.
     */
    public void setContains(
            final java.util.List<java.lang.String> vContainsList) {
        // copy vector
        this._containsList.clear();

        this._containsList.addAll(vContainsList);
    }

    /**
     * Sets the value of '_containsList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param containsList the Vector to set.
     */
    public void setContainsAsReference(
            final java.util.List<java.lang.String> containsList) {
        this._containsList = containsList;
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
