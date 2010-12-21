/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package altibus.schema;

/**
 * Class Altibus.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Altibus extends java.lang.Object 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _schemaLocation.
     */
    private java.lang.String _schemaLocation;

    /**
     * Field _reseau.
     */
    private altibus.schema.Reseau _reseau;

    /**
     * Field _exploitant.
     */
    private altibus.schema.Exploitant _exploitant;

    /**
     * Field _stationsList.
     */
    private java.util.List<altibus.schema.Stations> _stationsList;

    /**
     * Field _lignesList.
     */
    private java.util.List<altibus.schema.Lignes> _lignesList;

    /**
     * Field _calendriersList.
     */
    private java.util.List<altibus.schema.Calendriers> _calendriersList;

    /**
     * Field _intervallesList.
     */
    private java.util.List<altibus.schema.Intervalles> _intervallesList;

    /**
     * Field _busList.
     */
    private java.util.List<altibus.schema.Bus> _busList;

    /**
     * Field _arretsList.
     */
    private java.util.List<altibus.schema.Arrets> _arretsList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Altibus() {
        super();
        this._stationsList = new java.util.ArrayList<altibus.schema.Stations>();
        this._lignesList = new java.util.ArrayList<altibus.schema.Lignes>();
        this._calendriersList = new java.util.ArrayList<altibus.schema.Calendriers>();
        this._intervallesList = new java.util.ArrayList<altibus.schema.Intervalles>();
        this._busList = new java.util.ArrayList<altibus.schema.Bus>();
        this._arretsList = new java.util.ArrayList<altibus.schema.Arrets>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vArrets
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addArrets(
            final altibus.schema.Arrets vArrets)
    throws java.lang.IndexOutOfBoundsException {
        this._arretsList.add(vArrets);
    }

    /**
     * 
     * 
     * @param index
     * @param vArrets
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addArrets(
            final int index,
            final altibus.schema.Arrets vArrets)
    throws java.lang.IndexOutOfBoundsException {
        this._arretsList.add(index, vArrets);
    }

    /**
     * 
     * 
     * @param vBus
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addBus(
            final altibus.schema.Bus vBus)
    throws java.lang.IndexOutOfBoundsException {
        this._busList.add(vBus);
    }

    /**
     * 
     * 
     * @param index
     * @param vBus
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addBus(
            final int index,
            final altibus.schema.Bus vBus)
    throws java.lang.IndexOutOfBoundsException {
        this._busList.add(index, vBus);
    }

    /**
     * 
     * 
     * @param vCalendriers
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCalendriers(
            final altibus.schema.Calendriers vCalendriers)
    throws java.lang.IndexOutOfBoundsException {
        this._calendriersList.add(vCalendriers);
    }

    /**
     * 
     * 
     * @param index
     * @param vCalendriers
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addCalendriers(
            final int index,
            final altibus.schema.Calendriers vCalendriers)
    throws java.lang.IndexOutOfBoundsException {
        this._calendriersList.add(index, vCalendriers);
    }

    /**
     * 
     * 
     * @param vIntervalles
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addIntervalles(
            final altibus.schema.Intervalles vIntervalles)
    throws java.lang.IndexOutOfBoundsException {
        this._intervallesList.add(vIntervalles);
    }

    /**
     * 
     * 
     * @param index
     * @param vIntervalles
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addIntervalles(
            final int index,
            final altibus.schema.Intervalles vIntervalles)
    throws java.lang.IndexOutOfBoundsException {
        this._intervallesList.add(index, vIntervalles);
    }

    /**
     * 
     * 
     * @param vLignes
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addLignes(
            final altibus.schema.Lignes vLignes)
    throws java.lang.IndexOutOfBoundsException {
        this._lignesList.add(vLignes);
    }

    /**
     * 
     * 
     * @param index
     * @param vLignes
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addLignes(
            final int index,
            final altibus.schema.Lignes vLignes)
    throws java.lang.IndexOutOfBoundsException {
        this._lignesList.add(index, vLignes);
    }

    /**
     * 
     * 
     * @param vStations
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStations(
            final altibus.schema.Stations vStations)
    throws java.lang.IndexOutOfBoundsException {
        this._stationsList.add(vStations);
    }

    /**
     * 
     * 
     * @param index
     * @param vStations
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addStations(
            final int index,
            final altibus.schema.Stations vStations)
    throws java.lang.IndexOutOfBoundsException {
        this._stationsList.add(index, vStations);
    }

    /**
     * Method enumerateArrets.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends altibus.schema.Arrets> enumerateArrets(
    ) {
        return java.util.Collections.enumeration(this._arretsList);
    }

    /**
     * Method enumerateBus.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends altibus.schema.Bus> enumerateBus(
    ) {
        return java.util.Collections.enumeration(this._busList);
    }

    /**
     * Method enumerateCalendriers.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends altibus.schema.Calendriers> enumerateCalendriers(
    ) {
        return java.util.Collections.enumeration(this._calendriersList);
    }

    /**
     * Method enumerateIntervalles.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends altibus.schema.Intervalles> enumerateIntervalles(
    ) {
        return java.util.Collections.enumeration(this._intervallesList);
    }

    /**
     * Method enumerateLignes.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends altibus.schema.Lignes> enumerateLignes(
    ) {
        return java.util.Collections.enumeration(this._lignesList);
    }

    /**
     * Method enumerateStations.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends altibus.schema.Stations> enumerateStations(
    ) {
        return java.util.Collections.enumeration(this._stationsList);
    }

    /**
     * Method getArrets.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the altibus.schema.Arrets at the given
     * index
     */
    public altibus.schema.Arrets getArrets(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._arretsList.size()) {
            throw new IndexOutOfBoundsException("getArrets: Index value '" + index + "' not in range [0.." + (this._arretsList.size() - 1) + "]");
        }

        return (altibus.schema.Arrets) _arretsList.get(index);
    }

    /**
     * Method getArrets.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public altibus.schema.Arrets[] getArrets(
    ) {
        altibus.schema.Arrets[] array = new altibus.schema.Arrets[0];
        return (altibus.schema.Arrets[]) this._arretsList.toArray(array);
    }

    /**
     * Method getArretsAsReference.Returns a reference to
     * '_arretsList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<altibus.schema.Arrets> getArretsAsReference(
    ) {
        return this._arretsList;
    }

    /**
     * Method getArretsCount.
     * 
     * @return the size of this collection
     */
    public int getArretsCount(
    ) {
        return this._arretsList.size();
    }

    /**
     * Method getBus.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the altibus.schema.Bus at the given inde
     */
    public altibus.schema.Bus getBus(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._busList.size()) {
            throw new IndexOutOfBoundsException("getBus: Index value '" + index + "' not in range [0.." + (this._busList.size() - 1) + "]");
        }

        return (altibus.schema.Bus) _busList.get(index);
    }

    /**
     * Method getBus.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public altibus.schema.Bus[] getBus(
    ) {
        altibus.schema.Bus[] array = new altibus.schema.Bus[0];
        return (altibus.schema.Bus[]) this._busList.toArray(array);
    }

    /**
     * Method getBusAsReference.Returns a reference to '_busList'.
     * No type checking is performed on any modifications to the
     * Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<altibus.schema.Bus> getBusAsReference(
    ) {
        return this._busList;
    }

    /**
     * Method getBusCount.
     * 
     * @return the size of this collection
     */
    public int getBusCount(
    ) {
        return this._busList.size();
    }

    /**
     * Method getCalendriers.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the altibus.schema.Calendriers at the
     * given index
     */
    public altibus.schema.Calendriers getCalendriers(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._calendriersList.size()) {
            throw new IndexOutOfBoundsException("getCalendriers: Index value '" + index + "' not in range [0.." + (this._calendriersList.size() - 1) + "]");
        }

        return (altibus.schema.Calendriers) _calendriersList.get(index);
    }

    /**
     * Method getCalendriers.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public altibus.schema.Calendriers[] getCalendriers(
    ) {
        altibus.schema.Calendriers[] array = new altibus.schema.Calendriers[0];
        return (altibus.schema.Calendriers[]) this._calendriersList.toArray(array);
    }

    /**
     * Method getCalendriersAsReference.Returns a reference to
     * '_calendriersList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<altibus.schema.Calendriers> getCalendriersAsReference(
    ) {
        return this._calendriersList;
    }

    /**
     * Method getCalendriersCount.
     * 
     * @return the size of this collection
     */
    public int getCalendriersCount(
    ) {
        return this._calendriersList.size();
    }

    /**
     * Returns the value of field 'exploitant'.
     * 
     * @return the value of field 'Exploitant'.
     */
    public altibus.schema.Exploitant getExploitant(
    ) {
        return this._exploitant;
    }

    /**
     * Method getIntervalles.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the altibus.schema.Intervalles at the
     * given index
     */
    public altibus.schema.Intervalles getIntervalles(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._intervallesList.size()) {
            throw new IndexOutOfBoundsException("getIntervalles: Index value '" + index + "' not in range [0.." + (this._intervallesList.size() - 1) + "]");
        }

        return (altibus.schema.Intervalles) _intervallesList.get(index);
    }

    /**
     * Method getIntervalles.Returns the contents of the collection
     * in an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public altibus.schema.Intervalles[] getIntervalles(
    ) {
        altibus.schema.Intervalles[] array = new altibus.schema.Intervalles[0];
        return (altibus.schema.Intervalles[]) this._intervallesList.toArray(array);
    }

    /**
     * Method getIntervallesAsReference.Returns a reference to
     * '_intervallesList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<altibus.schema.Intervalles> getIntervallesAsReference(
    ) {
        return this._intervallesList;
    }

    /**
     * Method getIntervallesCount.
     * 
     * @return the size of this collection
     */
    public int getIntervallesCount(
    ) {
        return this._intervallesList.size();
    }

    /**
     * Method getLignes.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the altibus.schema.Lignes at the given
     * index
     */
    public altibus.schema.Lignes getLignes(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._lignesList.size()) {
            throw new IndexOutOfBoundsException("getLignes: Index value '" + index + "' not in range [0.." + (this._lignesList.size() - 1) + "]");
        }

        return (altibus.schema.Lignes) _lignesList.get(index);
    }

    /**
     * Method getLignes.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public altibus.schema.Lignes[] getLignes(
    ) {
        altibus.schema.Lignes[] array = new altibus.schema.Lignes[0];
        return (altibus.schema.Lignes[]) this._lignesList.toArray(array);
    }

    /**
     * Method getLignesAsReference.Returns a reference to
     * '_lignesList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<altibus.schema.Lignes> getLignesAsReference(
    ) {
        return this._lignesList;
    }

    /**
     * Method getLignesCount.
     * 
     * @return the size of this collection
     */
    public int getLignesCount(
    ) {
        return this._lignesList.size();
    }

    /**
     * Returns the value of field 'reseau'.
     * 
     * @return the value of field 'Reseau'.
     */
    public altibus.schema.Reseau getReseau(
    ) {
        return this._reseau;
    }

    /**
     * Returns the value of field 'schemaLocation'.
     * 
     * @return the value of field 'SchemaLocation'.
     */
    public java.lang.String getSchemaLocation(
    ) {
        return this._schemaLocation;
    }

    /**
     * Method getStations.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the altibus.schema.Stations at the
     * given index
     */
    public altibus.schema.Stations getStations(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stationsList.size()) {
            throw new IndexOutOfBoundsException("getStations: Index value '" + index + "' not in range [0.." + (this._stationsList.size() - 1) + "]");
        }

        return (altibus.schema.Stations) _stationsList.get(index);
    }

    /**
     * Method getStations.Returns the contents of the collection in
     * an Array.  <p>Note:  Just in case the collection contents
     * are changing in another thread, we pass a 0-length Array of
     * the correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public altibus.schema.Stations[] getStations(
    ) {
        altibus.schema.Stations[] array = new altibus.schema.Stations[0];
        return (altibus.schema.Stations[]) this._stationsList.toArray(array);
    }

    /**
     * Method getStationsAsReference.Returns a reference to
     * '_stationsList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<altibus.schema.Stations> getStationsAsReference(
    ) {
        return this._stationsList;
    }

    /**
     * Method getStationsCount.
     * 
     * @return the size of this collection
     */
    public int getStationsCount(
    ) {
        return this._stationsList.size();
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
     * Method iterateArrets.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends altibus.schema.Arrets> iterateArrets(
    ) {
        return this._arretsList.iterator();
    }

    /**
     * Method iterateBus.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends altibus.schema.Bus> iterateBus(
    ) {
        return this._busList.iterator();
    }

    /**
     * Method iterateCalendriers.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends altibus.schema.Calendriers> iterateCalendriers(
    ) {
        return this._calendriersList.iterator();
    }

    /**
     * Method iterateIntervalles.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends altibus.schema.Intervalles> iterateIntervalles(
    ) {
        return this._intervallesList.iterator();
    }

    /**
     * Method iterateLignes.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends altibus.schema.Lignes> iterateLignes(
    ) {
        return this._lignesList.iterator();
    }

    /**
     * Method iterateStations.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends altibus.schema.Stations> iterateStations(
    ) {
        return this._stationsList.iterator();
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
    public void removeAllArrets(
    ) {
        this._arretsList.clear();
    }

    /**
     */
    public void removeAllBus(
    ) {
        this._busList.clear();
    }

    /**
     */
    public void removeAllCalendriers(
    ) {
        this._calendriersList.clear();
    }

    /**
     */
    public void removeAllIntervalles(
    ) {
        this._intervallesList.clear();
    }

    /**
     */
    public void removeAllLignes(
    ) {
        this._lignesList.clear();
    }

    /**
     */
    public void removeAllStations(
    ) {
        this._stationsList.clear();
    }

    /**
     * Method removeArrets.
     * 
     * @param vArrets
     * @return true if the object was removed from the collection.
     */
    public boolean removeArrets(
            final altibus.schema.Arrets vArrets) {
        boolean removed = _arretsList.remove(vArrets);
        return removed;
    }

    /**
     * Method removeArretsAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public altibus.schema.Arrets removeArretsAt(
            final int index) {
        java.lang.Object obj = this._arretsList.remove(index);
        return (altibus.schema.Arrets) obj;
    }

    /**
     * Method removeBus.
     * 
     * @param vBus
     * @return true if the object was removed from the collection.
     */
    public boolean removeBus(
            final altibus.schema.Bus vBus) {
        boolean removed = _busList.remove(vBus);
        return removed;
    }

    /**
     * Method removeBusAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public altibus.schema.Bus removeBusAt(
            final int index) {
        java.lang.Object obj = this._busList.remove(index);
        return (altibus.schema.Bus) obj;
    }

    /**
     * Method removeCalendriers.
     * 
     * @param vCalendriers
     * @return true if the object was removed from the collection.
     */
    public boolean removeCalendriers(
            final altibus.schema.Calendriers vCalendriers) {
        boolean removed = _calendriersList.remove(vCalendriers);
        return removed;
    }

    /**
     * Method removeCalendriersAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public altibus.schema.Calendriers removeCalendriersAt(
            final int index) {
        java.lang.Object obj = this._calendriersList.remove(index);
        return (altibus.schema.Calendriers) obj;
    }

    /**
     * Method removeIntervalles.
     * 
     * @param vIntervalles
     * @return true if the object was removed from the collection.
     */
    public boolean removeIntervalles(
            final altibus.schema.Intervalles vIntervalles) {
        boolean removed = _intervallesList.remove(vIntervalles);
        return removed;
    }

    /**
     * Method removeIntervallesAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public altibus.schema.Intervalles removeIntervallesAt(
            final int index) {
        java.lang.Object obj = this._intervallesList.remove(index);
        return (altibus.schema.Intervalles) obj;
    }

    /**
     * Method removeLignes.
     * 
     * @param vLignes
     * @return true if the object was removed from the collection.
     */
    public boolean removeLignes(
            final altibus.schema.Lignes vLignes) {
        boolean removed = _lignesList.remove(vLignes);
        return removed;
    }

    /**
     * Method removeLignesAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public altibus.schema.Lignes removeLignesAt(
            final int index) {
        java.lang.Object obj = this._lignesList.remove(index);
        return (altibus.schema.Lignes) obj;
    }

    /**
     * Method removeStations.
     * 
     * @param vStations
     * @return true if the object was removed from the collection.
     */
    public boolean removeStations(
            final altibus.schema.Stations vStations) {
        boolean removed = _stationsList.remove(vStations);
        return removed;
    }

    /**
     * Method removeStationsAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public altibus.schema.Stations removeStationsAt(
            final int index) {
        java.lang.Object obj = this._stationsList.remove(index);
        return (altibus.schema.Stations) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vArrets
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setArrets(
            final int index,
            final altibus.schema.Arrets vArrets)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._arretsList.size()) {
            throw new IndexOutOfBoundsException("setArrets: Index value '" + index + "' not in range [0.." + (this._arretsList.size() - 1) + "]");
        }

        this._arretsList.set(index, vArrets);
    }

    /**
     * 
     * 
     * @param vArretsArray
     */
    public void setArrets(
            final altibus.schema.Arrets[] vArretsArray) {
        //-- copy array
        _arretsList.clear();

        for (int i = 0; i < vArretsArray.length; i++) {
                this._arretsList.add(vArretsArray[i]);
        }
    }

    /**
     * Sets the value of '_arretsList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vArretsList the Vector to copy.
     */
    public void setArrets(
            final java.util.List<altibus.schema.Arrets> vArretsList) {
        // copy vector
        this._arretsList.clear();

        this._arretsList.addAll(vArretsList);
    }

    /**
     * Sets the value of '_arretsList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param arretsList the Vector to set.
     */
    public void setArretsAsReference(
            final java.util.List<altibus.schema.Arrets> arretsList) {
        this._arretsList = arretsList;
    }

    /**
     * 
     * 
     * @param index
     * @param vBus
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setBus(
            final int index,
            final altibus.schema.Bus vBus)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._busList.size()) {
            throw new IndexOutOfBoundsException("setBus: Index value '" + index + "' not in range [0.." + (this._busList.size() - 1) + "]");
        }

        this._busList.set(index, vBus);
    }

    /**
     * 
     * 
     * @param vBusArray
     */
    public void setBus(
            final altibus.schema.Bus[] vBusArray) {
        //-- copy array
        _busList.clear();

        for (int i = 0; i < vBusArray.length; i++) {
                this._busList.add(vBusArray[i]);
        }
    }

    /**
     * Sets the value of '_busList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vBusList the Vector to copy.
     */
    public void setBus(
            final java.util.List<altibus.schema.Bus> vBusList) {
        // copy vector
        this._busList.clear();

        this._busList.addAll(vBusList);
    }

    /**
     * Sets the value of '_busList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param busList the Vector to set.
     */
    public void setBusAsReference(
            final java.util.List<altibus.schema.Bus> busList) {
        this._busList = busList;
    }

    /**
     * 
     * 
     * @param index
     * @param vCalendriers
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setCalendriers(
            final int index,
            final altibus.schema.Calendriers vCalendriers)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._calendriersList.size()) {
            throw new IndexOutOfBoundsException("setCalendriers: Index value '" + index + "' not in range [0.." + (this._calendriersList.size() - 1) + "]");
        }

        this._calendriersList.set(index, vCalendriers);
    }

    /**
     * 
     * 
     * @param vCalendriersArray
     */
    public void setCalendriers(
            final altibus.schema.Calendriers[] vCalendriersArray) {
        //-- copy array
        _calendriersList.clear();

        for (int i = 0; i < vCalendriersArray.length; i++) {
                this._calendriersList.add(vCalendriersArray[i]);
        }
    }

    /**
     * Sets the value of '_calendriersList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vCalendriersList the Vector to copy.
     */
    public void setCalendriers(
            final java.util.List<altibus.schema.Calendriers> vCalendriersList) {
        // copy vector
        this._calendriersList.clear();

        this._calendriersList.addAll(vCalendriersList);
    }

    /**
     * Sets the value of '_calendriersList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param calendriersList the Vector to set.
     */
    public void setCalendriersAsReference(
            final java.util.List<altibus.schema.Calendriers> calendriersList) {
        this._calendriersList = calendriersList;
    }

    /**
     * Sets the value of field 'exploitant'.
     * 
     * @param exploitant the value of field 'exploitant'.
     */
    public void setExploitant(
            final altibus.schema.Exploitant exploitant) {
        this._exploitant = exploitant;
    }

    /**
     * 
     * 
     * @param index
     * @param vIntervalles
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setIntervalles(
            final int index,
            final altibus.schema.Intervalles vIntervalles)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._intervallesList.size()) {
            throw new IndexOutOfBoundsException("setIntervalles: Index value '" + index + "' not in range [0.." + (this._intervallesList.size() - 1) + "]");
        }

        this._intervallesList.set(index, vIntervalles);
    }

    /**
     * 
     * 
     * @param vIntervallesArray
     */
    public void setIntervalles(
            final altibus.schema.Intervalles[] vIntervallesArray) {
        //-- copy array
        _intervallesList.clear();

        for (int i = 0; i < vIntervallesArray.length; i++) {
                this._intervallesList.add(vIntervallesArray[i]);
        }
    }

    /**
     * Sets the value of '_intervallesList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vIntervallesList the Vector to copy.
     */
    public void setIntervalles(
            final java.util.List<altibus.schema.Intervalles> vIntervallesList) {
        // copy vector
        this._intervallesList.clear();

        this._intervallesList.addAll(vIntervallesList);
    }

    /**
     * Sets the value of '_intervallesList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param intervallesList the Vector to set.
     */
    public void setIntervallesAsReference(
            final java.util.List<altibus.schema.Intervalles> intervallesList) {
        this._intervallesList = intervallesList;
    }

    /**
     * 
     * 
     * @param index
     * @param vLignes
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setLignes(
            final int index,
            final altibus.schema.Lignes vLignes)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._lignesList.size()) {
            throw new IndexOutOfBoundsException("setLignes: Index value '" + index + "' not in range [0.." + (this._lignesList.size() - 1) + "]");
        }

        this._lignesList.set(index, vLignes);
    }

    /**
     * 
     * 
     * @param vLignesArray
     */
    public void setLignes(
            final altibus.schema.Lignes[] vLignesArray) {
        //-- copy array
        _lignesList.clear();

        for (int i = 0; i < vLignesArray.length; i++) {
                this._lignesList.add(vLignesArray[i]);
        }
    }

    /**
     * Sets the value of '_lignesList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vLignesList the Vector to copy.
     */
    public void setLignes(
            final java.util.List<altibus.schema.Lignes> vLignesList) {
        // copy vector
        this._lignesList.clear();

        this._lignesList.addAll(vLignesList);
    }

    /**
     * Sets the value of '_lignesList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param lignesList the Vector to set.
     */
    public void setLignesAsReference(
            final java.util.List<altibus.schema.Lignes> lignesList) {
        this._lignesList = lignesList;
    }

    /**
     * Sets the value of field 'reseau'.
     * 
     * @param reseau the value of field 'reseau'.
     */
    public void setReseau(
            final altibus.schema.Reseau reseau) {
        this._reseau = reseau;
    }

    /**
     * Sets the value of field 'schemaLocation'.
     * 
     * @param schemaLocation the value of field 'schemaLocation'.
     */
    public void setSchemaLocation(
            final java.lang.String schemaLocation) {
        this._schemaLocation = schemaLocation;
    }

    /**
     * 
     * 
     * @param index
     * @param vStations
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setStations(
            final int index,
            final altibus.schema.Stations vStations)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._stationsList.size()) {
            throw new IndexOutOfBoundsException("setStations: Index value '" + index + "' not in range [0.." + (this._stationsList.size() - 1) + "]");
        }

        this._stationsList.set(index, vStations);
    }

    /**
     * 
     * 
     * @param vStationsArray
     */
    public void setStations(
            final altibus.schema.Stations[] vStationsArray) {
        //-- copy array
        _stationsList.clear();

        for (int i = 0; i < vStationsArray.length; i++) {
                this._stationsList.add(vStationsArray[i]);
        }
    }

    /**
     * Sets the value of '_stationsList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vStationsList the Vector to copy.
     */
    public void setStations(
            final java.util.List<altibus.schema.Stations> vStationsList) {
        // copy vector
        this._stationsList.clear();

        this._stationsList.addAll(vStationsList);
    }

    /**
     * Sets the value of '_stationsList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param stationsList the Vector to set.
     */
    public void setStationsAsReference(
            final java.util.List<altibus.schema.Stations> stationsList) {
        this._stationsList = stationsList;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled altibus.schema.Altibus
     */
    public static altibus.schema.Altibus unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (altibus.schema.Altibus) org.exolab.castor.xml.Unmarshaller.unmarshal(altibus.schema.Altibus.class, reader);
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
