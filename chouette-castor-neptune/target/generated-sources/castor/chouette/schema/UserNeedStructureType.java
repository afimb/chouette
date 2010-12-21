/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Type for of a specific need
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class UserNeedStructureType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * one of the following
     */
    private chouette.schema.UserNeedGroup _userNeedGroup;

    /**
     * Whether user need is included or excluded. Default is include
     */
    private boolean _excluded;

    /**
     * keeps track of state for field: _excluded
     */
    private boolean _has_excluded;

    /**
     * Relative ranking of need on a sclae 1-5
     */
    private long _needRanking;

    /**
     * keeps track of state for field: _needRanking
     */
    private boolean _has_needRanking;

    /**
     * Field _extensions.
     */
    private java.lang.Object _extensions;


      //----------------/
     //- Constructors -/
    //----------------/

    public UserNeedStructureType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     */
    public void deleteExcluded(
    ) {
        this._has_excluded= false;
    }

    /**
     */
    public void deleteNeedRanking(
    ) {
        this._has_needRanking= false;
    }

    /**
     * Returns the value of field 'excluded'. The field 'excluded'
     * has the following description: Whether user need is included
     * or excluded. Default is included
     * 
     * @return the value of field 'Excluded'.
     */
    public boolean getExcluded(
    ) {
        return this._excluded;
    }

    /**
     * Returns the value of field 'extensions'.
     * 
     * @return the value of field 'Extensions'.
     */
    public java.lang.Object getExtensions(
    ) {
        return this._extensions;
    }

    /**
     * Returns the value of field 'needRanking'. The field
     * 'needRanking' has the following description: Relative
     * ranking of need on a sclae 1-5
     * 
     * @return the value of field 'NeedRanking'.
     */
    public long getNeedRanking(
    ) {
        return this._needRanking;
    }

    /**
     * Returns the value of field 'userNeedGroup'. The field
     * 'userNeedGroup' has the following description: one of the
     * following
     * 
     * @return the value of field 'UserNeedGroup'.
     */
    public chouette.schema.UserNeedGroup getUserNeedGroup(
    ) {
        return this._userNeedGroup;
    }

    /**
     * Method hasExcluded.
     * 
     * @return true if at least one Excluded has been added
     */
    public boolean hasExcluded(
    ) {
        return this._has_excluded;
    }

    /**
     * Method hasNeedRanking.
     * 
     * @return true if at least one NeedRanking has been added
     */
    public boolean hasNeedRanking(
    ) {
        return this._has_needRanking;
    }

    /**
     * Returns the value of field 'excluded'. The field 'excluded'
     * has the following description: Whether user need is included
     * or excluded. Default is included
     * 
     * @return the value of field 'Excluded'.
     */
    public boolean isExcluded(
    ) {
        return this._excluded;
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
     * Sets the value of field 'excluded'. The field 'excluded' has
     * the following description: Whether user need is included or
     * excluded. Default is included
     * 
     * @param excluded the value of field 'excluded'.
     */
    public void setExcluded(
            final boolean excluded) {
        this._excluded = excluded;
        this._has_excluded = true;
    }

    /**
     * Sets the value of field 'extensions'.
     * 
     * @param extensions the value of field 'extensions'.
     */
    public void setExtensions(
            final java.lang.Object extensions) {
        this._extensions = extensions;
    }

    /**
     * Sets the value of field 'needRanking'. The field
     * 'needRanking' has the following description: Relative
     * ranking of need on a sclae 1-5
     * 
     * @param needRanking the value of field 'needRanking'.
     */
    public void setNeedRanking(
            final long needRanking) {
        this._needRanking = needRanking;
        this._has_needRanking = true;
    }

    /**
     * Sets the value of field 'userNeedGroup'. The field
     * 'userNeedGroup' has the following description: one of the
     * following
     * 
     * @param userNeedGroup the value of field 'userNeedGroup'.
     */
    public void setUserNeedGroup(
            final chouette.schema.UserNeedGroup userNeedGroup) {
        this._userNeedGroup = userNeedGroup;
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
