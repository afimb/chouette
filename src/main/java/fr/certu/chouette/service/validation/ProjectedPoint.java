package fr.certu.chouette.service.validation;

import java.math.BigDecimal;

public class ProjectedPoint {
	
	private String 			projectionType;
	private BigDecimal 		x;
	private BigDecimal 		y;
	private AreaCentroid 	areaCentroid;
	private StopPoint 		stopPoint;
	
	public void setProjectionType(String projectionType) {
		this.projectionType = projectionType;
	}
	
	public String getProjectionType() {
		return projectionType;
	}
	
	public void setX(BigDecimal x) {
		this.x = x;
	}
	
	public BigDecimal getX() {
		return x;
	}
	
	public void setY(BigDecimal y) {
		this.y = y;
	}
	
	public BigDecimal getY() {
		return y;
	}
	
	public void setAreaCentroid(AreaCentroid areaCentroid) {
		this.areaCentroid = areaCentroid;
	}
	
	public AreaCentroid getAreaCentroid() {
		return areaCentroid;
	}
	
	public void setStopPoint(StopPoint stopPoint) {
		this.stopPoint = stopPoint;
	}
	
	public StopPoint getStopPoint() {
		return stopPoint;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<ProjectedPoint>\n");
		stb.append("<X>"+x.toString()+"</X>\n");
		stb.append("<Y>"+y.toString()+"</Y>\n");
		if (projectionType != null)
			stb.append("<ProjectionType>"+projectionType+"</ProjectionType>\n");
		stb.append("</ProjectedPoint>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<ProjectedPoint>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<X>"+x.toString()+"</X>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Y>"+y.toString()+"</Y>\n");
		if (projectionType != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<ProjectionType>"+projectionType+"</ProjectionType>\n");
		}
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</ProjectedPoint>\n");
		return stb.toString();
	}
}
