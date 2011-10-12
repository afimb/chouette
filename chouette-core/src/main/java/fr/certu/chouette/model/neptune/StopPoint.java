package fr.certu.chouette.model.neptune;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

public class StopPoint extends NeptuneIdentifiedObject
{
	private static final long serialVersionUID = -4913573673645997423L;
	
	private static final Logger logger = Logger.getLogger(StopPoint.class);
	
	@Getter @Setter private Address address;
	@Getter @Setter private LongLatTypeEnum longLatType;
	@Getter @Setter private BigDecimal latitude;
	@Getter @Setter private BigDecimal longitude;
	@Getter @Setter private ProjectedPoint projectedPoint;
	@Getter @Setter private String comment;
	@Getter @Setter private String containedInStopAreaId;
	@Getter @Setter private StopArea containedInStopArea;
	@Getter @Setter private String lineIdShortcut;
	@Getter @Setter private Line line;
	@Getter @Setter private String ptNetworkIdShortcut;
	@Getter @Setter private PTNetwork ptNetwork;
	@Getter @Setter private int position;
	@Getter @Setter private Route route;
	
	@Getter @Setter private List<Facility> facilities;

	public void addFacility(Facility facility)
	{
		if(facilities == null) facilities = new ArrayList<Facility>();
		if(!facilities.contains(facility)) facilities.add(facility);
	}
	
	public void removeFacility(Facility facility)
	{
		if(facilities == null) facilities = new ArrayList<Facility>();
		if(facilities.contains(facility)) facilities.remove(facility);
	}

	@Override
	public String toString(String indent,int level)
	{
		StringBuilder sb = new StringBuilder(super.toString(indent,level));

		if (address != null) {
			sb.append("\n").append(indent).append("  address = ").append(address);			
		}

		if(longLatType != null){
			sb.append("\n").append(indent).append("  longLatType = ").append(longLatType);			
		}

		sb.append("\n").append(indent).append("  latitude = ").append(latitude);
		sb.append("\n").append(indent).append("  longitude = ").append(longitude);

		if(projectedPoint != null){
			sb.append("\n").append(indent).append("  projectedPoint = ").append(projectedPoint);
		}

		sb.append("\n").append(indent).append("  comment = ").append(comment);
		sb.append("\n").append(indent).append("  containedInStopAreaId = ").append(containedInStopAreaId);
		sb.append("\n").append(indent).append("  lineIdShortcut = ").append(lineIdShortcut);
		sb.append("\n").append(indent).append("  ptNetworkIdShortcut = ").append(ptNetworkIdShortcut);

		if (level > 0)
		{
			int childLevel = level -1;
			String childIndent = indent + CHILD_INDENT;
			if (containedInStopArea != null) 
			{
				sb.append("\n").append(indent).append(CHILD_ARROW).append(containedInStopArea.toString(childIndent,childLevel));
			}
		}

		return sb.toString();
	}
	
   /* (non-Javadoc)
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#complete()
    */
   @Override
   public void complete()
   {
      if (isCompleted()) return;
      super.complete();
      PTNetwork ptNetwork = getPtNetwork();
      if(ptNetwork != null)
         setPtNetworkIdShortcut(ptNetwork.getObjectId());
      Line line = getLine();
      if(line != null)
         setLineIdShortcut(line.getObjectId());
      StopArea area = getContainedInStopArea();   
      if (area != null)
      {
         area.complete();
         AreaCentroid centroid = area.getAreaCentroid();
         if (centroid != null)
         {
            setLatitude(centroid.getLatitude());
            setLongitude(centroid.getLongitude());
            setLongLatType(centroid.getLongLatType());
            setProjectedPoint(centroid.getProjectedPoint());
         }
         else
         {
            logger.error("stopPoint "+getObjectId()+" has an area without centroid "+area.getObjectId()); 
         }
         setName(area.getName());
      }
   }
}
