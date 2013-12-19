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
public class GuiValidation extends ActiveRecordObject 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5725000704712085992L;
	@Getter @Setter private Long referentialId;
	@Getter @Setter private GuiImport importTask;
    @Getter @Setter private String status;
    @Getter @Setter private JSONObject parameters;
	@Getter @Setter private Long userId;
	@Getter @Setter private String userName;
    @Getter @Setter private JSONObject progressInfo;
    @Getter @Setter private List<GuiValidationStep> steps;
    
	public GuiValidation()
	{
		setCreatedAt(Calendar.getInstance().getTime());
		setUpdatedAt(getCreatedAt());
	}

    
    public void addStep(GuiValidationStep step)
    {
    	if (steps == null) steps = new ArrayList<GuiValidationStep>();
    	steps.add(step);
    }

}
