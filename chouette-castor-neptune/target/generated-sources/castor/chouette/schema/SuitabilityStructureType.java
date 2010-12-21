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
public abstract class SuitabilityStructureType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Whether the Facility is suitable 
     */
    private chouette.schema.types.SuitabilityEnumeration _suitable;

    /**
     * Field _suitabilityStructureUserNeed.
     */
    private chouette.schema.SuitabilityStructureUserNeed _suitabilityStructureUserNeed;


      //----------------/
     //- Constructors -/
    //----------------/

    public SuitabilityStructureType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'suitabilityStructureUserNeed'.
     * 
     * @return the value of field 'SuitabilityStructureUserNeed'.
     */
    public chouette.schema.SuitabilityStructureUserNeed getSuitabilityStructureUserNeed(
    ) {
        return this._suitabilityStructureUserNeed;
    }

    /**
     * Returns the value of field 'suitable'. The field 'suitable'
     * has the following description: Whether the Facility is
     * suitable 
     * 
     * @return the value of field 'Suitable'.
     */
    public chouette.schema.types.SuitabilityEnumeration getSuitable(
    ) {
        return this._suitable;
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
     * Sets the value of field 'suitabilityStructureUserNeed'.
     * 
     * @param suitabilityStructureUserNeed the value of field
     * 'suitabilityStructureUserNeed'.
     */
    public void setSuitabilityStructureUserNeed(
            final chouette.schema.SuitabilityStructureUserNeed suitabilityStructureUserNeed) {
        this._suitabilityStructureUserNeed = suitabilityStructureUserNeed;
    }

    /**
     * Sets the value of field 'suitable'. The field 'suitable' has
     * the following description: Whether the Facility is suitable 
     * 
     * @param suitable the value of field 'suitable'.
     */
    public void setSuitable(
            final chouette.schema.types.SuitabilityEnumeration suitable) {
        this._suitable = suitable;
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
