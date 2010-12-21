/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package amivif.schema;

/**
 * PT Operator description
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public abstract class CompanyTypeType extends amivif.schema.TridentObjectTypeType 
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
     * Field _shortName.
     */
    private java.lang.String _shortName;

    /**
     * Field _organisationalUnit.
     */
    private java.lang.String _organisationalUnit;

    /**
     * Field _operatingDepartmentName.
     */
    private java.lang.String _operatingDepartmentName;

    /**
     * Field _code.
     */
    private java.lang.String _code;

    /**
     * Field _phone.
     */
    private java.lang.String _phone;

    /**
     * Field _fax.
     */
    private java.lang.String _fax;

    /**
     * Field _email.
     */
    private java.lang.String _email;

    /**
     * Field _registration.
     */
    private amivif.schema.Registration _registration;


      //----------------/
     //- Constructors -/
    //----------------/

    public CompanyTypeType() {
        super();
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'code'.
     * 
     * @return the value of field 'Code'.
     */
    public java.lang.String getCode(
    ) {
        return this._code;
    }

    /**
     * Returns the value of field 'email'.
     * 
     * @return the value of field 'Email'.
     */
    public java.lang.String getEmail(
    ) {
        return this._email;
    }

    /**
     * Returns the value of field 'fax'.
     * 
     * @return the value of field 'Fax'.
     */
    public java.lang.String getFax(
    ) {
        return this._fax;
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
     * Returns the value of field 'operatingDepartmentName'.
     * 
     * @return the value of field 'OperatingDepartmentName'.
     */
    public java.lang.String getOperatingDepartmentName(
    ) {
        return this._operatingDepartmentName;
    }

    /**
     * Returns the value of field 'organisationalUnit'.
     * 
     * @return the value of field 'OrganisationalUnit'.
     */
    public java.lang.String getOrganisationalUnit(
    ) {
        return this._organisationalUnit;
    }

    /**
     * Returns the value of field 'phone'.
     * 
     * @return the value of field 'Phone'.
     */
    public java.lang.String getPhone(
    ) {
        return this._phone;
    }

    /**
     * Returns the value of field 'registration'.
     * 
     * @return the value of field 'Registration'.
     */
    public amivif.schema.Registration getRegistration(
    ) {
        return this._registration;
    }

    /**
     * Returns the value of field 'shortName'.
     * 
     * @return the value of field 'ShortName'.
     */
    public java.lang.String getShortName(
    ) {
        return this._shortName;
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
     * Sets the value of field 'code'.
     * 
     * @param code the value of field 'code'.
     */
    public void setCode(
            final java.lang.String code) {
        this._code = code;
    }

    /**
     * Sets the value of field 'email'.
     * 
     * @param email the value of field 'email'.
     */
    public void setEmail(
            final java.lang.String email) {
        this._email = email;
    }

    /**
     * Sets the value of field 'fax'.
     * 
     * @param fax the value of field 'fax'.
     */
    public void setFax(
            final java.lang.String fax) {
        this._fax = fax;
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
     * Sets the value of field 'operatingDepartmentName'.
     * 
     * @param operatingDepartmentName the value of field
     * 'operatingDepartmentName'.
     */
    public void setOperatingDepartmentName(
            final java.lang.String operatingDepartmentName) {
        this._operatingDepartmentName = operatingDepartmentName;
    }

    /**
     * Sets the value of field 'organisationalUnit'.
     * 
     * @param organisationalUnit the value of field
     * 'organisationalUnit'.
     */
    public void setOrganisationalUnit(
            final java.lang.String organisationalUnit) {
        this._organisationalUnit = organisationalUnit;
    }

    /**
     * Sets the value of field 'phone'.
     * 
     * @param phone the value of field 'phone'.
     */
    public void setPhone(
            final java.lang.String phone) {
        this._phone = phone;
    }

    /**
     * Sets the value of field 'registration'.
     * 
     * @param registration the value of field 'registration'.
     */
    public void setRegistration(
            final amivif.schema.Registration registration) {
        this._registration = registration;
    }

    /**
     * Sets the value of field 'shortName'.
     * 
     * @param shortName the value of field 'shortName'.
     */
    public void setShortName(
            final java.lang.String shortName) {
        this._shortName = shortName;
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
