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
import fr.certu.chouette.model.neptune.NeptuneLocalizedObject;
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
   private static final Logger logger = Logger
         .getLogger(GeographicService.class);

   @Setter
   private INeptuneManager<StopArea> stopAreaManager;

   @Setter
   private int epsgLambert = 27572; // Lambert2e
   @Setter
   private int epsgWGS84 = 4326; // WGS84

   private MathTransform transformWGS84;
   private MathTransform transformLambert2e;
   CoordinateReferenceSystem sourceCRS = null;
   CoordinateReferenceSystem targetCRS = null;

   GeometryFactory factoryWGS84;
   GeometryFactory factoryLambert2e;

   public void init()
   {
      try
      {
         sourceCRS = CRS.decode("epsg:" + epsgLambert);
      } catch (FactoryException e)
      {
         logger.error("fail to initialize Geographic Tool for epsg:"
               + epsgLambert + " " + e.getMessage());
      }
      try
      {
         targetCRS = CRS.decode("epsg:" + epsgWGS84);
      } catch (FactoryException e)
      {
         logger.error("fail to initialize Geographic Tool for epsg:"
               + epsgWGS84 + " " + e.getMessage());
      }
      try
      {
         if (sourceCRS != null && targetCRS != null)
         {
            transformWGS84 = CRS.findMathTransform(sourceCRS, targetCRS);
            transformLambert2e = CRS.findMathTransform(targetCRS, sourceCRS);
         }
      } catch (FactoryException e)
      {
         logger.error("fail to initialize Geographic Tool : transforms "
               + e.getMessage());
      }
      factoryLambert2e = new GeometryFactory(new PrecisionModel(
            PrecisionModel.FLOATING), epsgLambert);
      factoryWGS84 = new GeometryFactory(new PrecisionModel(
            PrecisionModel.FLOATING), epsgWGS84);

   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.tool.IGeographicTool#propagateBarycentre()
    */
   @Override
   public void propagateBarycentre()
   {
      Filter comtypeFilter = Filter.getNewEqualsFilter(StopArea.AREA_TYPE,
            ChouetteAreaEnum.CommercialStopPoint.toString());
      Filter placetypeFilter = Filter.getNewEqualsFilter(StopArea.AREA_TYPE,
            ChouetteAreaEnum.StopPlace.toString());
      List<StopArea> commercials;
      List<StopArea> stopPlaces;
      // TODO ITL
      try
      {
         commercials = stopAreaManager.getAll(null, comtypeFilter);
         stopPlaces = stopAreaManager.getAll(null, placetypeFilter);
      } catch (ChouetteException e)
      {
         // TODO report error
         logger.error("cannot load StopAreas :" + e.getMessage());
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
            for (StopArea physical : commercial.getContainedStopAreas())
            {
               if (physical.hasCoordinates())
               {
                  sumLatitude += physical.getLatitude().doubleValue();
                  sumLongitude += physical.getLongitude().doubleValue();
                  count++;
               } else
               {
                  // TODO manage a report
                  logger.warn("Physical Stop without coordinate : "
                        + physical.getName() + " (" + physical.getObjectId()
                        + ")");
               }
            }
            if (count > 0)
            {
               commercial.setLatitude(new BigDecimal(sumLatitude / count));
               commercial.setLongitude(new BigDecimal(sumLongitude / count));
               commercial.setLongLatType(LongLatTypeEnum.WGS84);
               toBeSaved.add(commercial);
            } else
            {
               // TODO report
               logger.warn("no child coordinate for : " + commercial.getName()
                     + " (" + commercial.getObjectId() + ")");
            }
         } else
         {
            // TODO report
            logger.warn("no child for : " + commercial.getName() + " ("
                  + commercial.getObjectId() + ")");
         }
      }

      // compute StopPlaces : if stopPlace contains not localized stopPlaces,
      // it will be differed in forward loops
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
               for (StopArea child : place.getContainedStopAreas())
               {
                  if (child.hasCoordinates())
                  {
                     sumLatitude += child.getLatitude().doubleValue();
                     sumLongitude += child.getLongitude().doubleValue();
                     count++;
                  } else
                  {
                     if (child.getAreaType().equals(ChouetteAreaEnum.StopPlace))
                     {
                        if (stopPlaces.contains(child))
                        {
                           remainedPlaces.add(place);
                           differ = true;
                           break;
                        }
                     }
                     // TODO manage a report
                     logger.warn(child.getAreaType().toString()
                           + " without coordinate : " + child.getName() + " ("
                           + child.getObjectId() + ")");
                  }
               }
               if (differ)
                  continue; //
               if (count > 0)
               {
                  place.setLatitude(new BigDecimal(sumLatitude / count));
                  place.setLongitude(new BigDecimal(sumLongitude / count));
                  place.setLongLatType(LongLatTypeEnum.WGS84);
                  toBeSaved.add(place);
               } else
               {
                  // TODO report
                  logger.warn("no child coordinate for : " + place.getName()
                        + " (" + place.getObjectId() + ")");
               }
            } else
            {
               // TODO report
               logger.warn("no child for : " + place.getName() + " ("
                     + place.getObjectId() + ")");
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
         } catch (ChouetteException e)
         {
            // TODO report
            logger.error("cannot save StopAreas :" + e.getMessage());
         }
      }

   }

   /*
    * (non-Javadoc)
    * 
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
            for (StopArea child : area.getContainedStopAreas())
            {
               if (child.hasCoordinates())
               {
                  sumLatitude += child.getLatitude().doubleValue();
                  sumLongitude += child.getLongitude().doubleValue();
                  count++;
               } else
               {
                  // TODO manage a report
                  logger.warn("child Stop without coordinate : "
                        + child.getName() + " (" + child.getObjectId() + ")");
               }
            }
            if (count > 0)
            {
               area.setLatitude(new BigDecimal(sumLatitude / count));
               area.setLongitude(new BigDecimal(sumLongitude / count));
               area.setLongLatType(LongLatTypeEnum.WGS84);
            } else
            {
               // TODO report
               logger.warn("no child coordinate for : " + area.getName() + " ("
                     + area.getObjectId() + ")");
            }
         } else
         {
            // TODO report
            logger.warn("no child for : " + area.getName() + " ("
                  + area.getObjectId() + ")");
         }
      }

   }

   @Override
   public void convertToWGS84()
   {
      // build filter on projected point x and y not nulls and latitude or
      // longitude nulls
      Filter latFilter = Filter.getNewIsNullFilter(StopArea.LATITUDE);
      Filter lonFilter = Filter.getNewIsNullFilter(StopArea.LONGITUDE);
      Filter xFilter = Filter.getNewNotFilter(Filter
            .getNewIsNullFilter(StopArea.X));
      Filter yFilter = Filter.getNewNotFilter(Filter
            .getNewIsNullFilter(StopArea.Y));
      Filter coordFilter = Filter.getNewAndFilter(xFilter, yFilter,
            Filter.getNewOrFilter(latFilter, lonFilter));
      List<StopArea> areas;
      List<StopArea> toBeSaved = new ArrayList<StopArea>();
      try
      {
         areas = stopAreaManager.getAll(null, coordFilter);
      } catch (ChouetteException e)
      {
         // TODO report error
         logger.error("cannot load StopAreas :" + e.getMessage());
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
         } catch (ChouetteException e)
         {
            // TODO add report
            logger.error("cannot save StopAreas :" + e.getMessage());
         }
      }

   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.service.geographic.IGeographicService#convertToProjection
    * ()
    */
   @Override
   public void convertToProjection()
   {
      // build filter on projected point x or y nulls and latitude and
      // longitude not nulls
      Filter xFilter = Filter.getNewIsNullFilter(StopArea.X);
      Filter yFilter = Filter.getNewIsNullFilter(StopArea.Y);
      Filter latFilter = Filter.getNewNotFilter(Filter
            .getNewIsNullFilter(StopArea.LATITUDE));
      Filter lonFilter = Filter.getNewNotFilter(Filter
            .getNewIsNullFilter(StopArea.LONGITUDE));
      Filter coordFilter = Filter.getNewAndFilter(latFilter, lonFilter,
            Filter.getNewOrFilter(xFilter, yFilter));
      List<StopArea> areas;
      List<StopArea> toBeSaved = new ArrayList<StopArea>();
      try
      {
         areas = stopAreaManager.getAll(null, coordFilter);
      } catch (ChouetteException e)
      {
         // TODO report error
         logger.error("cannot load StopAreas :" + e.getMessage());
         return;
      }
      for (StopArea stopArea : areas)
      {
         if (convertToProjection(stopArea))
         {
            toBeSaved.add(stopArea);
         }
      }
      if (!toBeSaved.isEmpty())
      {
         try
         {
            stopAreaManager.saveOrUpdateAll(null, toBeSaved);
         } catch (ChouetteException e)
         {
            // TODO add report
            logger.error("cannot save StopAreas :" + e.getMessage());
         }
      }

   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.service.geographic.IGeographicService#convertToWGS84
    * (fr.certu.chouette.model.neptune.StopArea)
    */
   public boolean convertToWGS84(NeptuneLocalizedObject area)
   {
      if (sourceCRS == null)
      {
         logger.error("no projection defined ");
         return false;
      }

      if (!area.hasProjection())
      {
         logger.error("no projected coordinate for " + area.getName());
         return false;
      }

      Point point = factoryLambert2e.createPoint(new Coordinate(area.getX()
            .doubleValue(), area.getY().doubleValue()));

      try
      {
         Geometry wgs84 = JTS.transform(point, transformWGS84);
         Coordinate coord = wgs84.getCoordinate();
         area.setLongitude(BigDecimal.valueOf(coord.y));
         area.setLatitude(BigDecimal.valueOf(coord.x));
         area.setLongLatType(LongLatTypeEnum.WGS84);

      } catch (Exception e)
      {
         logger.error("fail to convert projected point to wgs84 :"
               + e.getMessage());
         return false;
      }
      return true;

   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.service.geographic.IGeographicService#convertToProjection
    * (fr.certu.chouette.model.neptune.StopArea)
    */
   public boolean convertToProjection(NeptuneLocalizedObject area)
   {
      if (sourceCRS == null)
      {
         area.setX(null);
         area.setY(null);
         area.setProjectionType(null);
         return true;
      }
      if (!area.hasCoordinates())
      {
         logger.error("no WGS84 coordinate for " + area.getName());
         return false;
      }

      Point point = factoryWGS84.createPoint(new Coordinate(area.getLatitude()
            .doubleValue(), area.getLongitude().doubleValue()));

      try
      {
         Geometry lambert2 = JTS.transform(point, transformLambert2e);
         Coordinate coord = lambert2.getCoordinate();
         area.setX(BigDecimal.valueOf(coord.x));
         area.setY(BigDecimal.valueOf(coord.y));
         area.setProjectionType("epsg:" + epsgLambert);

      } catch (Exception e)
      {
         // TODO report or or throw exception
         logger.error("fail to convert from wgs84 to projected point :"
               + e.getMessage());
         return false;
      }
      return true;

   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.service.geographic.IGeographicService#switchProjection
    * (java.lang.String)
    */
   public void switchProjection(String srid)
   {
      if (srid == null)
      {
         sourceCRS = null;
      } else
      {
         epsgLambert = Integer.parseInt(srid);
         try
         {
            sourceCRS = CRS.decode("epsg:" + epsgLambert);
            transformWGS84 = CRS.findMathTransform(sourceCRS, targetCRS);
            transformLambert2e = CRS.findMathTransform(targetCRS, sourceCRS);
            factoryLambert2e = new GeometryFactory(new PrecisionModel(
                  PrecisionModel.FLOATING), epsgLambert);
         } catch (FactoryException e)
         {
            logger.error("fail to initialize Geographic Tool : epsg:"
                  + epsgLambert + " " + e.getMessage());
         }
      }
   }

}
