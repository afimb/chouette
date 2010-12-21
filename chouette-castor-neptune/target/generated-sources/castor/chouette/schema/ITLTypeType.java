/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Interdiction de trafic local
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class ITLTypeType extends chouette.schema.castor.SchemaObject 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _areaId.
     */
    private java.lang.String _areaId;

    /**
     * Field _lineIdShortCut.
     */
    private java.lang.String _lineIdShortCut;

    /**
     * Field _name.
     */
    private java.lang.String _name;


      //----------------/
     //- Constructors -/
    //----------------/

    public ITLTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'areaId'.
     * 
     * @return the value of field 'AreaId'.
     */
    public java.lang.String getAreaId(
    ) {
        return this._areaId;
    }

    /**
     * Returns the value of field 'lineIdShortCut'.
     * 
     * @return the value of field 'LineIdShortCut'.
     */
    public java.lang.String getLineIdShortCut(
    ) {
        return this._lineIdShortCut;
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
     * Sets the value of field 'areaId'.
     * 
     * @param areaId the value of field 'areaId'.
     */
    public void setAreaId(
            final java.lang.String areaId) {
        this._areaId = areaId;
    }

    /**
     * Sets the value of field 'lineIdShortCut'.
     * 
     * @param lineIdShortCut the value of field 'lineIdShortCut'.
     */
    public void setLineIdShortCut(
            final java.lang.String lineIdShortCut) {
        this._lineIdShortCut = lineIdShortCut;
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
