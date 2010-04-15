package fr.certu.chouette.service.exportateur.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.StopPoint;

public class MassiveExportReport {
	private static final DateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
	private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private final Date exportDate = new Date(System.currentTimeMillis());
	private final Date startDate;
	private final Date endDate;
	private final boolean excludeConnectionLinks;
	private final List<LineReport> lineReports = new ArrayList<LineReport>();
	private final List<String> errors = new ArrayList<String>();
	
	public MassiveExportReport(Date startDate, Date endDate, boolean excludeConnectionLinks) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.excludeConnectionLinks = excludeConnectionLinks;
	}
	
	public void addLine(ChouettePTNetworkTypeType line){
		lineReports.add(new LineReport(line));
	}
	
	public void addError(String error){
		errors.add(error);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("#export\n");
		sb.append("at# ").append(dateTimeFormat.format(this.exportDate)).append("\n");
		sb.append("from# ").append(this.startDate != null ? dateFormat.format(this.startDate) : "").append("\n");
		sb.append("to# ").append(this.endDate != null ? dateFormat.format(this.endDate) : "").append("\n");
		sb.append("connection_links# ").append(!this.excludeConnectionLinks).append("\n\n");
		
		for(LineReport lineReport : lineReports){
			sb.append(lineReport).append("\n");
		}
		
		for(String error : errors){
			sb.append("error# ").append(error).append("\n");
		}
		
		return sb.toString();
	}

	private class LineReport{
		private String lineName;
		private String indice;
		private String registrationNumber;
		List<String> commercialStopPoints = new ArrayList<String>();
		
		public LineReport(ChouettePTNetworkTypeType line) {
			this.lineName = line.getChouetteLineDescription().getLine().getName();
			this.indice = line.getChouetteLineDescription().getLine().getNumber();
			this.registrationNumber = line.getChouetteLineDescription().getLine().getRegistration().getRegistrationNumber();
			
			for(StopPoint stopPoint : line.getChouetteLineDescription().getStopPoint()){
				this.commercialStopPoints.add(stopPoint.getName());
			}
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("line# ").append(this.lineName).append(" / ").append(this.indice).append(" (").append(this.registrationNumber).append(")\n");
			for(String commercialStopPoint : this.commercialStopPoints){
				sb.append("stoppoint# ").append(commercialStopPoint).append("\n");
			}
			
			return sb.toString();
		}
	}
}
