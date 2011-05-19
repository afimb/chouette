package fr.certu.chouette.model.neptune;

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
    @Getter @Setter private String areaId;
    @Getter @Setter private List<StopArea> stopAreas;
    /**
     * Field lineIdShortCut.
     */
    @Getter @Setter private String lineIdShortCut;
    @Getter @Setter private Line line;
}