package fr.certu.chouette.service.geographic;

import java.util.Collection;

import fr.certu.chouette.model.neptune.NeptuneLocalizedObject;
import fr.certu.chouette.model.neptune.StopArea;


public interface IGeographicService 
{
	/**
	 * compute coordinates for parents StopAreas with barycentre of containedStopAreas<br/>
	 * 
	 * when a child is not localized, it will be by-passed<br/>
	 * when no child is localized, parent won't be localized
	 * 
	 */
	void propagateBarycentre() ;
	
	void computeBarycentre(Collection<StopArea> areas);

	/**
	 * convert projectedPoint Lambert2e coordinates in WGS84 ones for every empty WGS84 data 
	 */
	void convertToWGS84();

	/**
	 * convert WGS84 coordinates in  projectedPoint Lambert2e ones for every empty projected points 
	 */
	void convertToProjection(); 
	
	/**
	 * convert projectedPoint SRID coordinates in WGS84 ones for given object
	 * 
	 * @param area
	 */
	boolean convertToWGS84(NeptuneLocalizedObject area);

	/**
	 * convert wgs84 coordinate to projected ones 
	 * 
	 * nullify data if no projection set
	 * 
	 * @deprecated  Since 2.1.0 : projected coordinates are no more saved 
	 * 
	 * @param area
	 * @return
	 */
	@Deprecated
	boolean convertToProjection(NeptuneLocalizedObject area);
	
	void switchProjection(String srid);
}
