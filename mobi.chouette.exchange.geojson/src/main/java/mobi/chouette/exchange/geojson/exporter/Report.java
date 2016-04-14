package mobi.chouette.exchange.geojson.exporter;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.report.LineError;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.LineInfo.LINE_STATE;
import mobi.chouette.model.Line;

public class Report implements Constant {

	// global stats
	public static void addGlobalStats(Context context, DataStats stats) {
		ActionReport report = getReport(context);
		report.setStats(stats);
	}

	// line info
	public static void addLineInfo(Context context, Line line, DataStats stats) {
		ActionReport report = getReport(context);
		LineInfo lineInfo = getLineInfo(report, line);
		lineInfo.setStats(stats);
	}

	public static void addLineInfo(Context context, Line line, LINE_STATE status) {
		ActionReport report = getReport(context);
		LineInfo lineInfo = getLineInfo(report, line);
		lineInfo.setStatus(status);
	}

	public static void addLineInfo(Context context, Line line, LineError error) {
		ActionReport report = getReport(context);
		LineInfo lineInfo = getLineInfo(report, line);
		lineInfo.setStatus(LINE_STATE.ERROR);
		lineInfo.getErrors().add(error);
	}

	// file info
	public static void addFileInfo(Context context, String name,
			FILE_STATE status) {
		ActionReport report = getReport(context);
		FileInfo lineInfo = getFileInfo(report, name);
		lineInfo.setStatus(status);
	}

	public static void addFileInfo(Context context, String name, FileError error) {
		ActionReport report = getReport(context);
		FileInfo fileInfo = getFileInfo(report, name);
		fileInfo.setStatus(FILE_STATE.ERROR);
		fileInfo.getErrors().add(error);
	}

	// private

	private static LineInfo getLineInfo(ActionReport report, Line line) {
		LineInfo result = report.findLineInfo(line.getObjectId());
		if (result == null) {
			result = new LineInfo(line);
			report.getLines().add(result);
		}
		return result;
	}

	private static FileInfo getFileInfo(ActionReport report, String name) {
		FileInfo result = report.findFileInfo(name);
		if (result == null) {
			result = new FileInfo(name, FILE_STATE.OK);
			report.getFiles().add(result);
		}
		return result;
	}

	private static ActionReport getReport(Context context) {
		ActionReport result = null;
		Object object = context.get(REPORT);
		if (object != null && object instanceof ActionReport) {
			result = (ActionReport) object;
		} else {
			result = new ActionReport();
			context.put(REPORT, result);
		}
		return result;
	}
}
