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

import fr.certu.chouette.filter.DetailLevelEnum;
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
	/**
	 * 
	 */
	@Getter @Setter private Date versionDate; // BD
	@Getter @Setter private String description; // BD
	@Getter @Setter private String registrationNumber; // BD
	@Getter @Setter private String sourceName; // BD 
	@Getter @Setter private String sourceIdentifier; // BD
	@Getter @Setter private String comment; // BD 
	@Getter @Setter private PTNetworkSourceTypeEnum 	pTNetworkSourceType; // Ajout BD
	@Getter @Setter private List<String>				lineIds; // FK inverse non alimenté par la BD TODO à voir


	/* (non-Javadoc)
	 * @see fr.certu.chouette.model.neptune.NeptuneBean#expand(fr.certu.chouette.manager.NeptuneBeanManager.DETAIL_LEVEL)
	 */
	@Override
	public void expand(DetailLevelEnum level)
	{
		// to avoid circular call check if level is already set according to this level
		if (getLevel().ordinal() >= level.ordinal()) return;
		super.expand(level);
		switch (level)
		{
		case ATTRIBUTE : 
			break;
		case NARROW_DEPENDENCIES :
			break;
		case STRUCTURAL_DEPENDENCIES :
			break;
		case ALL_DEPENDENCIES :
		}
	} 

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
