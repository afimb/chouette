/**
 * 
 */
package fr.certu.chouette.plugin.model;

import java.util.Calendar;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * @author michel
 *
 */
public class CompilanceCheckResult extends ActiveRecordObject 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5725000704712085992L;
    @Getter private String ruleCode;
    @Getter @Setter private String ruleFormat;
    @Getter @Setter private String ruleTarget;
    @Getter @Setter private Integer ruleLevel;
    @Getter @Setter private Integer ruleNumber;
    @Getter @Setter private String severity;
	@Getter @Setter private String status;
	@Getter @Setter private Integer violationCount;
    @Getter @Setter private JSONObject detail;
    @Getter @Setter private CompilanceCheckTask compilanceCheckTask;

    
	public CompilanceCheckResult()
	{
		setCreatedAt(Calendar.getInstance().getTime());
		setUpdatedAt(getCreatedAt());
	}
	
	public void setRuleCode(String code)
	{
		ruleCode = code;
		String[] items = code.split("-");
		if (items.length == 4)
		{
			ruleLevel = Integer.valueOf(items[0]);
			ruleFormat = items[1];
			ruleTarget = items[2];
			ruleNumber = Integer.valueOf(items[3]);
		}
		else if (items.length == 3)
		{
			ruleLevel = Integer.valueOf(items[0]);
			ruleFormat = "";
			ruleTarget = items[1];
			ruleNumber = Integer.valueOf(items[2]);			
		}
	}

}
