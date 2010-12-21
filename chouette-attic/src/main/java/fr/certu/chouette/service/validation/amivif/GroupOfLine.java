package fr.certu.chouette.service.validation.amivif;

import java.util.ArrayList;
import java.util.List;

public class GroupOfLine extends TridentObject {
	
	private String								name;																				// 1
	private List<String>						lineIds							= new ArrayList<String>();							// 1..w
	private List<Line>							lines							= new ArrayList<Line>();							// 1..w
	private String								comment;																			// 0..1
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setLineIds(List<String> lineIds) {
		this.lineIds = lineIds;
	}
	
	public List<String> getLineIds() {
		return lineIds;
	}
	
	public void addLineId(String lineId) {
		lineIds.add(lineId);
	}
	
	public void removeLineId(String lineId) {
		lineIds.remove(lineId);
	}
	
	public void removeLineId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLineIdsCount()))
			throw new IndexOutOfBoundsException();
		lineIds.remove(i);
	}
	
	public String getLineId(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLineIdsCount()))
			throw new IndexOutOfBoundsException();
		return (String)lineIds.get(i);
	}
	
	public int getLineIdsCount() {
		if (lineIds == null)
			return 0;
		return lineIds.size();
	}
	
	public void setLines(List<Line> lines) {
		this.lines = lines;
	}
	
	public List<Line> getLines() {
		return lines;
	}
	
	public void addLine(Line line) {
		lines.add(line);
	}
	
	public void removeLine(Line line) {
		lines.remove(line);
	}
	
	public void removeLine(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLinesCount()))
			throw new IndexOutOfBoundsException();
		lines.remove(i);
	}
	
	public Line getLine(int i) throws IndexOutOfBoundsException {
		if ((i < 0) || (i >= getLinesCount()))
			throw new IndexOutOfBoundsException();
		return (Line)lines.get(i);
	}
	
	public int getLinesCount() {
		if (lines == null)
			return 0;
		return lines.size();
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
}
