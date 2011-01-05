/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 
 */
@NoArgsConstructor
public class Company extends NeptuneIdentifiedObject
{
	@Getter @Setter private String shortName;
	@Getter @Setter private String organisationalUnit;
	@Getter @Setter private String operatingDepartmentName;
	@Getter @Setter private String code;
	@Getter @Setter private String phone;
	@Getter @Setter private String fax;
	@Getter @Setter private String email;
	@Getter @Setter private String registrationNumber;

	/* (non-Javadoc)
	 * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.lang.String, int)
	 */
	@Override
	public String toString(String indent,int level)
	{
		String s = super.toString(indent,level);
		s += "\n"+indent+"shortName = "+shortName;
		s += "\n"+indent+"organisationalUnit = "+organisationalUnit;
		s += "\n"+indent+"operatingDepartmentName = "+operatingDepartmentName;
		s += "\n"+indent+"code = "+code;
		s += "\n"+indent+"phone = "+phone;
		s += "\n"+indent+"fax = "+fax;
		s += "\n"+indent+"email = "+email;
		s += "\n"+indent+"fax = "+fax;
		s += "\n"+indent+"registrationNumber = "+registrationNumber;

		return s;
	}

}
