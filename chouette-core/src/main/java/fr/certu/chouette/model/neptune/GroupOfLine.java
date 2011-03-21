package fr.certu.chouette.model.neptune;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class GroupOfLine extends NeptuneIdentifiedObject{

	private static final long serialVersionUID = 2900948915585746984L;
	@Getter @Setter private List<String> lineIds;
	@Getter @Setter private List<Line> lines;
	@Getter @Setter private String comment;
}
