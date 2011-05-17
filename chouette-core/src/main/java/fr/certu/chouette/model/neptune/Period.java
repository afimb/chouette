package fr.certu.chouette.model.neptune;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class Period implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1964071056103739954L;
	@Getter @Setter private Date startDate;
	@Getter @Setter private Date endDate;
	
	public Period()
	{
		
	}
	
	public Period(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("start = ").append(formatDate(startDate)).append(" end = ").append(formatDate(endDate));
		return sb.toString();
	}
	private static String formatDate(Date date){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(date != null){
			return dateFormat.format(date);
		}
		else{
			return null;
		}
	}
}
