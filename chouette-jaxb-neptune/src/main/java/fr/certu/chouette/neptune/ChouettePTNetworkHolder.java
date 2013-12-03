package fr.certu.chouette.neptune;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.trident.schema.trident.ChouettePTNetworkType;

import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

@AllArgsConstructor
public class ChouettePTNetworkHolder 
{
@Getter private ChouettePTNetworkType chouettePTNetwork;
@Getter private PhaseReportItem report;

}
