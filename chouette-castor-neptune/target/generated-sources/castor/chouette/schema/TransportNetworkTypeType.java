/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * PT Network description, can be for Public Transport or Road
 * Network.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class TransportNetworkTypeType extends chouette.schema.LogicalLocationTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _versionDate.
     */
    private org.exolab.castor.types.Date _versionDate;

    /**
     * Field _description.
     */
    private java.lang.String _description;


      //----------------/
     //- Constructors -/
    //----------------/

    public TransportNetworkTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'description'.
     * 
     * @return the value of field 'Description'.
     */
    public java.lang.String getDescription(
    ) {
        return this._description;
    }

    /**
     * Returns the value of field 'versionDate'.
     * 
     * @return the value of field 'VersionDate'.
     */
    public org.exolab.castor.types.Date getVersionDate(
    ) {
        return this._versionDate;
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
     * Sets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(
            final java.lang.String description) {
        this._description = description;
    }

    /**
     * Sets the value of field 'versionDate'.
     * 
     * @param versionDate the value of field 'versionDate'.
     */
    public void setVersionDate(
            final org.exolab.castor.types.Date versionDate) {
        this._versionDate = versionDate;
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
