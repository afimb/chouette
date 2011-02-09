package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

import lombok.Getter;
import lombok.Setter;

public class ConnectionLink extends NeptuneIdentifiedObject
{
	@Getter @Setter private String comment;
	@Getter @Setter private BigDecimal linkDistance;
	@Getter @Setter private String startOfLinkId;
	@Getter @Setter private StopArea startOfLink;
	@Getter @Setter private String endOfLinkId;
	@Getter @Setter private StopArea endOfLink;
	@Getter @Setter boolean liftAvailable;
	@Getter @Setter boolean mobilityRestrictedSuitable;
	@Getter @Setter boolean stairsAvailable;
	@Getter @Setter List<UserNeedEnum> userNeeds;
	@Getter @Setter private Date defaultDuration;
	@Getter @Setter private Date frequentTravellerDuration;
	@Getter @Setter private Date occasionalTravellerDuration;
	@Getter @Setter private Date mobilityRestrictedTravellerDuration;
	@Getter @Setter private ConnectionLinkTypeEnum linkType; 
	
	public void addUserNeed(UserNeedEnum userNeed)
	{
		if (userNeeds == null) userNeeds = new ArrayList<UserNeedEnum>();
		userNeeds.add(userNeed);
	}

	
	@Override
	public String toString(String indent, int level) {
		StringBuilder sb = new StringBuilder(super.toString(indent,level));
		sb.append("\n").append(indent).append("  startOfLinkId = ").append(startOfLinkId);
		sb.append("\n").append(indent).append("  endOfLinkId = ").append(endOfLinkId);
		if(linkDistance != null){
			sb.append("\n").append(indent).append("  linkDistance = ").append(linkDistance.toPlainString());			
		}
		sb.append("\n").append(indent).append("  comment = ").append(comment);
		sb.append("\n").append(indent).append("  liftAvailable = ").append(liftAvailable);
		sb.append("\n").append(indent).append("  mobilityRestrictedSuitable = ").append(mobilityRestrictedSuitable);
		sb.append("\n").append(indent).append("  stairsAvailable = ").append(stairsAvailable);
		sb.append("\n").append(indent).append("  defaultDuration = ").append(formatDate(defaultDuration));
		sb.append("\n").append(indent).append("  frequentTravellerDuration = ").append(formatDate(frequentTravellerDuration));
		sb.append("\n").append(indent).append("  occasionalTravellerDuration = ").append(formatDate(occasionalTravellerDuration));
		sb.append("\n").append(indent).append("  mobilityRestrictedTravellerDuration = ").append(formatDate(mobilityRestrictedTravellerDuration));
		sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(linkType);
		
		if(userNeeds != null){
			sb.append("\n").append(indent).append(CHILD_ARROW).append("userNeeds");
			for (UserNeedEnum userNeed : getUserNeeds())
			{
				sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(userNeed);
			}
		}
		
		if (level > 0)
		{
			String childIndent = indent + CHILD_INDENT;
			if (startOfLink != null) 
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(startOfLink.toString(childIndent,0));
			}
			if (endOfLink != null) 
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(endOfLink.toString(childIndent,0));
			}
		}	

		return sb.toString();
	}
	
	private String formatDate(Date date){
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		if(date != null){
			return dateFormat.format(date);
		}
		else{
			return null;
		}
	}
}
