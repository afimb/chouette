/**
 * 
 */
package fr.certu.chouette.plugin.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * @author michel
 *
 */
public class CompilanceCheckTask extends ActiveRecordObject 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5725000704712085992L;
	@Getter @Setter private Long referentialId;
	@Getter @Setter private ImportTask importTask;
    @Getter @Setter private String status;
    @Getter @Setter private JSONObject parameters;
	@Getter @Setter private Long userId;
	@Getter @Setter private String userName;
    @Getter @Setter private JSONObject progressInfo;
    @Getter @Setter private List<CompilanceCheckResult> results;
    @Getter @Setter private String referencesType;
    @Getter @Setter private String referenceIds;
    
	public CompilanceCheckTask()
	{
		setCreatedAt(Calendar.getInstance().getTime());
		setUpdatedAt(getCreatedAt());
	}

    
    public void addResult(CompilanceCheckResult result)
    {
    	if (results == null) results = new ArrayList<CompilanceCheckResult>();
    	results.add(result);
    	result.setCompilanceCheckTask(this);
    }
    
    public void addAllResults(List<CompilanceCheckResult> results)
    {
    	for (CompilanceCheckResult result : results) 
    	{
			addResult(result);
		}
    }


}
