/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.plugin.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 */
@AllArgsConstructor
public class ValidationStepDescription
{
	@Getter @Setter private String name; 
	@Getter @Setter private int classRank;
	
}
