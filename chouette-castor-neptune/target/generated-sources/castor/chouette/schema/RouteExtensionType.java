/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Extension d'itin�raire qui en pr�cise le sens, aller ou retour
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class RouteExtensionType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _wayBack.
     */
    private java.lang.String _wayBack;


      //----------------/
     //- Constructors -/
    //----------------/

    public RouteExtensionType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'wayBack'.
     * 
     * @return the value of field 'WayBack'.
     */
    public java.lang.String getWayBack(
    ) {
        return this._wayBack;
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
     * Sets the value of field 'wayBack'.
     * 
     * @param wayBack the value of field 'wayBack'.
     */
    public void setWayBack(
            final java.lang.String wayBack) {
        this._wayBack = wayBack;
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
