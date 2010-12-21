/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Class ChouetteArea.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class ChouetteArea extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * La liste des zones dons le type figure sur l'extension
     */
    private java.util.List<chouette.schema.StopArea> _stopAreaList;

    /**
     * Liste de position g�ographiques
     */
    private java.util.List<chouette.schema.AreaCentroid> _areaCentroidList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ChouetteArea() {
        super();
        this._stopAreaList = new java.util.ArrayList<chouette.schema.StopArea>();
        this._areaCentroidList = new java.util.ArrayList<chouette.schema.AreaCentroid>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vAreaCentroid
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAreaCentroid(
            final chouette.schema.AreaCentroid vAreaCentroid)
    throws java.lang.IndexOutOfBoundsException {
        this._areaCentroidList.add(vAreaCentroid);
    }

    /**
     * 
     * 
     * @param index
     * @param vAreaCentroid
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addAreaCentroid(
            final int index,
            final chouette.schema.AreaCentroid vAreaCentroid)
    throws java.lang.IndexOutOfBoundsException {
        this._areaCentroidList.add(index, vAreaCentroid);
    }

    /**
     * 
     * 
     * @param vStopArea
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStopArea(
            final chouette.schema.StopArea vStopArea)
    throws java.lang.IndexOutOfBoundsException {
        this._stopAreaList.add(vStopArea);
    }

    /**
     * 
     * 
     * @param index
     * @param vStopArea
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStopArea(
            final int index,
            final chouette.schema.StopArea vStopArea)
    throws java.lang.IndexOutOfBoundsException {
        this._stopAreaList.add(index, vStopArea);
    }

    /**
     * Method enumerateAreaCentroid.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.AreaCentroid> enumerateAreaCentroid(
    ) {
        return java.util.Collections.enumeration(this._areaCentroidList);
    }

    /**
     * Method enumerateStopArea.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.StopArea> enumerateStopArea(
    ) {
        return java.util.Collections.enumeration(this._stopAreaList);
    }

    /**
     * Method getAreaCentroid.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.AreaCentroid at the
     * given index
     */
    public chouette.schema.AreaCentroid getAreaCentroid(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._areaCentroidList.size()) {
            throw new IndexOutOfBoundsException("getAreaCentroid: Index value '" + index + "' not in range [0.." + (this._areaCentroidList.size() - 1) + "]");
        }

        return (chouette.schema.AreaCentroid) _areaCentroidList.get(index);
    }

    /**
     * Method getAreaCentroid.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.AreaCentroid[] getAreaCentroid(
    ) {
        chouette.schema.AreaCentroid[] array = new chouette.schema.AreaCentroid[0];
        return (chouette.schema.AreaCentroid[]) this._areaCentroidList.toArray(array);
    }

    /**
     * Method getAreaCentroidAsReference.Returns a reference to
     * '_areaCentroidList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.AreaCentroid> getAreaCentroidAsReference(
    ) {
        return this._areaCentroidList;
    }

    /**
     * Method getAreaCentroidCount.
     * 
     * @return the size of this collection
     */
    public int getAreaCentroidCount(
    ) {
        return this._areaCentroidList.size();
    }

    /**
     * Method getStopArea.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.StopArea at the
     * given index
     */
    public chouette.schema.StopArea getStopArea(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stopAreaList.size()) {
            throw new IndexOutOfBoundsException("getStopArea: Index value '" + index + "' not in range [0.." + (this._stopAreaList.size() - 1) + "]");
        }

        return (chouette.schema.StopArea) _stopAreaList.get(index);
    }

    /**
     * Method getStopArea.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.StopArea[] getStopArea(
    ) {
        chouette.schema.StopArea[] array = new chouette.schema.StopArea[0];
        return (chouette.schema.StopArea[]) this._stopAreaList.toArray(array);
    }

    /**
     * Method getStopAreaAsReference.Returns a reference to
     * '_stopAreaList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.StopArea> getStopAreaAsReference(
    ) {
        return this._stopAreaList;
    }

    /**
     * Method getStopAreaCount.
     * 
     * @return the size of this collection
     */
    public int getStopAreaCount(
    ) {
        return this._stopAreaList.size();
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
     * Method iterateAreaCentroid.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.AreaCentroid> iterateAreaCentroid(
    ) {
        return this._areaCentroidList.iterator();
    }

    /**
     * Method iterateStopArea.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.StopArea> iterateStopArea(
    ) {
        return this._stopAreaList.iterator();
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
    public void removeAllAreaCentroid(
    ) {
        this._areaCentroidList.clear();
    }

    /**
     */
    public void removeAllStopArea(
    ) {
        this._stopAreaList.clear();
    }

    /**
     * Method removeAreaCentroid.
     * 
     * @param vAreaCentroid
     * @return true if the object was removed from the collection.
     */
    public boolean removeAreaCentroid(
            final chouette.schema.AreaCentroid vAreaCentroid) {
        boolean removed = _areaCentroidList.remove(vAreaCentroid);
        return removed;
    }

    /**
     * Method removeAreaCentroidAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.AreaCentroid removeAreaCentroidAt(
            final int index) {
        java.lang.Object obj = this._areaCentroidList.remove(index);
        return (chouette.schema.AreaCentroid) obj;
    }

    /**
     * Method removeStopArea.
     * 
     * @param vStopArea
     * @return true if the object was removed from the collection.
     */
    public boolean removeStopArea(
            final chouette.schema.StopArea vStopArea) {
        boolean removed = _stopAreaList.remove(vStopArea);
        return removed;
    }

    /**
     * Method removeStopAreaAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.StopArea removeStopAreaAt(
            final int index) {
        java.lang.Object obj = this._stopAreaList.remove(index);
        return (chouette.schema.StopArea) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vAreaCentroid
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setAreaCentroid(
            final int index,
            final chouette.schema.AreaCentroid vAreaCentroid)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._areaCentroidList.size()) {
            throw new IndexOutOfBoundsException("setAreaCentroid: Index value '" + index + "' not in range [0.." + (this._areaCentroidList.size() - 1) + "]");
        }

        this._areaCentroidList.set(index, vAreaCentroid);
    }

    /**
     * 
     * 
     * @param vAreaCentroidArray
     */
    public void setAreaCentroid(
            final chouette.schema.AreaCentroid[] vAreaCentroidArray) {
        //-- copy array
        _areaCentroidList.clear();

        for (int i = 0; i < vAreaCentroidArray.length; i++) {
                this._areaCentroidList.add(vAreaCentroidArray[i]);
        }
    }

    /**
     * Sets the value of '_areaCentroidList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vAreaCentroidList the Vector to copy.
     */
    public void setAreaCentroid(
            final java.util.List<chouette.schema.AreaCentroid> vAreaCentroidList) {
        // copy vector
        this._areaCentroidList.clear();

        this._areaCentroidList.addAll(vAreaCentroidList);
    }

    /**
     * Sets the value of '_areaCentroidList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param areaCentroidList the Vector to set.
     */
    public void setAreaCentroidAsReference(
            final java.util.List<chouette.schema.AreaCentroid> areaCentroidList) {
        this._areaCentroidList = areaCentroidList;
    }

    /**
     * 
     * 
     * @param index
     * @param vStopArea
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setStopArea(
            final int index,
            final chouette.schema.StopArea vStopArea)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stopAreaList.size()) {
            throw new IndexOutOfBoundsException("setStopArea: Index value '" + index + "' not in range [0.." + (this._stopAreaList.size() - 1) + "]");
        }

        this._stopAreaList.set(index, vStopArea);
    }

    /**
     * 
     * 
     * @param vStopAreaArray
     */
    public void setStopArea(
            final chouette.schema.StopArea[] vStopAreaArray) {
        //-- copy array
        _stopAreaList.clear();

        for (int i = 0; i < vStopAreaArray.length; i++) {
                this._stopAreaList.add(vStopAreaArray[i]);
        }
    }

    /**
     * Sets the value of '_stopAreaList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vStopAreaList the Vector to copy.
     */
    public void setStopArea(
            final java.util.List<chouette.schema.StopArea> vStopAreaList) {
        // copy vector
        this._stopAreaList.clear();

        this._stopAreaList.addAll(vStopAreaList);
    }

    /**
     * Sets the value of '_stopAreaList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param stopAreaList the Vector to set.
     */
    public void setStopAreaAsReference(
            final java.util.List<chouette.schema.StopArea> stopAreaList) {
        this._stopAreaList = stopAreaList;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled chouette.schema.ChouetteArea
     */
    public static chouette.schema.ChouetteArea unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (chouette.schema.ChouetteArea) org.exolab.castor.xml.Unmarshaller.unmarshal(chouette.schema.ChouetteArea.class, reader);
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
