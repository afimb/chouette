package fr.certu.chouette.service.validation;

import java.util.ArrayList;
import java.util.List;

public class ChouettePTNetwork {
	
	private ChouetteArea 				chouetteArea;
	private ChouetteLineDescription 	chouetteLineDescription;
	private List<Company> 				companies 					= new ArrayList<Company>();
	private List<ConnectionLink> 		connectionLinks 			= new ArrayList<ConnectionLink>();
	private List<GroupOfLine> 		groupOfLines;
	private PTNetwork 					pTNetwork;
	private List<TimeSlot> 				timeSlots 					= new ArrayList<TimeSlot>();
	private List<Timetable> 			timetables 					= new ArrayList<Timetable>();
	
	public void setChouetteArea(ChouetteArea chouetteArea) {
		this.chouetteArea = chouetteArea;
	}
	
	public ChouetteArea getChouetteArea() {
		return chouetteArea;
	}
	
	public void setChouetteLineDescription(ChouetteLineDescription chouetteLineDescription) {
		this.chouetteLineDescription = chouetteLineDescription;
	}
	
	public ChouetteLineDescription getChouetteLineDescription() {
		return chouetteLineDescription;
	}
	
	public void addCompany(Company company) throws IndexOutOfBoundsException {
		companies.add(company);
	}
	
	public void addCompany(int index, Company company) throws IndexOutOfBoundsException {
		companies.add(index, company);
	}
	
	public void removeCompany(int index) throws IndexOutOfBoundsException {
		companies.remove(index);
	}
	
	public void removeCompany(Company company) {
		companies.remove(company);
	}
	
	public void clearCompanies() {
		companies.clear();
	}
	
	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}
	
	public List<Company> getCompanies() {
		return companies;
	}
	
	public Company[] getCompanyAsTable() {
		int size = companies.size();
		Company[] mArray = new Company[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (Company)companies.get(index);
		return mArray;
	}
	
	public Company getCompany(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > companies.size()))
            throw new IndexOutOfBoundsException();
		return (Company) companies.get(index);
	}
	
	public int getCompanyCount() {
        return companies.size();
    }
	
	public void setCompanies(ArrayList<Company> companies) {
        this.companies = companies;
    }
	
    public void setCompanies(Company[] arrayOfCompanies) {
    	companies.clear();
        for (int i = 0; i < arrayOfCompanies.length; i++)
            companies.add(arrayOfCompanies[i]);
    }
    
    public void setCompany(int index, Company company) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > companies.size()))
    		throw new IndexOutOfBoundsException();
    	companies.set(index, company);
    }
	
	public void addConnectionLink(ConnectionLink connectionLink) throws IndexOutOfBoundsException {
		connectionLinks.add(connectionLink);
	}
	
	public void addConnectionLink(int index, ConnectionLink connectionLink) throws IndexOutOfBoundsException {
		connectionLinks.add(index, connectionLink);
	}
	
	public void removeConnectionLink(int index) throws IndexOutOfBoundsException {
		connectionLinks.remove(index);
	}
	
	public void removeConnectionLink(ConnectionLink connectionLink) {
		connectionLinks.remove(connectionLink);
	}
	
	public void clearConnectionLinks() {
		connectionLinks.clear();
	}
	
	public void setConnectionLinks(List<ConnectionLink> connectionLinks) {
		this.connectionLinks = connectionLinks;
	}
	
	public List<ConnectionLink> getConnectionLinks() {
		return connectionLinks;
	}
	
	public ConnectionLink[] getConnectionLinkAsTable() {
		int size = connectionLinks.size();
		ConnectionLink[] mArray = new ConnectionLink[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (ConnectionLink)connectionLinks.get(index);
		return mArray;
	}
	
	public ConnectionLink getConnectionLink(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > connectionLinks.size()))
            throw new IndexOutOfBoundsException();
		return (ConnectionLink) connectionLinks.get(index);
	}
	
	public int getConnectionLinkCount() {
        return connectionLinks.size();
    }
	
	public void setConnectionLinks(ArrayList<ConnectionLink> connectionLinks) {
        this.connectionLinks = connectionLinks;
    }
	
    public void setConnectionLinks(ConnectionLink[] arrayOfConnectionLinks) {
    	connectionLinks.clear();
        for (int i = 0; i < arrayOfConnectionLinks.length; i++)
            connectionLinks.add(arrayOfConnectionLinks[i]);
    }
    
    public void setConnectionLink(int index, ConnectionLink connectionLink) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > connectionLinks.size()))
    		throw new IndexOutOfBoundsException();
    	connectionLinks.set(index, connectionLink);
    }
	
	
	public void addGroupOfLine(GroupOfLine groupOfLine) throws IndexOutOfBoundsException {
	    if (groupOfLine != null) {
		if (groupOfLines == null)
		    groupOfLines = new ArrayList<GroupOfLine>();
		groupOfLines.add(groupOfLine);
	    }
	}
	
	public void addGroupOfLine(int index, GroupOfLine groupOfLine) throws IndexOutOfBoundsException {
		groupOfLines.add(index, groupOfLine);
	}
	
	public void removeGroupOfLine(int index) throws IndexOutOfBoundsException {
		groupOfLines.remove(index);
	}
	
	public void removeGroupOfLine(GroupOfLine groupOfLine) {
		groupOfLines.remove(groupOfLine);
	}
	
	public void clearGroupOfLines() {
		groupOfLines.clear();
	}
	
	public void setGroupOfLines(List<GroupOfLine> groupOfLines) {
		this.groupOfLines = groupOfLines;
	}
	
	public List<GroupOfLine> getGroupOfLines() {
		return groupOfLines;
	}
	
	public GroupOfLine[] getGroupOfLineAsTable() {
		int size = groupOfLines.size();
		GroupOfLine[] mArray = new GroupOfLine[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (GroupOfLine)groupOfLines.get(index);
		return mArray;
	}
	
	public GroupOfLine getGroupOfLine(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > groupOfLines.size()))
            throw new IndexOutOfBoundsException();
		return (GroupOfLine) groupOfLines.get(index);
	}
	
	public int getGroupOfLineCount() {
        return groupOfLines.size();
    }
	
	public void setGroupOfLines(ArrayList<GroupOfLine> groupOfLines) {
        this.groupOfLines = groupOfLines;
    }
	
    public void setGroupOfLines(GroupOfLine[] arrayOfGroupOfLines) {
    	groupOfLines.clear();
        for (int i = 0; i < arrayOfGroupOfLines.length; i++)
        	groupOfLines.add(arrayOfGroupOfLines[i]);
    }
    
    public void setGroupOfLine(int index, GroupOfLine groupOfLine) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > groupOfLines.size()))
    		throw new IndexOutOfBoundsException();
    	groupOfLines.set(index, groupOfLine);
    }
	
	public void setPTNetwork(PTNetwork pTNetwork) {
		this.pTNetwork = pTNetwork;
	}
	
	public PTNetwork getPTNetwork() {
		return pTNetwork;
	}
	
	public void addTimeSlot(TimeSlot timeSlot) throws IndexOutOfBoundsException {
		timeSlots.add(timeSlot);
	}
	
	public void addTimeSlot(int index, TimeSlot timeSlot) throws IndexOutOfBoundsException {
		timeSlots.add(index, timeSlot);
	}
	
	public void removeTimeSlot(int index) throws IndexOutOfBoundsException {
		timeSlots.remove(index);
	}
	
	public void removeTimeSlot(TimeSlot timeSlot) {
		timeSlots.remove(timeSlot);
	}
	
	public void clearTimeSlots() {
		timeSlots.clear();
	}
	
	public void setTimeSlots(List<TimeSlot> timeSlots) {
		this.timeSlots = timeSlots;
	}
	
	public List<TimeSlot> getTimeSlots() {
		return timeSlots;
	}
	
	public TimeSlot[] getTimeSlotAsTable() {
		int size = timeSlots.size();
		TimeSlot[] mArray = new TimeSlot[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (TimeSlot)timeSlots.get(index);
		return mArray;
	}
	
	public TimeSlot getTimeSlot(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > timeSlots.size()))
            throw new IndexOutOfBoundsException();
		return (TimeSlot) timeSlots.get(index);
	}
	
	public int getTimeSlotCount() {
        return timeSlots.size();
    }
	
	public void setTimeSlots(ArrayList<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }
	
    public void setTimeSlots(TimeSlot[] arrayOfTimeSlots) {
    	timeSlots.clear();
        for (int i = 0; i < arrayOfTimeSlots.length; i++)
            timeSlots.add(arrayOfTimeSlots[i]);
    }
    
    public void setTimeSlot(int index, TimeSlot timeSlot) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > timeSlots.size()))
    		throw new IndexOutOfBoundsException();
    	timeSlots.set(index, timeSlot);
    }
		
	public void addTimetable(Timetable timetable) throws IndexOutOfBoundsException {
		timetables.add(timetable);
	}
	
	public void addTimetable(int index, Timetable timetable) throws IndexOutOfBoundsException {
		timetables.add(index, timetable);
	}
	
	public void removeTimetable(int index) throws IndexOutOfBoundsException {
		timetables.remove(index);
	}
	
	public void removeTimetable(Timetable timetable) {
		timetables.remove(timetable);
	}
	
	public void clearTimetables() {
		timetables.clear();
	}
	
	public void setTimetables(List<Timetable> timetables) {
		this.timetables = timetables;
	}
	
	public List<Timetable> getTimetables() {
		return timetables;
	}
	
	public Timetable[] getTimetableAsTable() {
		int size = timetables.size();
		Timetable[] mArray = new Timetable[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (Timetable)timetables.get(index);
		return mArray;
	}
	
	public Timetable getTimetable(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > timetables.size()))
            throw new IndexOutOfBoundsException();
		return (Timetable) timetables.get(index);
	}
	
	public int getTimetableCount() {
        return timetables.size();
    }
	
	public void setTimetables(ArrayList<Timetable> timetables) {
        this.timetables = timetables;
    }
	
    public void setTimetables(Timetable[] arrayOfTimetables) {
    	timetables.clear();
        for (int i = 0; i < arrayOfTimetables.length; i++)
            timetables.add(arrayOfTimetables[i]);
    }
    
    public void setTimetable(int index, Timetable timetable) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > timetables.size()))
    		throw new IndexOutOfBoundsException();
    	timetables.set(index, timetable);
    }
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		stb.append("<ChouettePTNetwork>\n");
		stb.append(pTNetwork.toString());
		if (groupOfLines != null)
			for (int i = 0; i < groupOfLines.size(); i++)
				stb.append(groupOfLines.get(i).toString());
		for (int i = 0; i < companies.size(); i++)
			stb.append(companies.get(i).toString());
		stb.append(chouetteArea.toString());
		for (int i = 0; i < connectionLinks.size(); i++)
			stb.append(connectionLinks.get(i).toString());
		for (int i = 0; i < timetables.size(); i++)
			stb.append(timetables.get(i).toString());
		for (int i = 0; i < timeSlots.size(); i++)
			stb.append(timeSlots.get(i).toString());
		stb.append(chouetteLineDescription.toString());
		stb.append("</ChouettePTNetwork>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<ChouettePTNetwork>\n");
		stb.append(pTNetwork.toString(indent+1, indentSize));
		if (groupOfLines != null)
			for (int i = 0; i < groupOfLines.size(); i++)
				stb.append(groupOfLines.get(i).toString(indent+1, indentSize));
		for (int i = 0; i < companies.size(); i++)
			stb.append(companies.get(i).toString(indent+1, indentSize));
		stb.append(chouetteArea.toString(indent+1, indentSize));
		for (int i = 0; i < connectionLinks.size(); i++)
			stb.append(connectionLinks.get(i).toString(indent+1, indentSize));
		for (int i = 0; i < timetables.size(); i++)
			stb.append(timetables.get(i).toString(indent+1, indentSize));
		for (int i = 0; i < timeSlots.size(); i++)
			stb.append(timeSlots.get(i).toString(indent+1, indentSize));
		stb.append(chouetteLineDescription.toString(indent+1, indentSize));
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</ChouettePTNetwork>\n");
		return stb.toString();
		
	}
}
