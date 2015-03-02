package fr.certu.chouette.exchange.xml.neptune.importer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

@AllArgsConstructor
public class Context
{
   @Getter
   @Setter
   private String sourceFile;
   @Getter
   @Setter
   private ReportItem importReport;
   @Getter
   @Setter
   private PhaseReportItem validationReport;
   @Getter
   @Setter
   private SharedImportedData sharedData;
   @Getter
   @Setter
   private UnsharedImportedData unshareableData; 
   @Getter
   @Setter
   private Level2Validator validator;
   @Getter
   @Setter
   private boolean optimizeMemory;
   @Getter
   @Setter 
   private ModelAssembler assembler;
}
