/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * A General Link Between two Points (or object inheriting 
 * from Point)
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class GeneralLinkTypeType extends amivif.schema.LocationTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name.
     */
    private java.lang.String _name;

    /**
     * Field _startOfLink.
     */
    private java.lang.String _startOfLink;

    /**
     * Field _endOfLink.
     */
    private java.lang.String _endOfLink;

    /**
     * Field _linkDistance.
     */
    private java.math.BigDecimal _linkDistance;


      //----------------/
     //- Constructors -/
    //----------------/

    public GeneralLinkTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'endOfLink'.
     * 
     * @return the value of field 'EndOfLink'.
     */
    public java.lang.String getEndOfLink(
    ) {
        return this._endOfLink;
    }

    /**
     * Returns the value of field 'linkDistance'.
     * 
     * @return the value of field 'LinkDistance'.
     */
    public java.math.BigDecimal getLinkDistance(
    ) {
        return this._linkDistance;
    }

    /**
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'Name'.
     */
    public java.lang.String getName(
    ) {
        return this._name;
    }

    /**
     * Returns the value of field 'startOfLink'.
     * 
     * @return the value of field 'StartOfLink'.
     */
    public java.lang.String getStartOfLink(
    ) {
        return this._startOfLink;
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
     * Sets the value of field 'endOfLink'.
     * 
     * @param endOfLink the value of field 'endOfLink'.
     */
    public void setEndOfLink(
            final java.lang.String endOfLink) {
        this._endOfLink = endOfLink;
    }

    /**
     * Sets the value of field 'linkDistance'.
     * 
     * @param linkDistance the value of field 'linkDistance'.
     */
    public void setLinkDistance(
            final java.math.BigDecimal linkDistance) {
        this._linkDistance = linkDistance;
    }

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(
            final java.lang.String name) {
        this._name = name;
    }

    /**
     * Sets the value of field 'startOfLink'.
     * 
     * @param startOfLink the value of field 'startOfLink'.
     */
    public void setStartOfLink(
            final java.lang.String startOfLink) {
        this._startOfLink = startOfLink;
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
