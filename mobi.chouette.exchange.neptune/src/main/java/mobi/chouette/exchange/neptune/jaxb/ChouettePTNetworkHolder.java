package mobi.chouette.exchange.neptune.jaxb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mobi.chouette.exchange.validation.report.Phase;

import org.trident.schema.trident.ChouettePTNetworkType;


@AllArgsConstructor
public class ChouettePTNetworkHolder
{
   @Getter
   private ChouettePTNetworkType chouettePTNetwork;
   @Getter
   private Phase report;

}
