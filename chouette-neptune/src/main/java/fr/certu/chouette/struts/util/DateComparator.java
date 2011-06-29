package fr.certu.chouette.struts.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import org.displaytag.model.Cell;

public class DateComparator implements Comparator<Cell> {
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	public int compare(Cell cell1, Cell cell2) {
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = sdf.parse(cell1.getStaticValue().toString().trim());
			date2 = sdf.parse(cell2.getStaticValue().toString().trim());
		}
		catch(ParseException e) {
			System.out.println("STATIC VALUE = \""+cell1.getStaticValue().toString().trim()+"\"");
			System.out.println("STATIC VALUE = \""+cell2.getStaticValue().toString().trim()+"\"");
			e.printStackTrace();
		}
		return date1.compareTo(date2);
	}
}
