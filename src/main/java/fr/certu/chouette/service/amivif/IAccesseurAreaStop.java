package fr.certu.chouette.service.amivif;

import chouette.schema.AreaCentroid;
import chouette.schema.StopArea;

public interface IAccesseurAreaStop {

	StopArea getStopAreaOfStop(String stopId);

	AreaCentroid getAreaCentroidOfStop(String stopId);

	AreaCentroid getAreaCentroidOfArea(String stopId);

	public AreaCentroid getCentroidById(String centroidId);

}