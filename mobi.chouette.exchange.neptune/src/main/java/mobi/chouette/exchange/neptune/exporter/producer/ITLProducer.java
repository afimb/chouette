package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.NeptuneChouetteIdGenerator;
import mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters;
import mobi.chouette.model.Line;
import mobi.chouette.model.RoutingConstraint;

import org.trident.schema.trident.ITLType;

public class ITLProducer implements Constant
{
   /**
    * @param context job context
    * @param line exported line
    * @param routingConstraint exported routing Constraint
    * @return ITL Neptune object
    */
   public ITLType produce(Context context, Line line, RoutingConstraint routingConstraint)
   {
	   NeptuneExportParameters parameters = (NeptuneExportParameters) context.get(CONFIGURATION);
	   NeptuneChouetteIdGenerator neptuneChouetteIdGenerator = (NeptuneChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		
      ITLType jaxbITL = AbstractJaxbNeptuneProducer.tridentFactory
            .createITLType();

      jaxbITL.setName(routingConstraint.getName());
      jaxbITL.setLineIdShortCut(neptuneChouetteIdGenerator.toSpecificFormatId(line.getChouetteId(), parameters.getDefaultCodespace(), line));
      jaxbITL.setAreaId(neptuneChouetteIdGenerator.toSpecificFormatId(routingConstraint.getChouetteId(), parameters.getDefaultCodespace(), routingConstraint));

      return jaxbITL;
   }

}
