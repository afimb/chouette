package fr.certu.chouette.exchange.xml.neptune.importer.producer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import chouette.schema.ITL;
import fr.certu.chouette.exchange.xml.neptune.model.NeptuneRoutingConstraint;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 *  produce a temporary NeptuneRoutingConstraint object to create 
 *  relation between line and RoutingConstraint StopAreas on assembly phase
 */
public class RoutingConstraintProducer extends AbstractProducer
{
   private static final Logger logger = Logger.getLogger(RoutingConstraintProducer.class);
   /**
    * produce a temporary NeptuneRoutingConstraint object to create 
    *  relation between line and RoutingConstraint StopAreas on assembly phase
    * 
    * @param itls ITL data from Neptune file
    * @param report error reporting
    * @return relations created
    */
   public List<NeptuneRoutingConstraint> produce(ITL[] itls,ReportItem report) 
   {
      Map<String,NeptuneRoutingConstraint> constraintMap = new HashMap<String, NeptuneRoutingConstraint>();
      List<NeptuneRoutingConstraint> restrictions = new ArrayList<NeptuneRoutingConstraint>();

      for (ITL itl : itls) 
      {
         String lineId = getNonEmptyTrimedString(itl.getLineIdShortCut());
         NeptuneRoutingConstraint restriction = constraintMap.get(lineId);
         if (restriction == null)
         {
            restriction = new NeptuneRoutingConstraint();

            // LineIdShortCut mandatory
            restriction.setLineId(lineId);

            constraintMap.put(restriction.getLineId(), restriction);
            restrictions.add(restriction);
         }
         logger.debug("RoutingConstraint relation between "+lineId+" and "+itl.getAreaId()+" created");
         // Area mandatory
         restriction.addRoutingConstraintId(getNonEmptyTrimedString(itl.getAreaId()));
      }
      return restrictions;
   }
}
