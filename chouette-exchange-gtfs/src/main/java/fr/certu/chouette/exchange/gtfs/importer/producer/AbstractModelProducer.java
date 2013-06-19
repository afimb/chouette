package fr.certu.chouette.exchange.gtfs.importer.producer;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsBean;
import fr.certu.chouette.model.neptune.NeptuneObject;

public abstract class AbstractModelProducer<T extends NeptuneObject, U extends GtfsBean> extends AbstractProducer implements IModelProducer<T, U>
{
    
	private static String prefix;
	private static String incremental = "";
	private static int nullIdCount = 0;
    public String composeObjectId(String type, String id, Logger logger)
    {
    	if (id == null) 
    	{
    		logger.error("id null for "+type);
    		id="NULL_"+nullIdCount;
    		nullIdCount++;
    	}
    	return prefix+":"+type+":"+id.trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
    }

    public String composeIncrementalObjectId(String type, String id, Logger logger)
    {
      if (id == null) 
      {
         logger.error("id null for "+type);
         id="NULL_"+nullIdCount;
         nullIdCount++;
      }
      return prefix+":"+type+":"+incremental+id.trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
    }

    public static void setIncrementalPrefix(String value)
    {
       incremental = value;
    }
    public static String getIncrementalPrefix()
    {
    	return incremental ;
    }
    public static void setPrefix(String value)
    {
      prefix = value;
    }
    public static String getPrefix()
    {
      return prefix ;
    }
	
}
