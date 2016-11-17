package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mobi.chouette.exchange.report.AbstractReport;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;


@Data
@EqualsAndHashCode(callSuper=false)
@ToString
public class CheckPointReport extends AbstractReport{

	public enum SEVERITY {
		WARNING, ERROR, IMPROVMENT
	};

	private String name;

	private String phase;

	private String target;

	private String rank;

	private SEVERITY severity;

	private RESULT state;

	private int checkPointErrorCount = 0;

	private List<Integer> checkPointErrorsKeys = new ArrayList<Integer>();

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


	@Override
	public void print(PrintStream out, StringBuilder ret , int level, boolean first) {
		ret.setLength(0);
		out.print(addLevel(ret,level).append('{'));
		out.print(toJsonString(ret,level+1,"test_id", name, true));
		out.print(toJsonString(ret,level+1,"level", phase, false));
		out.print(toJsonString(ret,level+1,"type", target, false));
		out.print(toJsonString(ret,level+1,"rank", rank, false));
		out.print(toJsonString(ret,level+1,"severity", severity, false));
		out.print(toJsonString(ret,level+1,"result", state, false));
		out.print(toJsonString(ret,level+1,"check_point_error_count", checkPointErrorCount, false));
		if (!checkPointErrorsKeys.isEmpty())
			printIntArray(out,ret, level+1,"errors",checkPointErrorsKeys, false);
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'),level).append('}'));
		
	}

}
