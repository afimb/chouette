/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema;

/**
 * Class AccessibilitySuitabilityDetailsItem.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class AccessibilitySuitabilityDetailsItem implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _userNeedGroup.
     */
    private chouette.schema.UserNeedGroup _userNeedGroup;


      //----------------/
     //- Constructors -/
    //----------------/

    public AccessibilitySuitabilityDetailsItem() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'userNeedGroup'.
     * 
     * @return the value of field 'UserNeedGroup'.
     */
    public chouette.schema.UserNeedGroup getUserNeedGroup(
    ) {
        return this._userNeedGroup;
    }

    /**
     * Sets the value of field 'userNeedGroup'.
     * 
     * @param userNeedGroup the value of field 'userNeedGroup'.
     */
    public void setUserNeedGroup(
            final chouette.schema.UserNeedGroup userNeedGroup) {
        this._userNeedGroup = userNeedGroup;
    }

}
