/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * StopPoint on a Route of a Line of a PT Network
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class StopPointTypeType extends amivif.schema.PointTypeType 
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
     * Field _lineIdShortcut.
     */
    private java.lang.String _lineIdShortcut;

    /**
     * Field _ptNetworkIdShortcut.
     */
    private java.lang.String _ptNetworkIdShortcut;

    /**
     * Field _comment.
     */
    private java.lang.String _comment;

    /**
     * Extensions AMIVIF sur les points d'arret
     */
    private amivif.schema.AMIVIF_StopPoint_Extension _AMIVIF_StopPoint_Extension;


      //----------------/
     //- Constructors -/
    //----------------/

    public StopPointTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'AMIVIF_StopPoint_Extension'. The
     * field 'AMIVIF_StopPoint_Extension' has the following
     * description: Extensions AMIVIF sur les points d'arret
     * 
     * @return the value of field 'AMIVIF_StopPoint_Extension'.
     */
    public amivif.schema.AMIVIF_StopPoint_Extension getAMIVIF_StopPoint_Extension(
    ) {
        return this._AMIVIF_StopPoint_Extension;
    }

    /**
     * Returns the value of field 'comment'.
     * 
     * @return the value of field 'Comment'.
     */
    public java.lang.String getComment(
    ) {
        return this._comment;
    }

    /**
     * Returns the value of field 'lineIdShortcut'.
     * 
     * @return the value of field 'LineIdShortcut'.
     */
    public java.lang.String getLineIdShortcut(
    ) {
        return this._lineIdShortcut;
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
     * Returns the value of field 'ptNetworkIdShortcut'.
     * 
     * @return the value of field 'PtNetworkIdShortcut'.
     */
    public java.lang.String getPtNetworkIdShortcut(
    ) {
        return this._ptNetworkIdShortcut;
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
     * Sets the value of field 'AMIVIF_StopPoint_Extension'. The
     * field 'AMIVIF_StopPoint_Extension' has the following
     * description: Extensions AMIVIF sur les points d'arret
     * 
     * @param AMIVIF_StopPoint_Extension the value of field
     * 'AMIVIF_StopPoint_Extension'.
     */
    public void setAMIVIF_StopPoint_Extension(
            final amivif.schema.AMIVIF_StopPoint_Extension AMIVIF_StopPoint_Extension) {
        this._AMIVIF_StopPoint_Extension = AMIVIF_StopPoint_Extension;
    }

    /**
     * Sets the value of field 'comment'.
     * 
     * @param comment the value of field 'comment'.
     */
    public void setComment(
            final java.lang.String comment) {
        this._comment = comment;
    }

    /**
     * Sets the value of field 'lineIdShortcut'.
     * 
     * @param lineIdShortcut the value of field 'lineIdShortcut'.
     */
    public void setLineIdShortcut(
            final java.lang.String lineIdShortcut) {
        this._lineIdShortcut = lineIdShortcut;
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
     * Sets the value of field 'ptNetworkIdShortcut'.
     * 
     * @param ptNetworkIdShortcut the value of field
     * 'ptNetworkIdShortcut'.
     */
    public void setPtNetworkIdShortcut(
            final java.lang.String ptNetworkIdShortcut) {
        this._ptNetworkIdShortcut = ptNetworkIdShortcut;
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
