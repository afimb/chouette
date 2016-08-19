package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mobi.chouette.exchange.report.AbstractReport;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "key", "testId", "source", "targets", "value", "referenceValue" })
public class CheckPointErrorReport extends AbstractReport {

	@XmlElement(name = "test_id")
	private String testId;

	@XmlElement(name = "source")
	private Location source;

	@XmlElement(name = "target")
	private List<Location> targets = new ArrayList<>();

	@XmlElement(name = "error_id", required = true)
	private String key;

	@XmlElement(name = "error_value")
	private String value = "";

	@XmlElement(name = "reference_value")
	private String referenceValue = "";

	protected CheckPointErrorReport(String testId, String key, Location source) {
		setKey(key.replaceAll("-", "_").toLowerCase());
		this.source = source;
		this.testId = testId;

	}

	protected CheckPointErrorReport(String testId, String key, Location source, String value) {
		this(testId, key, source);
		this.value = value;

	}

	protected CheckPointErrorReport(String testId, String key, Location source, String value, String refValue) {
		this(testId, key, source, value);
		this.referenceValue = refValue;

	}

	protected CheckPointErrorReport(String testId, String key, Location source, Location... targets) {
		this(testId, key, source);
		this.getTargets().addAll(Arrays.asList(targets));
	}

	protected CheckPointErrorReport(String testId, String key, Location source, String value, Location... targets) {
		this(testId, key, source, value);
		this.getTargets().addAll(Arrays.asList(targets));
	}

	protected CheckPointErrorReport(String testId, String key, Location source, String value, String refValue,
			Location... targets) {
		this(testId, key, source, value, refValue);
		this.getTargets().addAll(Arrays.asList(targets));
	}


	@Override
	public void print(PrintStream out, StringBuilder ret , int level, boolean first) {
		ret.setLength(0);
		out.print(addLevel(ret, level).append('{'));
		out.print(toJsonString(ret, level+1, "test_id", testId, true));
		out.print(toJsonString(ret, level+1, "error_id", key, false));
		if (source != null)
			printObject(out, ret, level+1, "source", source, false);
		if (!targets.isEmpty()) {
			printArray(out, ret, level + 1, "target", targets, false);
		}
		if (value != null) {
			out.print(toJsonString(ret, level+1, "error_value", value, false));
		}
		if (referenceValue != null) {
			out.print(toJsonString(ret, level+1, "reference_value", referenceValue, false));
		}
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'), level).append('}'));

	}
}
