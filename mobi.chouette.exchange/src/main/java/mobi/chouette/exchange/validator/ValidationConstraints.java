package mobi.chouette.exchange.validator;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.exchange.validator.report.CheckPoint;

public class ValidationConstraints {

	@Getter
	@Setter
	private List<CheckPoint> checkpoints = new ArrayList<CheckPoint>();

}
