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
public class GroupOfLine extends NeptuneIdentifiedObject{

	private static final long serialVersionUID = 2900948915585746984L;
	@Getter @Setter private List<String> lineIds;
	@Getter @Setter private List<Line> lines;
	@Getter @Setter private String comment;
	
	/**
	 * add a lineId to list only if not already present
	 * @param lineId
	 */
	public void addLineId(String lineId){
		if(lineIds == null) lineIds = new ArrayList<String>();
		lineIds.add(lineId);
	}
	/**
	 * add a line to list only if not already present
	 * @param line
	 */
	public void addLine(Line line){
		if(lines == null) lines = new ArrayList<Line>();
		lines.add(line);
	}
	
}
