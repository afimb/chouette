package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mobi.chouette.exchange.report.AbstractReport;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class FileLocation extends AbstractReport {

	private String filename;

	private Integer lineNumber;

	private Integer columnNumber;

	protected FileLocation(String fileName) {
		this.filename = fileName;
	}

	protected FileLocation(String fileName, int lineNumber, int columnNumber) {
		this.filename = fileName;
		if (Integer.valueOf(lineNumber) >= 0)
			this.lineNumber = Integer.valueOf(lineNumber);
		if (Integer.valueOf(columnNumber) >= 0)
			this.columnNumber = Integer.valueOf(columnNumber);
	}

	public FileLocation(DataLocation dl) {
		this(dl.getFilename(),dl.getLineNumber(),dl.getColumnNumber());
				
	}

	@Override
	public void print(PrintStream out, StringBuilder ret , int level, boolean first) {
		ret.setLength(0);
		out.print(addLevel(ret, level).append('{'));
		out.print(toJsonString(ret, level + 1, "filename", filename, true));
		if (lineNumber != null) {
			out.print(toJsonString(ret, level + 1, "line_number", lineNumber, false));
		}
		if (columnNumber != null) {
			out.print(toJsonString(ret, level + 1, "column_number", columnNumber, false));
		}
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'), level).append('}'));

	}

}
