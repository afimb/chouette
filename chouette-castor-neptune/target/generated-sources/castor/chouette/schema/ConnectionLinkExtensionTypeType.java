/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Class ConnectionLinkExtensionTypeType.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class ConnectionLinkExtensionTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _accessibilitySuitabilityDetails.
     */
    private chouette.schema.AccessibilitySuitabilityDetails _accessibilitySuitabilityDetails;


      //----------------/
     //- Constructors -/
    //----------------/

    public ConnectionLinkExtensionTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field
     * 'accessibilitySuitabilityDetails'.
     * 
     * @return the value of field 'AccessibilitySuitabilityDetails'.
     */
    public chouette.schema.AccessibilitySuitabilityDetails getAccessibilitySuitabilityDetails(
    ) {
        return this._accessibilitySuitabilityDetails;
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
     * Sets the value of field 'accessibilitySuitabilityDetails'.
     * 
     * @param accessibilitySuitabilityDetails the value of field
     * 'accessibilitySuitabilityDetails'.
     */
    public void setAccessibilitySuitabilityDetails(
            final chouette.schema.AccessibilitySuitabilityDetails accessibilitySuitabilityDetails) {
        this._accessibilitySuitabilityDetails = accessibilitySuitabilityDetails;
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
