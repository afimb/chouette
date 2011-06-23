package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
/**
 * 
 * @author mamadou keira
 *
 */
public class RestrictionConstraint extends NeptuneIdentifiedObject{
	private static final long serialVersionUID = 8284408951111709673L;
	/**
     * Field areaId.
     */
    @Getter @Setter private List<String> areaIds;
    @Getter @Setter private List<StopArea> stopAreas;
    /**
     * Field lineIdShortCut.
     */
    @Getter @Setter private String lineIdShortCut;
    @Getter @Setter private Line line;
    
    public void addAreaId(String areaId)
    {
    	if (areaIds == null) areaIds = new ArrayList<String>();
    	if (areaId != null && !areaIds.contains(areaId))
    	{
    		areaIds.add(areaId);
    	}
    }
    public void removeAreaId(String areaId)
    {
    	if (areaIds == null) areaIds = new ArrayList<String>();
    	if (areaId != null && areaIds.contains(areaId))
    	{
    		areaIds.remove(areaId);
    	}
    }
    public void addArea(StopArea area)
    {
    	if (stopAreas == null) stopAreas = new ArrayList<StopArea>();
    	if (area != null && !stopAreas.contains(area))
    	{
    		stopAreas.add(area);
    	}
    }
    public void removeArea(StopArea area)
    {
    	if (stopAreas == null) stopAreas = new ArrayList<StopArea>();
    	if (area != null && stopAreas.contains(area))
    	{
    		stopAreas.remove(area);
    	}
    }

    
    
}