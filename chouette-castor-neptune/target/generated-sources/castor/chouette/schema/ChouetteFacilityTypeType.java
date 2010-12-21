/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Description des equipement situ�s dans les zones d'arr�t ou les
 * vehicules (via la ligne)
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class ChouetteFacilityTypeType extends chouette.schema.TridentObjectTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _facilityLocation.
     */
    private chouette.schema.FacilityLocation _facilityLocation;

    /**
     * The facility has to be attached to a StopAres (Quay,
     * BoardingPosition or Stop Place), a line (meaning vehicles
     * operating this line), a connection link or a Stop Point on
     * Route (stopPoint))
     */
    private chouette.schema.ChouetteFacilityTypeChoice _chouetteFacilityTypeChoice;

    /**
     * Description of the feauture of the facility. Several
     * features may be associated to a single facility.
     */
    private java.util.List<chouette.schema.FacilityFeature> _facilityFeatureList;

    /**
     * Name of the facility
     */
    private java.lang.String _name;

    /**
     * Textual description of the facility
     */
    private java.lang.String _description;

    /**
     * Is the access restricted or authorised to everybody
     */
    private boolean _freeAccess;

    /**
     * keeps track of state for field: _freeAccess
     */
    private boolean _has_freeAccess;

    /**
     * Field _comment.
     */
    private java.lang.String _comment;


      //----------------/
     //- Constructors -/
    //----------------/

    public ChouetteFacilityTypeType() {
        super();
        this._facilityFeatureList = new java.util.ArrayList<chouette.schema.FacilityFeature>();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vFacilityFeature
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFacilityFeature(
            final chouette.schema.FacilityFeature vFacilityFeature)
    throws java.lang.IndexOutOfBoundsException {
        this._facilityFeatureList.add(vFacilityFeature);
    }

    /**
     * 
     * 
     * @param index
     * @param vFacilityFeature
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void addFacilityFeature(
            final int index,
            final chouette.schema.FacilityFeature vFacilityFeature)
    throws java.lang.IndexOutOfBoundsException {
        this._facilityFeatureList.add(index, vFacilityFeature);
    }

    /**
     */
    public void deleteFreeAccess(
    ) {
        this._has_freeAccess= false;
    }

    /**
     * Method enumerateFacilityFeature.
     * 
     * @return an Enumeration over all possible elements of this
     * collection
     */
    public java.util.Enumeration<? extends chouette.schema.FacilityFeature> enumerateFacilityFeature(
    ) {
        return java.util.Collections.enumeration(this._facilityFeatureList);
    }

    /**
     * Returns the value of field 'chouetteFacilityTypeChoice'. The
     * field 'chouetteFacilityTypeChoice' has the following
     * description: The facility has to be attached to a StopAres
     * (Quay, BoardingPosition or Stop Place), a line (meaning
     * vehicles operating this line), a connection link or a Stop
     * Point on Route (stopPoint))
     * 
     * @return the value of field 'ChouetteFacilityTypeChoice'.
     */
    public chouette.schema.ChouetteFacilityTypeChoice getChouetteFacilityTypeChoice(
    ) {
        return this._chouetteFacilityTypeChoice;
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
     * Returns the value of field 'description'. The field
     * 'description' has the following description: Textual
     * description of the facility
     * 
     * @return the value of field 'Description'.
     */
    public java.lang.String getDescription(
    ) {
        return this._description;
    }

    /**
     * Method getFacilityFeature.
     * 
     * @param index
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     * @return the value of the chouette.schema.FacilityFeature at
     * the given index
     */
    public chouette.schema.FacilityFeature getFacilityFeature(
            final int index)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._facilityFeatureList.size()) {
            throw new IndexOutOfBoundsException("getFacilityFeature: Index value '" + index + "' not in range [0.." + (this._facilityFeatureList.size() - 1) + "]");
        }

        return (chouette.schema.FacilityFeature) _facilityFeatureList.get(index);
    }

    /**
     * Method getFacilityFeature.Returns the contents of the
     * collection in an Array.  <p>Note:  Just in case the
     * collection contents are changing in another thread, we pass
     * a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of
     * exactly the correct length.
     * 
     * @return this collection as an Array
     */
    public chouette.schema.FacilityFeature[] getFacilityFeature(
    ) {
        chouette.schema.FacilityFeature[] array = new chouette.schema.FacilityFeature[0];
        return (chouette.schema.FacilityFeature[]) this._facilityFeatureList.toArray(array);
    }

    /**
     * Method getFacilityFeatureAsReference.Returns a reference to
     * '_facilityFeatureList'. No type checking is performed on any
     * modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public java.util.List<chouette.schema.FacilityFeature> getFacilityFeatureAsReference(
    ) {
        return this._facilityFeatureList;
    }

    /**
     * Method getFacilityFeatureCount.
     * 
     * @return the size of this collection
     */
    public int getFacilityFeatureCount(
    ) {
        return this._facilityFeatureList.size();
    }

    /**
     * Returns the value of field 'facilityLocation'.
     * 
     * @return the value of field 'FacilityLocation'.
     */
    public chouette.schema.FacilityLocation getFacilityLocation(
    ) {
        return this._facilityLocation;
    }

    /**
     * Returns the value of field 'freeAccess'. The field
     * 'freeAccess' has the following description: Is the access
     * restricted or authorised to everybody
     * 
     * @return the value of field 'FreeAccess'.
     */
    public boolean getFreeAccess(
    ) {
        return this._freeAccess;
    }

    /**
     * Returns the value of field 'name'. The field 'name' has the
     * following description: Name of the facility
     * 
     * @return the value of field 'Name'.
     */
    public java.lang.String getName(
    ) {
        return this._name;
    }

    /**
     * Method hasFreeAccess.
     * 
     * @return true if at least one FreeAccess has been added
     */
    public boolean hasFreeAccess(
    ) {
        return this._has_freeAccess;
    }

    /**
     * Returns the value of field 'freeAccess'. The field
     * 'freeAccess' has the following description: Is the access
     * restricted or authorised to everybody
     * 
     * @return the value of field 'FreeAccess'.
     */
    public boolean isFreeAccess(
    ) {
        return this._freeAccess;
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
     * Method iterateFacilityFeature.
     * 
     * @return an Iterator over all possible elements in this
     * collection
     */
    public java.util.Iterator<? extends chouette.schema.FacilityFeature> iterateFacilityFeature(
    ) {
        return this._facilityFeatureList.iterator();
    }

    /**
     */
    public void removeAllFacilityFeature(
    ) {
        this._facilityFeatureList.clear();
    }

    /**
     * Method removeFacilityFeature.
     * 
     * @param vFacilityFeature
     * @return true if the object was removed from the collection.
     */
    public boolean removeFacilityFeature(
            final chouette.schema.FacilityFeature vFacilityFeature) {
        boolean removed = _facilityFeatureList.remove(vFacilityFeature);
        return removed;
    }

    /**
     * Method removeFacilityFeatureAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public chouette.schema.FacilityFeature removeFacilityFeatureAt(
            final int index) {
        java.lang.Object obj = this._facilityFeatureList.remove(index);
        return (chouette.schema.FacilityFeature) obj;
    }

    /**
     * Sets the value of field 'chouetteFacilityTypeChoice'. The
     * field 'chouetteFacilityTypeChoice' has the following
     * description: The facility has to be attached to a StopAres
     * (Quay, BoardingPosition or Stop Place), a line (meaning
     * vehicles operating this line), a connection link or a Stop
     * Point on Route (stopPoint))
     * 
     * @param chouetteFacilityTypeChoice the value of field
     * 'chouetteFacilityTypeChoice'.
     */
    public void setChouetteFacilityTypeChoice(
            final chouette.schema.ChouetteFacilityTypeChoice chouetteFacilityTypeChoice) {
        this._chouetteFacilityTypeChoice = chouetteFacilityTypeChoice;
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
     * Sets the value of field 'description'. The field
     * 'description' has the following description: Textual
     * description of the facility
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(
            final java.lang.String description) {
        this._description = description;
    }

    /**
     * 
     * 
     * @param index
     * @param vFacilityFeature
     * @throws java.lang.IndexOutOfBoundsException if the index
     * given is outside the bounds of the collection
     */
    public void setFacilityFeature(
            final int index,
            final chouette.schema.FacilityFeature vFacilityFeature)
    throws java.lang.IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this._facilityFeatureList.size()) {
            throw new IndexOutOfBoundsException("setFacilityFeature: Index value '" + index + "' not in range [0.." + (this._facilityFeatureList.size() - 1) + "]");
        }

        this._facilityFeatureList.set(index, vFacilityFeature);
    }

    /**
     * 
     * 
     * @param vFacilityFeatureArray
     */
    public void setFacilityFeature(
            final chouette.schema.FacilityFeature[] vFacilityFeatureArray) {
        //-- copy array
        _facilityFeatureList.clear();

        for (int i = 0; i < vFacilityFeatureArray.length; i++) {
                this._facilityFeatureList.add(vFacilityFeatureArray[i]);
        }
    }

    /**
     * Sets the value of '_facilityFeatureList' by copying the
     * given Vector. All elements will be checked for type safety.
     * 
     * @param vFacilityFeatureList the Vector to copy.
     */
    public void setFacilityFeature(
            final java.util.List<chouette.schema.FacilityFeature> vFacilityFeatureList) {
        // copy vector
        this._facilityFeatureList.clear();

        this._facilityFeatureList.addAll(vFacilityFeatureList);
    }

    /**
     * Sets the value of '_facilityFeatureList' by setting it to
     * the given Vector. No type checking is performed.
     * @deprecated
     * 
     * @param facilityFeatureList the Vector to set.
     */
    public void setFacilityFeatureAsReference(
            final java.util.List<chouette.schema.FacilityFeature> facilityFeatureList) {
        this._facilityFeatureList = facilityFeatureList;
    }

    /**
     * Sets the value of field 'facilityLocation'.
     * 
     * @param facilityLocation the value of field 'facilityLocation'
     */
    public void setFacilityLocation(
            final chouette.schema.FacilityLocation facilityLocation) {
        this._facilityLocation = facilityLocation;
    }

    /**
     * Sets the value of field 'freeAccess'. The field 'freeAccess'
     * has the following description: Is the access restricted or
     * authorised to everybody
     * 
     * @param freeAccess the value of field 'freeAccess'.
     */
    public void setFreeAccess(
            final boolean freeAccess) {
        this._freeAccess = freeAccess;
        this._has_freeAccess = true;
    }

    /**
     * Sets the value of field 'name'. The field 'name' has the
     * following description: Name of the facility
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
