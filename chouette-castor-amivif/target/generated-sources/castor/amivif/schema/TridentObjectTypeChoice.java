/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * Class TridentObjectTypeChoice.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class TridentObjectTypeChoice extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _validityPeriodList.
     */
    private java.util.List<amivif.schema.ValidityPeriod> _validityPeriodList;

    /**
     * Field _validityDomain.
     */
    private java.lang.String _validityDomain;


      //----------------/
     //- Constructors -/
    //----------------/

    public TridentObjectTypeChoice() {
        super();
        this._validityPeriodList = new java.util.ArrayList<amivif.schema.ValidityPeriod>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vValidityPeriod
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addValidityPeriod(
            final amivif.schema.ValidityPeriod vValidityPeriod)
    throws java.lang.IndexOutOfBoundsException {
        this._validityPeriodList.add(vValidityPeriod);
    }

    /**
     * 
     * 
     * @param index
     * @param vValidityPeriod
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addValidityPeriod(
            final int index,
            final amivif.schema.ValidityPeriod vValidityPeriod)
    throws java.lang.IndexOutOfBoundsException {
        this._validityPeriodList.add(index, vValidityPeriod);
    }

    /**
     * Method enumerateValidityPeriod.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends amivif.schema.ValidityPeriod> enumerateValidityPeriod(
    ) {
        return java.util.Collections.enumeration(this._validityPeriodList);
    }

    /**
     * Returns the value of field 'validityDomain'.
     * 
     * @return the value of field 'ValidityDomain'.
     */
    public java.lang.String getValidityDomain(
    ) {
        return this._validityDomain;
    }

    /**
     * Method getValidityPeriod.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the amivif.schema.ValidityPeriod at the
     * given index
     */
    public amivif.schema.ValidityPeriod getValidityPeriod(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._validityPeriodList.size()) {
            throw new IndexOutOfBoundsException("getValidityPeriod: Index value '" + index + "' not in range [0.." + (this._validityPeriodList.size() - 1) + "]");
        }

        return (amivif.schema.ValidityPeriod) _validityPeriodList.get(index);
    }

    /**
     * Method getValidityPeriod.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public amivif.schema.ValidityPeriod[] getValidityPeriod(
    ) {
        amivif.schema.ValidityPeriod[] array = new amivif.schema.ValidityPeriod[0];
        return (amivif.schema.ValidityPeriod[]) this._validityPeriodList.toArray(array);
    }

    /**
     * Method getValidityPeriodAsReference.Returns a reference to
     * '_validityPeriodList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<amivif.schema.ValidityPeriod> getValidityPeriodAsReference(
    ) {
        return this._validityPeriodList;
    }

    /**
     * Method getValidityPeriodCount.
     * 
     * @return the size of this collection
     */
    public int getValidityPeriodCount(
    ) {
        return this._validityPeriodList.size();
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
     * Method iterateValidityPeriod.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends amivif.schema.ValidityPeriod> iterateValidityPeriod(
    ) {
        return this._validityPeriodList.iterator();
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
    public void removeAllValidityPeriod(
    ) {
        this._validityPeriodList.clear();
    }

    /**
     * Method removeValidityPeriod.
     * 
     * @param vValidityPeriod
     * @return true if the object was removed from the collection.
     */
    public boolean removeValidityPeriod(
            final amivif.schema.ValidityPeriod vValidityPeriod) {
        boolean removed = _validityPeriodList.remove(vValidityPeriod);
        return removed;
    }

    /**
     * Method removeValidityPeriodAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public amivif.schema.ValidityPeriod removeValidityPeriodAt(
            final int index) {
        java.lang.Object obj = this._validityPeriodList.remove(index);
        return (amivif.schema.ValidityPeriod) obj;
    }

    /**
     * Sets the value of field 'validityDomain'.
     * 
     * @param validityDomain the value of field 'validityDomain'.
     */
    public void setValidityDomain(
            final java.lang.String validityDomain) {
        this._validityDomain = validityDomain;
    }

    /**
     * 
     * 
     * @param index
     * @param vValidityPeriod
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setValidityPeriod(
            final int index,
            final amivif.schema.ValidityPeriod vValidityPeriod)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._validityPeriodList.size()) {
            throw new IndexOutOfBoundsException("setValidityPeriod: Index value '" + index + "' not in range [0.." + (this._validityPeriodList.size() - 1) + "]");
        }

        this._validityPeriodList.set(index, vValidityPeriod);
    }

    /**
     * 
     * 
     * @param vValidityPeriodArray
     */
    public void setValidityPeriod(
            final amivif.schema.ValidityPeriod[] vValidityPeriodArray) {
        //-- copy array
        _validityPeriodList.clear();

        for (int i = 0; i < vValidityPeriodArray.length; i++) {
                this._validityPeriodList.add(vValidityPeriodArray[i]);
        }
    }

    /**
     * Sets the value of '_validityPeriodList' by copying the given
     * Vector. All elements will be checked for type safety.
     * 
     * @param vValidityPeriodList the Vector to copy.
     */
    public void setValidityPeriod(
            final java.util.List<amivif.schema.ValidityPeriod> vValidityPeriodList) {
        // copy vector
        this._validityPeriodList.clear();

        this._validityPeriodList.addAll(vValidityPeriodList);
    }

    /**
     * Sets the value of '_validityPeriodList' by setting it to the
     * given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param validityPeriodList the Vector to set.
     */
    public void setValidityPeriodAsReference(
            final java.util.List<amivif.schema.ValidityPeriod> validityPeriodList) {
        this._validityPeriodList = validityPeriodList;
    }

    /**
     * Method unmarshal.
     * 
     * @param reader
     * @throws org.exolab.castor.xml.MarshalException if object is
     * null or if any SAXException is thrown during marshaling
     * @throws org.exolab.castor.xml.ValidationException if this
     * object is an invalid instance according to the schema
     * @return the unmarshaled amivif.schema.TridentObjectTypeChoice
     */
    public static amivif.schema.TridentObjectTypeChoice unmarshal(
            final java.io.Reader reader)
    throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException {
        return (amivif.schema.TridentObjectTypeChoice) org.exolab.castor.xml.Unmarshaller.unmarshal(amivif.schema.TridentObjectTypeChoice.class, reader);
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
