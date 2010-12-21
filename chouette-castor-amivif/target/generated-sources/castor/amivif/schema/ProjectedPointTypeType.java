/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * Position of a point in a projection system.
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class ProjectedPointTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _x.
     */
    private java.math.BigDecimal _x;

    /**
     * Field _y.
     */
    private java.math.BigDecimal _y;

    /**
     * Field _projectionType.
     */
    private java.lang.String _projectionType;


      //----------------/
     //- Constructors -/
    //----------------/

    public ProjectedPointTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'projectionType'.
     * 
     * @return the value of field 'ProjectionType'.
     */
    public java.lang.String getProjectionType(
    ) {
        return this._projectionType;
    }

    /**
     * Returns the value of field 'x'.
     * 
     * @return the value of field 'X'.
     */
    public java.math.BigDecimal getX(
    ) {
        return this._x;
    }

    /**
     * Returns the value of field 'y'.
     * 
     * @return the value of field 'Y'.
     */
    public java.math.BigDecimal getY(
    ) {
        return this._y;
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
     * Sets the value of field 'projectionType'.
     * 
     * @param projectionType the value of field 'projectionType'.
     */
    public void setProjectionType(
            final java.lang.String projectionType) {
        this._projectionType = projectionType;
    }

    /**
     * Sets the value of field 'x'.
     * 
     * @param x the value of field 'x'.
     */
    public void setX(
            final java.math.BigDecimal x) {
        this._x = x;
    }

    /**
     * Sets the value of field 'y'.
     * 
     * @param y the value of field 'y'.
     */
    public void setY(
            final java.math.BigDecimal y) {
        this._y = y;
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
