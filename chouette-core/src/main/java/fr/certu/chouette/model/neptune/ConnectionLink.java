package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

/**
 * Neptune ConnectionLink : a link between 2 StopArea 
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
public class ConnectionLink extends NeptuneIdentifiedObject
{
	private static final long serialVersionUID = 8490105295077539089L;
	/**
	 * Comment 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String comment;
	/**
	 * Link Distance in meters (To be confirmed) 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private BigDecimal linkDistance;
	/**
	 * Neptune Id for Start of Link StopArea 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String startOfLinkId;
	/**
	 * Start of Link StopArea 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private StopArea startOfLink;
	/**
	 * Neptune Id for End of Link StopArea 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private String endOfLinkId;
	/**
	 * End of Link StopArea 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private StopArea endOfLink;
	/**
	 * Indicate if a Lift is available 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter boolean liftAvailable;
	/**
	 * indicate if the link is equipped for mobility restricted persons 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter boolean mobilityRestrictedSuitable;
	/**
	 * indicate if stairs are present on the link 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter boolean stairsAvailable;
	/**
	 * give a list of specific User needs available
	 * <br/><i>readable/writable</i>
	 */
	private List<UserNeedEnum> userNeeds; //Never be persisted
	/**
	 * 
	 */
	@Getter @Setter private Integer intUserNeeds; //BD
	/**
	 * Duration of link 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private Date defaultDuration;
	/**
	 * Duration of link for frequent travelers 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private Date frequentTravellerDuration;
	/**
	 * Duration of link for occasional travelers 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private Date occasionalTravellerDuration;
	/**
	 * Duration of link for mobility restricted travelers 
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private Date mobilityRestrictedTravellerDuration;
	/**
	 * Link type
	 * <br/><i>readable/writable</i>
	 */
	@Getter @Setter private ConnectionLinkTypeEnum linkType; 

	public void addUserNeed(UserNeedEnum userNeed)
	{
		if (userNeeds == null) userNeeds = new ArrayList<UserNeedEnum>();
		userNeeds.add(userNeed);
		synchronizeUserNeeds();
	}

	public void removeUserNeed(UserNeedEnum userNeed){
		if(userNeeds != null)
			userNeeds.remove(userNeed);
		synchronizeUserNeeds();
	}
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
			startOfLink = null;
			endOfLink = null;
			break;
		case NARROW_DEPENDENCIES : 
		case STRUCTURAL_DEPENDENCIES : 
		case ALL_DEPENDENCIES :
			if (getStartOfLink() != null) getStartOfLink().expand(DetailLevelEnum.ATTRIBUTE);
			if (getEndOfLink() != null) getEndOfLink().expand(DetailLevelEnum.ATTRIBUTE);
		}
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
		sb.append("\n").append(indent).append("  linkType = ").append(linkType);

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

	public List<UserNeedEnum> getUserNeeds() 
	{
		if (intUserNeeds == null) return userNeeds;	
		//CASTOREVO
		UserNeedEnum[] userNeedEnums = UserNeedEnum.values();
		for (UserNeedEnum userNeedEnum : userNeedEnums) 
		{
			int filtre = (int) Math.pow(2, userNeedEnum.ordinal());
			if (filtre == (intUserNeeds.intValue() & filtre))
			{
				addUserNeed(userNeedEnum);
			}
		}	
		return userNeeds;
	}

	public void setUserNeeds(List<UserNeedEnum> userNeedEnums)
	{
		userNeeds = userNeedEnums;
		//CASTOREVO
		synchronizeUserNeeds();
	}

	private void synchronizeUserNeeds() {
		intUserNeeds = 0;
		if (userNeeds == null) return;

		for (UserNeedEnum userNeedEnum : userNeeds) 
		{
			intUserNeeds += (int)Math.pow(2, userNeedEnum.ordinal());
		}
	}
}
