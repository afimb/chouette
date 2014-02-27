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
   @Setter
   @Column(name = "short_name")
   private String shortName;

   @Getter
   @Setter
   @Column(name = "organizational_unit")
   private String organisationalUnit;

   @Getter
   @Setter
   @Column(name = "operating_department_name")
   private String operatingDepartmentName;

   @Getter
   @Setter
   @Column(name = "code")
   private String code;

   @Getter
   @Setter
   @Column(name = "phone")
   private String phone;

   @Getter
   @Setter
   @Column(name = "fax")
   private String fax;

   @Getter
   @Setter
   @Column(name = "email")
   private String email;

   @Getter
   @Column(name = "registration_number", nullable = false, unique = true)
   private String registrationNumber;

   @Getter
   @Setter
   @OneToMany(mappedBy = "company")
   private List<Line> lines = new ArrayList<Line>(0);

   public void setRegistrationNumber(String value)
   {
      if (value != null && value.length() > 255)
      {
         log.warn("registrationNumber too long, truncated " + value);
         registrationNumber = value.substring(0, 255);
      }
      else
      {
         registrationNumber = value;
      }
   }

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

   @Override
   public <T extends NeptuneObject> boolean compareAttributes(
         T anotherObject)
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
