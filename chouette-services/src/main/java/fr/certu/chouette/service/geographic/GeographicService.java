package fr.certu.chouette.service.geographic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Setter;

import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;

/**
 * Some tools for geographic manipulations
 * 
 * @author michel
 *
 */
public class GeographicService implements IGeographicService
{
	private static final Logger logger = Logger.getLogger(GeographicService.class);

	@Setter private INeptuneManager<StopArea> stopAreaManager;

	@Setter private int epsgLambert = 27572;  // Lambert2e
	@Setter private int epsgWGS84 = 4326;  // WGS84

	private MathTransform transformWGS84;
	private MathTransform transformLambert2e;
	CoordinateReferenceSystem sourceCRS;
	CoordinateReferenceSystem targetCRS;

	GeometryFactory factoryWGS84;
	GeometryFactory factoryLambert2e;

	public void init()
	{
		try {
			sourceCRS = CRS.decode("epsg:"+epsgLambert);
			targetCRS = CRS.decode("epsg:"+epsgWGS84);

			transformWGS84 = CRS.findMathTransform(sourceCRS, targetCRS);
			transformLambert2e = CRS.findMathTransform(targetCRS, sourceCRS);
		} 
		catch (FactoryException e) 
		{
			// TODO Auto-generated catch block
			logger.error("fail to initialize Geographic Tool :" +e.getMessage());
		}
		factoryLambert2e = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), epsgLambert);
		factoryWGS84 = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), epsgWGS84);

	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.tool.IGeographicTool#propagateBarycentre()
	 */
	@Override
	public void propagateBarycentre() 
	{
		// Filter latFilter = Filter.getNewIsNullFilter(StopArea.AREACENTROID+"."+AreaCentroid.LATITUDE);
		// Filter lonFilter = Filter.getNewIsNullFilter(StopArea.AREACENTROID+"."+AreaCentroid.LONGITUDE);
		// Filter coordFilter = Filter.getNewOrFilter(latFilter,lonFilter);
		Filter comtypeFilter = Filter.getNewEqualsFilter(StopArea.AREA_TYPE, ChouetteAreaEnum.COMMERCIALSTOPPOINT.toString());
		// Filter comFilter = Filter.getNewAndFilter(comtypeFilter,coordFilter);
		Filter placetypeFilter = Filter.getNewEqualsFilter(StopArea.AREA_TYPE, ChouetteAreaEnum.STOPPLACE.toString());
		// Filter placeFilter = Filter.getNewAndFilter(placetypeFilter,coordFilter);
		List<StopArea> commercials;
		List<StopArea> stopPlaces;
		// TODO ITL
		try 
		{
//			commercials = stopAreaManager.getAll(null,comFilter);
//			stopPlaces = stopAreaManager.getAll(null,placeFilter);
         commercials = stopAreaManager.getAll(null,comtypeFilter);
         stopPlaces = stopAreaManager.getAll(null,placetypeFilter);
		} 
		catch (ChouetteException e) 
		{
			// TODO report error
			logger.error("cannot load StopAreas :" +e.getMessage());
			return;
		}
		List<StopArea> toBeSaved = new ArrayList<StopArea>();

		// compute commercialStops coordinates
		for (StopArea commercial : commercials) 
		{
			if (!commercial.getContainedStopAreas().isEmpty())
			{
				double sumLatitude = 0.;
				double sumLongitude = 0.;
				int count = 0;
				LongLatTypeEnum longLatType = null;
				for (StopArea physical : commercial.getContainedStopAreas()) 
				{
					if (physical.getLatitude() != null && physical.getLongitude() != null)
					{
						sumLatitude += physical.getLatitude().doubleValue();
						sumLongitude += physical.getLongitude().doubleValue();
						count ++;
						if (longLatType == null && physical.getLongLatType() != null)
						{
							longLatType = physical.getLongLatType();
						}
					}
					else 
					{
						// TODO manage a report
						logger.warn("Physical Stop without coordinate : " +physical.getName()+" ("+physical.getObjectId()+")");
					}
				}
				if (count > 0)
				{
					// if (commercial.getAreaCentroid() == null) commercial.setAreaCentroid(new AreaCentroid());
					// AreaCentroid centroid = commercial.getAreaCentroid();
					commercial.setLatitude(new BigDecimal(sumLatitude/count));
					commercial.setLongitude(new BigDecimal(sumLongitude/count));
					commercial.setLongLatType(LongLatTypeEnum.WGS84);
					toBeSaved.add(commercial);
				}
				else
				{
					// TODO report
					logger.warn("no child coordinate for : " +commercial.getName()+" ("+commercial.getObjectId()+")");
				}
			}
			else
			{
				// TODO report
				logger.warn("no child for : " +commercial.getName()+" ("+commercial.getObjectId()+")");
			}
		}

		// compute StopPlaces : if stopPlace contains not localized stopPlaces, it will be differed in forward loops
		List<StopArea> remainedPlaces = new ArrayList<StopArea>();
		while (!stopPlaces.isEmpty())
		{
			for (StopArea place : stopPlaces)
			{
				if (!place.getContainedStopAreas().isEmpty())
				{
					double sumLatitude = 0.;
					double sumLongitude = 0.;
					int count = 0;
					boolean differ = false;
					LongLatTypeEnum longLatType = null;
					for (StopArea child : place.getContainedStopAreas()) 
					{
						if (child.getAreaCentroid() != null && child.getLatitude() != null && child.getLongitude() != null)
						{
							sumLatitude += child.getLatitude().doubleValue();
							sumLongitude += child.getLongitude().doubleValue();
							count ++;
							if (longLatType == null && child.getLongLatType() != null)
							{
								longLatType = child.getLongLatType();
							}
						}
						else 
						{
							if (child.getAreaType().equals(ChouetteAreaEnum.STOPPLACE))
							{
								if (stopPlaces.contains(child)) 
								{
									remainedPlaces.add(place);
									differ = true;
									break;
								}
							}
							// TODO manage a report
							logger.warn(child.getAreaType().toString()+" without coordinate : " +child.getName()+" ("+child.getObjectId()+")");
						}
					}
					if (differ) continue; // 
					if (count > 0)
					{
						// if (place.getAreaCentroid() == null) place.setAreaCentroid(new AreaCentroid());
						// AreaCentroid centroid = place.getAreaCentroid();
						place.setLatitude(new BigDecimal(sumLatitude/count));
						place.setLongitude(new BigDecimal(sumLongitude/count));
						place.setLongLatType(LongLatTypeEnum.WGS84);
						toBeSaved.add(place);
					}
					else
					{
						// TODO report
						logger.warn("no child coordinate for : " +place.getName()+" ("+place.getObjectId()+")");
					}
				}
				else
				{
					// TODO report
					logger.warn("no child for : " +place.getName()+" ("+place.getObjectId()+")");
				}
			}
			// prepare next loop for differed stopplaces
			stopPlaces = remainedPlaces;
			remainedPlaces = new ArrayList<StopArea>();
		}
		if (!toBeSaved.isEmpty())
		{
			try 
			{
				stopAreaManager.saveOrUpdateAll(null, toBeSaved);
			} 
			catch (ChouetteException e) 
			{
				// TODO report
				logger.error("cannot save StopAreas :" +e.getMessage());
			}
		}

	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.tool.IGeographicTool#propagateBarycentre()
	 */
	@Override
	public void computeBarycentre(Collection<StopArea> areas) 
	{
		// compute parent area coordinates
		for (StopArea area : areas) 
		{
			if (!area.getContainedStopAreas().isEmpty())
			{
				double sumLatitude = 0.;
				double sumLongitude = 0.;
				int count = 0;
				LongLatTypeEnum longLatType = null;
				for (StopArea child : area.getContainedStopAreas()) 
				{
					if (child.getLatitude() != null && child.getLongitude() != null)
					{
						sumLatitude += child.getLatitude().doubleValue();
						sumLongitude += child.getLongitude().doubleValue();
						count ++;
						if (longLatType == null && child.getLongLatType() != null)
						{
							longLatType = child.getLongLatType();
						}
					}
					else 
					{
						// TODO manage a report
						logger.warn("child Stop without coordinate : " +child.getName()+" ("+child.getObjectId()+")");
					}
				}
				if (count > 0)
				{
					// if (area.getAreaCentroid() == null) area.setAreaCentroid(new AreaCentroid());
					// AreaCentroid centroid = area.getAreaCentroid();
					area.setLatitude(new BigDecimal(sumLatitude/count));
					area.setLongitude(new BigDecimal(sumLongitude/count));
					area.setLongLatType(LongLatTypeEnum.WGS84);
				}
				else
				{
					// TODO report
					logger.warn("no child coordinate for : " +area.getName()+" ("+area.getObjectId()+")");
				}
			}
			else
			{
				// TODO report
				logger.warn("no child for : " +area.getName()+" ("+area.getObjectId()+")");
			}
		}


	}



	@Override
	public void convertToWGS84() 
	{
		// build filter on projected point x and y not nulls and lattitude or longitude nulls
		Filter latFilter = Filter.getNewIsNullFilter(StopArea.LATITUDE);
		Filter lonFilter = Filter.getNewIsNullFilter(StopArea.LONGITUDE);
		Filter xFilter = Filter.getNewNotFilter(Filter.getNewIsNullFilter(StopArea.X));
		Filter yFilter = Filter.getNewNotFilter(Filter.getNewIsNullFilter(StopArea.Y));
		Filter coordFilter = Filter.getNewAndFilter(xFilter,yFilter,Filter.getNewOrFilter(latFilter,lonFilter));
		List<StopArea> areas;
		List<StopArea> toBeSaved = new ArrayList<StopArea>();
		try 
		{
			areas = stopAreaManager.getAll(null,coordFilter);
		} 
		catch (ChouetteException e) 
		{
			// TODO report error
			logger.error("cannot load StopAreas :" +e.getMessage());
			return;
		}
		for (StopArea stopArea : areas) 
		{
			if (convertToWGS84(stopArea)) 
			{
				toBeSaved.add(stopArea);
			}
		}
		if (!toBeSaved.isEmpty())
		{
			try 
			{
				stopAreaManager.saveOrUpdateAll(null, toBeSaved);
			} 
			catch (ChouetteException e) 
			{
				// TODO add report
				logger.error("cannot save StopAreas :" +e.getMessage());
			}
		}
		
	}

	@Override
	public void convertToLambert2e() 
	{
		// build filter on projected point x or y nulls and lattitude and longitude not nulls
		Filter xFilter = Filter.getNewIsNullFilter(StopArea.X);
		Filter yFilter = Filter.getNewIsNullFilter(StopArea.Y);
		Filter latFilter = Filter.getNewNotFilter(Filter.getNewIsNullFilter(StopArea.LATITUDE));
		Filter lonFilter = Filter.getNewNotFilter(Filter.getNewIsNullFilter(StopArea.LONGITUDE));
		Filter coordFilter = Filter.getNewAndFilter(latFilter,lonFilter,Filter.getNewOrFilter(xFilter,yFilter));
		List<StopArea> areas;
		List<StopArea> toBeSaved = new ArrayList<StopArea>();
		try 
		{
			areas = stopAreaManager.getAll(null,coordFilter);
		} 
		catch (ChouetteException e) 
		{
			// TODO report error
			logger.error("cannot load StopAreas :" +e.getMessage());
			return;
		}
		for (StopArea stopArea : areas) 
		{
			if (convertToLambert2e(stopArea)) 
			{
				toBeSaved.add(stopArea);
			}
		}
		if (!toBeSaved.isEmpty())
		{
			try 
			{
				stopAreaManager.saveOrUpdateAll(null, toBeSaved);
			} 
			catch (ChouetteException e) 
			{
				// TODO add report
				logger.error("cannot save StopAreas :" +e.getMessage());
			}
		}
		
		
	}
	
	public boolean convertToWGS84(StopArea area)
	{
		if (sourceCRS == null ) 
		{
			logger.error("no projection defined ");
			return false;
		}
		if (area.getProjectionType() == null || area.getX() == null || area.getY() == null)
		{
			logger.error("no projected coordinate for "+ area.getName());
			return false;
		}

		Point point = factoryLambert2e.createPoint(new Coordinate(area.getX().doubleValue(),
				area.getY().doubleValue()));

		try 
		{
			Geometry wgs84 = JTS.transform( point, transformWGS84);
			Coordinate coord = wgs84.getCoordinate();
			area.setLongitude(BigDecimal.valueOf(coord.y));
			area.setLatitude(BigDecimal.valueOf(coord.x));
			area.setLongLatType(LongLatTypeEnum.WGS84);

		} 
		catch (Exception e) 
		{
			// TODO report or throw exception
			logger.error("fail to convert projected point to wgs84 :" +e.getMessage());
			return false;
		}
		return true;

	}


	public boolean convertToLambert2e(StopArea area)
	{
		if (targetCRS == null ) return false;
		Point point = factoryWGS84.createPoint(new Coordinate(area.getLatitude().doubleValue(),
				area.getLongitude().doubleValue()));

		try 
		{
			Geometry lambert2 = JTS.transform( point, transformLambert2e);
			Coordinate coord = lambert2.getCoordinate();
			area.setX(BigDecimal.valueOf(coord.x));
			area.setY(BigDecimal.valueOf(coord.y));
			area.setProjectionType("epsg:"+epsgLambert);

		} 
		catch (Exception e) 
		{
			// TODO report or or throw exception
			logger.error("fail to convert from wgs84 to projected point :" +e.getMessage());
			return false;
		}
		return true;

	}
	
	 public void switchProjection(String srid)
	 {
	    epsgLambert = Integer.parseInt(srid);
	      try {
	         sourceCRS = CRS.decode("epsg:"+epsgLambert);
	         transformLambert2e = CRS.findMathTransform(targetCRS, sourceCRS);
	      } 
	      catch (FactoryException e) 
	      {
	         // TODO Auto-generated catch block
	         logger.error("fail to initialize Geographic Tool :" +e.getMessage());
	      }
	      factoryLambert2e = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), epsgLambert);
	 }

}
