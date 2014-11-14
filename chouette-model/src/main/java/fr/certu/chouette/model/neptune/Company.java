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
 * Neptune Company
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */

@Entity
@Table(name = "companies")
@NoArgsConstructor
@Log4j
public class Company extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = -8086291270595894778L;

   @Getter
   @Column(name = "name", nullable = false)
   private String name;
   public void setName(String value)
   {
      name = dataBaseSizeProtectedValue(value,"name",log);
   }

   @Getter
   @Column(name = "short_name")
   private String shortName;
   public void setShortName(String value)
   {
      shortName = dataBaseSizeProtectedValue(value,"shortName",log);
   }

   @Getter
   @Column(name = "organizational_unit")
   private String organisationalUnit;
   public void setOrganisationalUnit(String value)
   {
      organisationalUnit = dataBaseSizeProtectedValue(value,"organisationalUnit",log);
   }

   @Getter
   @Column(name = "operating_department_name")
   private String operatingDepartmentName;
   public void setOperatingDepartmentName(String value)
   {
      operatingDepartmentName = dataBaseSizeProtectedValue(value,"operatingDepartmentName",log);
   }

   @Getter
   @Column(name = "code")
   private String code;
   public void setCode(String value)
   {
      code = dataBaseSizeProtectedValue(value,"code",log);
   }

   @Getter
   @Column(name = "phone")
   private String phone;
   public void setPhone(String value)
   {
      phone = dataBaseSizeProtectedValue(value,"phone",log);
   }

   @Getter
   @Column(name = "fax")
   private String fax;
   public void setFax(String value)
   {
      fax = dataBaseSizeProtectedValue(value,"fax",log);
   }

   @Getter
   @Column(name = "email")
   private String email;
   public void setEmail(String value)
   {
      email = dataBaseSizeProtectedValue(value,"email",log);
   }

   @Getter
   @Column(name = "registration_number", unique = true)
   private String registrationNumber;
   public void setRegistrationNumber(String value)
   {
      registrationNumber = dataBaseSizeProtectedValue(value,"registrationNumber",log);
   }

   @Getter
   @Column(name = "url")
   private String url;
   public void setUrl(String value)
   {
      url = dataBaseSizeProtectedValue(value,"url",log);
   }


   @Getter
   @Column(name = "time_zone")
   private String timeZone;
   public void setTimeZone(String value)
   {
      timeZone = dataBaseSizeProtectedValue(value,"timeZone",log);
   }

   @Getter
   @Setter
   @OneToMany(mappedBy = "company")
   private List<Line> lines = new ArrayList<Line>(0);

   @Override
   public String toString(String indent, int level)
   {
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      sb.append("\n").append(indent).append("shortName = ").append(shortName);
      sb.append("\n").append(indent).append("organisationalUnit = ")
            .append(organisationalUnit);
      sb.append("\n").append(indent).append("operatingDepartmentName = ")
            .append(operatingDepartmentName);
      sb.append("\n").append(indent).append("code = ").append(code);
      sb.append("\n").append(indent).append("phone = ").append(phone);
      sb.append("\n").append(indent).append("fax = ").append(fax);
      sb.append("\n").append(indent).append("email = ").append(email);
      sb.append("\n").append(indent).append("fax = ").append(fax);
      sb.append("\n").append(indent).append("registrationNumber = ")
            .append(registrationNumber);

      return sb.toString();
   }

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
         if (!sameValue(this.getRegistrationNumber(),
               another.getRegistrationNumber()))
            return false;

         if (!sameValue(this.getShortName(), another.getShortName()))
            return false;
         if (!sameValue(this.getCode(), another.getCode()))
            return false;
         if (!sameValue(this.getOrganisationalUnit(),
               another.getOrganisationalUnit()))
            return false;
         if (!sameValue(this.getOperatingDepartmentName(),
               another.getOperatingDepartmentName()))
            return false;
         if (!sameValue(this.getPhone(), another.getPhone()))
            return false;
         if (!sameValue(this.getFax(), another.getFax()))
            return false;
         if (!sameValue(this.getEmail(), another.getEmail()))
            return false;
         return true;
      } else
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
