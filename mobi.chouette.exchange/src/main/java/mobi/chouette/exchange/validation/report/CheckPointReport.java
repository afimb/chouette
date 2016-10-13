package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mobi.chouette.exchange.report.AbstractReport;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import javax.xml.bind.annotation.*;


@Data
@EqualsAndHashCode(callSuper=false)
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "phase", "target", "rank", "severity", "state", "checkPointErrorCount", "checkPointErrorsKeys" })
public class CheckPointReport extends AbstractReport{

	public enum SEVERITY {
		WARNING, ERROR, IMPROVMENT
	};

	@XmlElement(name = "test_id", required = true)
	private String name;

	@XmlElement(name = "level", required = true)
	private String phase;

	@XmlElement(name = "object_type", required = true)
	private String target;

	@XmlElement(name = "rank", required = true)
	private String rank;

	@XmlElement(name = "severity", required = true)
	private SEVERITY severity;

	@XmlElement(name = "result", required = true)
	private RESULT state;

	@XmlElement(name = "error_count")
	private int checkPointErrorCount = 0;

	@XmlElement(name = "errors")
	private List<Integer> checkPointErrorsKeys = new ArrayList<>();

	@XmlTransient
	private boolean maxByFile = true;

	protected CheckPointReport(String name, RESULT state, SEVERITY severity) {
		this.name = name;
		this.severity = severity;
		this.state = state;

		String[] token = name.split("\\-");
		if (token.length >= 4) {
			this.phase = token[0];
			this.target = token[2];
			this.rank = token[3];
		} else if (token.length == 3) {
			this.phase = token[0];
			this.target = token[1];
			this.rank = token[2];
		} else {
			throw new IllegalArgumentException("invalid name " + name);
		}
	}

	protected boolean addCheckPointError(int checkPointErrorId) {
		boolean ret = false;
		if (maxByFile) {
			if (checkPointErrorCount < maxErrors) 
			{
				checkPointErrorsKeys.add(new Integer(checkPointErrorId));
				ret = true;
			}
		}
		checkPointErrorCount++;
		state = RESULT.NOK;
		return ret;
	}

}
