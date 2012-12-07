/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.importer.producer;

import fr.certu.chouette.exchange.gtfs.model.GtfsBean;
import fr.certu.chouette.model.neptune.NeptuneObject;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author michel
 *
 */
public interface IModelProducer <T extends NeptuneObject, U extends GtfsBean> 
{
    T produce(U o,ReportItem report);
}
