/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import fr.certu.chouette.model.neptune.Company;

/**
 * @author michel
 *
 */
public class CompanyManager extends AbstractNeptuneManager<Company> 
{

	public CompanyManager()
	{
		super(Company.class);
	}

	

}
