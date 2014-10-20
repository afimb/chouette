package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.nio.file.Paths;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;

public class GtfsDao
{
   private String _path;

   private GtfsParser<GtfsStopTime> stopTimeDao;

   public GtfsDao(String path)
   {
      _path = path;
   }

   public GtfsParser<GtfsStopTime> getStopTimeDao() throws Exception
   {
      if (stopTimeDao == null)
      {
         stopTimeDao = ParserFactory.build(Paths.get(_path,
               StopTimesParser.FILENAME).toString());
      }
      return stopTimeDao;
   }

}
