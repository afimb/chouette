package fr.certu.chouette.model.neptune;

import lombok.Getter;
import lombok.Setter;
/**
 * 
 * @author mamadou keira
 *
 */
public class RestrictionConstraint extends NeptuneObject{
	private static final long serialVersionUID = 8284408951111709673L;
	/**
     * Field areaId.
     */
    @Getter @Setter private String areaId;
    @Getter @Setter private StopArea stopArea;
    /**
     * Field lineIdShortCut.
     */
    @Getter @Setter private String lineIdShortCut;
    @Getter @Setter private Line line;
    /**
     * Field name.
     */
    @Getter @Setter private String name;
}
