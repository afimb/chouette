/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 */
@NoArgsConstructor
public class PTNetwork extends NeptuneIdentifiedObject
{
	@Getter @Setter private Date versionDate;
	@Getter @Setter private String description;
	@Getter @Setter private String registrationNumber;
	@Getter @Setter private String sourceName;
	@Getter @Setter private String sourceIdentifier;
	@Getter @Setter private String comment;
	@Getter @Setter private PTNetworkSourceTypeEnum 	pTNetworkSourceType;
	@Getter @Setter private List<String>				lineIds;


	/* (non-Javadoc)
	 * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.lang.String, int)
	 */
	@Override
	public String toString(String indent,int level)
	{
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
		StringBuilder sb = new StringBuilder(super.toString(indent,level));
		if (versionDate != null)
			sb.append("\n").append(indent).append("versionDate = ").append(f.format(versionDate));
		sb.append("\n").append(indent).append("description = ").append(description);
		sb.append("\n").append(indent).append("registrationNumber = ").append(registrationNumber);
		sb.append("\n").append(indent).append("sourceName = ").append(sourceName);
		sb.append("\n").append(indent).append("sourceIdentifier = ").append(sourceIdentifier);
		sb.append("\n").append(indent).append("comment = ").append(comment);

		if (lineIds != null)
		{
			sb.append("\n").append(indent).append(CHILD_ARROW).append("lineIds");
			for (String lineId : lineIds)
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(lineId);
			}
		}
		
		return sb.toString();
	}

	public void addLineId(String lineId)
	{
		if (lineIds== null) lineIds = new ArrayList<String>();
		lineIds.add(lineId);
	}
	

	
}
