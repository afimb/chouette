package fr.certu.chouette.service.amivif.base;

public class ProjectedPointConverter {
	
	public chouette.schema.ProjectedPoint atc(amivif.schema.ProjectedPoint amivifProjectedPoint) {
		if (amivifProjectedPoint == null)
			return null;
		chouette.schema.ProjectedPoint 	chouetteProjectedPoint = new chouette.schema.ProjectedPoint();
		chouetteProjectedPoint.setProjectionType(amivifProjectedPoint.getProjectionType());
		chouetteProjectedPoint.setX(amivifProjectedPoint.getX());
		chouetteProjectedPoint.setY(amivifProjectedPoint.getY());
		return chouetteProjectedPoint;
	}

	public amivif.schema.ProjectedPoint cta(chouette.schema.ProjectedPoint chouetteProjectedPoint) {
		if (chouetteProjectedPoint == null)
			return null;
		amivif.schema.ProjectedPoint 	amivifProjectedPoint = new amivif.schema.ProjectedPoint();
		amivifProjectedPoint.setProjectionType(chouetteProjectedPoint.getProjectionType());
		amivifProjectedPoint.setX(chouetteProjectedPoint.getX());
		amivifProjectedPoint.setY(chouetteProjectedPoint.getY());
		return amivifProjectedPoint;
	}
}
