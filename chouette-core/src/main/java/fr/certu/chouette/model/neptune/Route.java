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
		StringBuilder sb = new StringBuilder(super.toString(indent,level));
		sb.append("\n").append(indent).append("oppositeRouteId = ").append(oppositeRouteId);
		sb.append("\n").append(indent).append("lineId = ").append(lineId);
		sb.append("\n").append(indent).append("publishedName = ").append(publishedName);
		sb.append("\n").append(indent).append("number = ").append(number);
		sb.append("\n").append(indent).append("direction = ").append(direction);
		sb.append("\n").append(indent).append("comment = ").append(comment);
		sb.append("\n").append(indent).append("wayBack = ").append(wayBack);

		return sb.toString();
	}

}
