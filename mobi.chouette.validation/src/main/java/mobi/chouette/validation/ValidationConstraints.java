package mobi.chouette.validation;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.validation.report.CheckPoint;

public class ValidationConstraints {

	@Getter
	@Setter
	private List<CheckPoint> checkpoints = new ArrayList<CheckPoint>();

}
