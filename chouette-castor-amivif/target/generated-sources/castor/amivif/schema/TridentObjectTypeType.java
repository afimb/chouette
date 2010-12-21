/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * Class TridentObjectTypeType.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class TridentObjectTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _objectId.
     */
    private java.lang.String _objectId;

    /**
     * Field _objectVersion.
     */
    private long _objectVersion;

    /**
     * keeps track of state for field: _objectVersion
     */
    private boolean _has_objectVersion;

    /**
     * Field _creationTime.
     */
    private java.util.Date _creationTime;

    /**
     * Field _expiryTime.
     */
    private java.util.Date _expiryTime;

    /**
     * Field _creatorId.
     */
    private java.lang.String _creatorId;

    /**
     * Field _tridentObjectTypeChoice.
     */
    private amivif.schema.TridentObjectTypeChoice _tridentObjectTypeChoice;


      //----------------/
     //- Constructors -/
    //----------------/

    public TridentObjectTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteObjectVersion(
    ) {
        this._has_objectVersion= false;
    }

    /**
     * Returns the value of field 'creationTime'.
     * 
     * @return the value of field 'CreationTime'.
     */
    public java.util.Date getCreationTime(
    ) {
        return this._creationTime;
    }

    /**
     * Returns the value of field 'creatorId'.
     * 
     * @return the value of field 'CreatorId'.
     */
    public java.lang.String getCreatorId(
    ) {
        return this._creatorId;
    }

    /**
     * Returns the value of field 'expiryTime'.
     * 
     * @return the value of field 'ExpiryTime'.
     */
    public java.util.Date getExpiryTime(
    ) {
        return this._expiryTime;
    }

    /**
     * Returns the value of field 'objectId'.
     * 
     * @return the value of field 'ObjectId'.
     */
    public java.lang.String getObjectId(
    ) {
        return this._objectId;
    }

    /**
     * Returns the value of field 'objectVersion'.
     * 
     * @return the value of field 'ObjectVersion'.
     */
    public long getObjectVersion(
    ) {
        return this._objectVersion;
    }

    /**
     * Returns the value of field 'tridentObjectTypeChoice'.
     * 
     * @return the value of field 'TridentObjectTypeChoice'.
     */
    public amivif.schema.TridentObjectTypeChoice getTridentObjectTypeChoice(
    ) {
        return this._tridentObjectTypeChoice;
    }

    /**
     * Method hasObjectVersion.
     * 
     * @return true if at least one ObjectVersion has been added
     */
    public boolean hasObjectVersion(
    ) {
        return this._has_objectVersion;
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
     * Sets the value of field 'creationTime'.
     * 
     * @param creationTime the value of field 'creationTime'.
     */
    public void setCreationTime(
            final java.util.Date creationTime) {
        this._creationTime = creationTime;
    }

    /**
     * Sets the value of field 'creatorId'.
     * 
     * @param creatorId the value of field 'creatorId'.
     */
    public void setCreatorId(
            final java.lang.String creatorId) {
        this._creatorId = creatorId;
    }

    /**
     * Sets the value of field 'expiryTime'.
     * 
     * @param expiryTime the value of field 'expiryTime'.
     */
    public void setExpiryTime(
            final java.util.Date expiryTime) {
        this._expiryTime = expiryTime;
    }

    /**
     * Sets the value of field 'objectId'.
     * 
     * @param objectId the value of field 'objectId'.
     */
    public void setObjectId(
            final java.lang.String objectId) {
        this._objectId = objectId;
    }

    /**
     * Sets the value of field 'objectVersion'.
     * 
     * @param objectVersion the value of field 'objectVersion'.
     */
    public void setObjectVersion(
            final long objectVersion) {
        this._objectVersion = objectVersion;
        this._has_objectVersion = true;
    }

    /**
     * Sets the value of field 'tridentObjectTypeChoice'.
     * 
     * @param tridentObjectTypeChoice the value of field
     * 'tridentObjectTypeChoice'.
     */
    public void setTridentObjectTypeChoice(
            final amivif.schema.TridentObjectTypeChoice tridentObjectTypeChoice) {
        this._tridentObjectTypeChoice = tridentObjectTypeChoice;
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
