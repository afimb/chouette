/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * Chouette Company : a company providing public transport services.
 * <p/>
 * Neptune mapping : Company <br/>
 * Gtfs mapping : Agency <br/>
 */

@Entity
@Table(name = "companies")
@NoArgsConstructor
@Log4j
public class Company extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = -8086291270595894778L;

   /**
    * name
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "name")
   private String name;

   /**
    * set name <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setName(String value)
   {
      name = dataBaseSizeProtectedValue(value, "name", log);
   }

   /**
    * short name
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "short_name")
   private String shortName;

   /**
    * set short name <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setShortName(String value)
   {
      shortName = dataBaseSizeProtectedValue(value, "shortName", log);
   }

   /**
    * organizational unit
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "organizational_unit")
   private String organisationalUnit;

   /**
    * set organizational unit <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setOrganisationalUnit(String value)
   {
      organisationalUnit = dataBaseSizeProtectedValue(value, "organisationalUnit", log);
   }

   /**
    * operating department name
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "operating_department_name")
   private String operatingDepartmentName;

   /**
    * set operating department name <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setOperatingDepartmentName(String value)
   {
      operatingDepartmentName = dataBaseSizeProtectedValue(value, "operatingDepartmentName", log);
   }

   /**
    * organization code <br/>
    * usually fixed by Transport Authority
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "code")
   private String code;

   /**
    * set organization code <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setCode(String value)
   {
      code = dataBaseSizeProtectedValue(value, "code", log);
   }

   /**
    * phone number
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "phone")
   private String phone;

   /**
    * set phone number <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setPhone(String value)
   {
      phone = dataBaseSizeProtectedValue(value, "phone", log);
   }

   /**
    * fax number
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "fax")
   private String fax;

   /**
    * set fax number <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setFax(String value)
   {
      fax = dataBaseSizeProtectedValue(value, "fax", log);
   }

   /**
    * email
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "email")
   private String email;

   /**
    * set email <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setEmail(String value)
   {
      email = dataBaseSizeProtectedValue(value, "email", log);
   }

   /**
    * registration number
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "registration_number", unique = true)
   private String registrationNumber;

   /**
    * set registration number <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setRegistrationNumber(String value)
   {
      registrationNumber = dataBaseSizeProtectedValue(value, "registrationNumber", log);
   }

   /**
    * web site url
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "url")
   private String url;

   /**
    * set web site url <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setUrl(String value)
   {
      url = dataBaseSizeProtectedValue(value, "url", log);
   }

   /**
    * default timezone
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "time_zone")
   private String timeZone;

   /**
    * set default timezone <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setTimeZone(String value)
   {
      timeZone = dataBaseSizeProtectedValue(value, "timeZone", log);
   }

   /**
    * lines
    * 
    * @param lines
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   @OneToMany(mappedBy = "company")
   private List<Line> lines = new ArrayList<Line>(0);

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.
    * lang.String, int)
    */
   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("shortName = ").append(shortName);
      sb.append("\n").append(indent).append("organisationalUnit = ").append(organisationalUnit);
      sb.append("\n").append(indent).append("operatingDepartmentName = ").append(operatingDepartmentName);
      sb.append("\n").append(indent).append("code = ").append(code);
      sb.append("\n").append(indent).append("phone = ").append(phone);
      sb.append("\n").append(indent).append("fax = ").append(fax);
      sb.append("\n").append(indent).append("email = ").append(email);
      sb.append("\n").append(indent).append("fax = ").append(fax);
      sb.append("\n").append(indent).append("registrationNumber = ").append(registrationNumber);

      return sb.toString();
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.model.neptune.NeptuneObject#compareAttributes(fr.certu
    * .chouette.model.neptune.NeptuneObject)
    */
   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T anotherObject)
   {
      if (anotherObject instanceof Company)
      {
         Company another = (Company) anotherObject;
         if (!sameValue(this.getObjectId(), another.getObjectId()))
            return false;
         if (!sameValue(this.getObjectVersion(), another.getObjectVersion()))
            return false;
         if (!sameValue(this.getName(), another.getName()))
            return false;
         if (!sameValue(this.getRegistrationNumber(), another.getRegistrationNumber()))
            return false;

         if (!sameValue(this.getShortName(), another.getShortName()))
            return false;
         if (!sameValue(this.getCode(), another.getCode()))
            return false;
         if (!sameValue(this.getOrganisationalUnit(), another.getOrganisationalUnit()))
            return false;
         if (!sameValue(this.getOperatingDepartmentName(), another.getOperatingDepartmentName()))
            return false;
         if (!sameValue(this.getPhone(), another.getPhone()))
            return false;
         if (!sameValue(this.getFax(), another.getFax()))
            return false;
         if (!sameValue(this.getEmail(), another.getEmail()))
            return false;
         return true;
      }
      else
      {
         return false;
      }
   }

   @Override
   public String toURL()
   {
      return "companies/" + getId();
   }

}
