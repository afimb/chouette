package fr.certu.chouette.service.validation.amivif;

import java.math.BigDecimal;

public class ProjectedPoint {
	
	private BigDecimal	x;				// 1
	private BigDecimal	y;				// 1
	private String		projectionType;	// 0..1
	
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
	
	public void setProjectionType(String projectionType) {
		this.projectionType = projectionType;
	}
	
	public String getProjectionType() {
		return projectionType;
	}
}
