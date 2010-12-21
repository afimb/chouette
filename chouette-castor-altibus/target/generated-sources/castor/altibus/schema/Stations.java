/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package altibus.schema;

/**
 * Class Stations.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Stations extends java.lang.Object 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _stationList.
     */
    private java.util.List<altibus.schema.Station> _stationList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Stations() {
        super();
        this._stationList = new java.util.ArrayList<altibus.schema.Station>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vStation
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStation(
            final altibus.schema.Station vStation)
    throws java.lang.IndexOutOfBoundsException {
        this._stationList.add(vStation);
    }

    /**
     * 
     * 
     * @param index
     * @param vStation
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStation(
            final int index,
            final altibus.schema.Station vStation)
    throws java.lang.IndexOutOfBoundsException {
        this._stationList.add(index, vStation);
    }

    /**
     * Method enumerateStation.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends altibus.schema.Station> enumerateStation(
    ) {
        return java.util.Collections.enumeration(this._stationList);
    }

    /**
     * Method getStation.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the altibus.schema.Station at the given
     * index
     */
    public altibus.schema.Station getStation(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stationList.size()) {
            throw new IndexOutOfBoundsException("getStation: Index value '" + index + "' not in range [0.." + (this._stationList.size() - 1) + "]");
        }

        return (altibus.schema.Station) _stationList.get(index);
    }

    /**
     * Method getStation.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public altibus.schema.Station[] getStation(
    ) {
        altibus.schema.Station[] array = new altibus.schema.Station[0];
        return (altibus.schema.Station[]) this._stationList.toArray(array);
    }

    /**
     * Method getStationAsReference.Returns a reference to
     * '_stationList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<altibus.schema.Station> getStationAsReference(
    ) {
        return this._stationList;
    }

    /**
     * Method getStationCount.
     * 
     * @return the size of this collection
     */
    public int getStationCount(
    ) {
        return this._stationList.size();
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
     * Method iterateStation.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends altibus.schema.Station> iterateStation(
    ) {
        return this._stationList.iterator();
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
    public void removeAllStation(
    ) {
        this._stationList.clear();
    }

    /**
     * Method removeStation.
     * 
     * @param vStation
     * @return true if the object was removed from the collection.
     */
    public boolean removeStation(
            final altibus.schema.Station vStation) {
        boolean removed = _stationList.remove(vStation);
        return removed;
    }

    /**
     * Method removeStationAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public altibus.schema.Station removeStationAt(
            final int index) {
        java.lang.Object obj = this._stationList.remove(index);
        return (altibus.schema.Station) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vStation
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setStation(
            final int index,
            final altibus.schema.Station vStation)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stationList.size()) {
            throw new IndexOutOfBoundsException("setStation: Index value '" + index + "' not in range [0.." + (this._stationList.size() - 1) + "]");
        }

        this._stationList.set(index, vStation);
    }

    /**
     * 
     * 
     * @param vStationArray
     */
    public void setStation(
            final altibus.schema.Station[] vStationArray) {
        //-- copy array
        _stationList.clear();

        for (int i = 0; i < vStationArray.length; i++) {
                this._stationList.add(vStationArray[i]);
        }
    }

    /**
     * Sets the value of '_stationList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vStationList the Vector to copy.
     */
    public void setStation(
            final java.util.List<altibus.schema.Station> vStationList) {
        // copy vector
        this._stationList.clear();

        this._stationList.addAll(vStationList);
    }

    /**
     * Sets the value of '_stationList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param stationList the Vector to set.
     */
    public void setStationAsReference(
            final java.util.List<altibus.schema.Station> stationList) {
        this._stationList = stationList;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled altibus.schema.Stations
     */
    public static altibus.schema.Stations unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (altibus.schema.Stations) org.exolab.castor.xml.Unmarshaller.unmarshal(altibus.schema.Stations.class, reader);
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
