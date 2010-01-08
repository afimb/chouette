package fr.certu.chouette.service.validation;

import org.exolab.castor.types.Date;

public class Period {

	private Timetable 	timetable;
	private Date 		startOfPeriod;
	private Date 		endOfPeriod;	
	
	public void setTimetable(Timetable timetable) {
		this.timetable = timetable;
	}
	
	public Timetable getTimetable() {
		return timetable;
	}

	public void setStartOfPeriod(Date startOfPeriod) {
		this.startOfPeriod = startOfPeriod;
	}
	
	public Date getStartOfPeriod() {
		return startOfPeriod;
	}
	
	public void setEndOfPeriod(Date endOfPeriod) {
		this.endOfPeriod = endOfPeriod;
	}
	
	public Date getEndOfPeriod() {
		return endOfPeriod;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<Period>\n");
		stb.append("<Start>"+startOfPeriod+"</Start>\n");
		stb.append("<End>"+endOfPeriod+"</End>\n");
		stb.append("</Period>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Period>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Start>"+startOfPeriod+"</Start>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<End>"+endOfPeriod+"</End>\n");
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</Period>\n");
		return stb.toString();
	}
}
