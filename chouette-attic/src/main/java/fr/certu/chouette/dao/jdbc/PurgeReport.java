package fr.certu.chouette.dao.jdbc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PurgeReport {
	private static final DateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
	private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private final Date purgeDate = new Date(System.currentTimeMillis());
	private final Date boundaryDate;
	private final boolean before;
	private final List<ItemReport> lineReports = new ArrayList<ItemReport>();
	private final List<ItemReport> commercialStopPointReports = new ArrayList<ItemReport>();
	private final List<ItemReport> physicalStopPointReports = new ArrayList<ItemReport>();
	private final List<ItemReport> connectionLinkReports = new ArrayList<ItemReport>();
	private final List<CountReport> countReports = new ArrayList<CountReport>();
	private final List<String> errors = new ArrayList<String>();
	private HashMap<String, String> summary;
	
	public PurgeReport(Date boundaryDate, boolean before) {
		this.boundaryDate = boundaryDate;
		this.before = before;
	}
	
	public HashMap<String, String> getSummary() {
		return summary;
	}
	
	public void setSummary(HashMap<String, String> summary) {
		this.summary = summary;
	}
	
	public Date getPurgeDate(){
		return this.purgeDate;
	}
	
	public void addLine(String name, String registrationNumber){
		lineReports.add(new ItemReport("line#", name, registrationNumber));
	}
	
	public void addPhysicalStopPoint(String name, String registrationNumber){
		physicalStopPointReports.add(new ItemReport("physical_stop_point#", name, registrationNumber));
	}
	
	public void addCommercialStopPoint(String name, String registrationNumber){
		commercialStopPointReports.add(new ItemReport("commercial_stop_point#", name, registrationNumber));
	}
	
	public void addConnectionLink(String name, String objectId){
		connectionLinkReports.add(new ItemReport("connection_link#", name, objectId));
	}
	
	public void addCount(String name, int before, int after){
		countReports.add(new CountReport(name, before, after));
	}	
	
	public void addError(String error){
		errors.add(error);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("#purge\n");
		sb.append("at# ").append(dateTimeFormat.format(this.purgeDate)).append("\n");
		sb.append("boundary_date# ").append(dateFormat.format(this.boundaryDate)).append("\n");
		sb.append("before# ").append(this.before).append("\n\n");
		
		for(ItemReport lineReport : lineReports){
			sb.append(lineReport);
		}
		
		sb.append("\n");
		
		for(ItemReport physicalStopPointReport : physicalStopPointReports){
			sb.append(physicalStopPointReport);
		}
		
		sb.append("\n");
		
		for(ItemReport commercialStopPointReport : commercialStopPointReports){
			sb.append(commercialStopPointReport);
		}
		
		sb.append("\n");
		
		for(ItemReport connectionLinkReport : connectionLinkReports){
			sb.append(connectionLinkReport);
		}
		
		sb.append("\n");
		
		for(CountReport countReport : countReports){
			sb.append(countReport);
		}
		
		sb.append("\n");
		
		for(String error : errors){
			sb.append("error# ").append(error).append("\n");
		}
		
		return sb.toString();
	}

	private class ItemReport{
		private String type;
		private String name;
		private String id;
		
		public ItemReport(String type, String name, String id) {
			this.type = type;
			this.name = name;
			this.id = id;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(this.type).append(" ").append(this.name).append(" / ").append(this.id).append("\n");
			
			return sb.toString();
		}
	}
	
	private class CountReport{
		private String type;
		private int before;
		private int after;
		
		public CountReport(String type, int before, int after) {
			this.type = type;
			this.before = before;
			this.after = after;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(this.type).append(" ").append(this.before).append(" ").append(this.after).append("\n");
			
			return sb.toString();
		}
	}
	
}
