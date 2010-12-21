/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * A PT area made up of a set of PT Stop Points
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class StopAreaTypeType extends amivif.schema.AreaTypeType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _comment.
     */
    private java.lang.String _comment;

    /**
     * Extensions AMIVIF sur les Aires de transport
     */
    private amivif.schema.AMIVIF_StopArea_Extension _AMIVIF_StopArea_Extension;


      //----------------/
     //- Constructors -/
    //----------------/

    public StopAreaTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'AMIVIF_StopArea_Extension'. The
     * field 'AMIVIF_StopArea_Extension' has the following
     * description: Extensions AMIVIF sur les Aires de transport
     * 
     * @return the value of field 'AMIVIF_StopArea_Extension'.
     */
    public amivif.schema.AMIVIF_StopArea_Extension getAMIVIF_StopArea_Extension(
    ) {
        return this._AMIVIF_StopArea_Extension;
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
     * Sets the value of field 'AMIVIF_StopArea_Extension'. The
     * field 'AMIVIF_StopArea_Extension' has the following
     * description: Extensions AMIVIF sur les Aires de transport
     * 
     * @param AMIVIF_StopArea_Extension the value of field
     * 'AMIVIF_StopArea_Extension'.
     */
    public void setAMIVIF_StopArea_Extension(
            final amivif.schema.AMIVIF_StopArea_Extension AMIVIF_StopArea_Extension) {
        this._AMIVIF_StopArea_Extension = AMIVIF_StopArea_Extension;
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
