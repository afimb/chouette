/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package altibus.schema;

/**
 * Class Lignes.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class Lignes extends java.lang.Object 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _ligneList.
     */
    private java.util.List<altibus.schema.Ligne> _ligneList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Lignes() {
        super();
        this._ligneList = new java.util.ArrayList<altibus.schema.Ligne>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vLigne
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addLigne(
            final altibus.schema.Ligne vLigne)
    throws java.lang.IndexOutOfBoundsException {
        this._ligneList.add(vLigne);
    }

    /**
     * 
     * 
     * @param index
     * @param vLigne
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addLigne(
            final int index,
            final altibus.schema.Ligne vLigne)
    throws java.lang.IndexOutOfBoundsException {
        this._ligneList.add(index, vLigne);
    }

    /**
     * Method enumerateLigne.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends altibus.schema.Ligne> enumerateLigne(
    ) {
        return java.util.Collections.enumeration(this._ligneList);
    }

    /**
     * Method getLigne.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the altibus.schema.Ligne at the given
     * index
     */
    public altibus.schema.Ligne getLigne(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ligneList.size()) {
            throw new IndexOutOfBoundsException("getLigne: Index value '" + index + "' not in range [0.." + (this._ligneList.size() - 1) + "]");
        }

        return (altibus.schema.Ligne) _ligneList.get(index);
    }

    /**
     * Method getLigne.Returns the contents of the collection in an
     * Array.  <p>Note:  Just in case the collection contents are
     * changing in another thread, we pass a 0-length Array of the
     * correct type into the API call.  This way we <i>know</i>
     * that the Array returned is of exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public altibus.schema.Ligne[] getLigne(
    ) {
        altibus.schema.Ligne[] array = new altibus.schema.Ligne[0];
        return (altibus.schema.Ligne[]) this._ligneList.toArray(array);
    }

    /**
     * Method getLigneAsReference.Returns a reference to
     * '_ligneList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<altibus.schema.Ligne> getLigneAsReference(
    ) {
        return this._ligneList;
    }

    /**
     * Method getLigneCount.
     * 
     * @return the size of this collection
     */
    public int getLigneCount(
    ) {
        return this._ligneList.size();
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
     * Method iterateLigne.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends altibus.schema.Ligne> iterateLigne(
    ) {
        return this._ligneList.iterator();
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
    public void removeAllLigne(
    ) {
        this._ligneList.clear();
    }

    /**
     * Method removeLigne.
     * 
     * @param vLigne
     * @return true if the object was removed from the collection.
     */
    public boolean removeLigne(
            final altibus.schema.Ligne vLigne) {
        boolean removed = _ligneList.remove(vLigne);
        return removed;
    }

    /**
     * Method removeLigneAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public altibus.schema.Ligne removeLigneAt(
            final int index) {
        java.lang.Object obj = this._ligneList.remove(index);
        return (altibus.schema.Ligne) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vLigne
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setLigne(
            final int index,
            final altibus.schema.Ligne vLigne)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._ligneList.size()) {
            throw new IndexOutOfBoundsException("setLigne: Index value '" + index + "' not in range [0.." + (this._ligneList.size() - 1) + "]");
        }

        this._ligneList.set(index, vLigne);
    }

    /**
     * 
     * 
     * @param vLigneArray
     */
    public void setLigne(
            final altibus.schema.Ligne[] vLigneArray) {
        //-- copy array
        _ligneList.clear();

        for (int i = 0; i < vLigneArray.length; i++) {
                this._ligneList.add(vLigneArray[i]);
        }
    }

    /**
     * Sets the value of '_ligneList' by copying the given Vector.
     * All elements will be checked for type safety.
     * 
     * @param vLigneList the Vector to copy.
     */
    public void setLigne(
            final java.util.List<altibus.schema.Ligne> vLigneList) {
        // copy vector
        this._ligneList.clear();

        this._ligneList.addAll(vLigneList);
    }

    /**
     * Sets the value of '_ligneList' by setting it to the given
     * Vector. No type checking is performed.
     * @deprecated
     * 
     * @param ligneList the Vector to set.
     */
    public void setLigneAsReference(
            final java.util.List<altibus.schema.Ligne> ligneList) {
        this._ligneList = ligneList;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled altibus.schema.Lignes
     */
    public static altibus.schema.Lignes unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (altibus.schema.Lignes) org.exolab.castor.xml.Unmarshaller.unmarshal(altibus.schema.Lignes.class, reader);
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
