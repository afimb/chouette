/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import fr.certu.chouette.model.neptune.type.PTDirectionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 
 */
@NoArgsConstructor
public class Route extends NeptuneIdentifiedObject
{
	@Getter @Setter private Long oppositeRouteId;
	@Getter @Setter private Long lineId;
	@Getter @Setter private String publishedName;
	@Getter @Setter private String number; 
	@Getter @Setter private PTDirectionEnum direction;
	@Getter @Setter private String comment;
	@Getter @Setter private String wayBack;

	/* (non-Javadoc)
	 * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.lang.String, int)
	 */
	@Override
	public String toString(String indent,int level)
	{
		String s = super.toString(indent,level);
		s += "\n"+indent+"oppositeRouteId = "+oppositeRouteId;
		s += "\n"+indent+"lineId = "+lineId;
		s += "\n"+indent+"publishedName = "+publishedName;
		s += "\n"+indent+"number = "+number;
		s += "\n"+indent+"direction = "+direction;
		s += "\n"+indent+"comment = "+comment;
		s += "\n"+indent+"wayBack = "+wayBack;

		return s;
	}

}
