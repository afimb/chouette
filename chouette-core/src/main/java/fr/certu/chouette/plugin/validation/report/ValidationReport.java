/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.validation.report;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.plugin.model.CompilanceCheckResult;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author michel
 *
 */
public class ValidationReport extends Report 
{
    enum ORIGIN {VALIDATION} ;
	/**
	 * 
	 */
	public ValidationReport() 
	{
		setOriginKey(ORIGIN.VALIDATION.name());
		updateStatus(STATE.OK);
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.plugin.report.Report#addItem(fr.certu.chouette.plugin.report.ReportItem)
	 */
	@Override
	public void addItem(ReportItem item) 
	{
		super.addItem(item);
		updateStatus(item.getStatus());
	}
	
    public List<CompilanceCheckResult> toValidationResults()
    {
    	List<CompilanceCheckResult> list = new ArrayList<CompilanceCheckResult>();
    	for (ReportItem item : getItems()) 
    	{
    		list.addAll(((PhaseReportItem) item).toValidationResults());
		}
    	return list;
    	
    }



}
