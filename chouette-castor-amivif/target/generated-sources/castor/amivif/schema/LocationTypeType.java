/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * General description of a location (point, area, link)
 *  This type is an abstract type
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class LocationTypeType extends amivif.schema.LogicalLocationTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _referencingMethod.
     */
    private amivif.schema.types.LocationReferencingMethodType _referencingMethod;


      //----------------/
     //- Constructors -/
    //----------------/

    public LocationTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'referencingMethod'.
     * 
     * @return the value of field 'ReferencingMethod'.
     */
    public amivif.schema.types.LocationReferencingMethodType getReferencingMethod(
    ) {
        return this._referencingMethod;
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
     * Sets the value of field 'referencingMethod'.
     * 
     * @param referencingMethod the value of field
     * 'referencingMethod'.
     */
    public void setReferencingMethod(
            final amivif.schema.types.LocationReferencingMethodType referencingMethod) {
        this._referencingMethod = referencingMethod;
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
