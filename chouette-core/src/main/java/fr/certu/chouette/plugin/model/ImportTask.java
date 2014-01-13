/**
 * 
 */
package fr.certu.chouette.plugin.model;



import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * @author michel
 *
 */
public class ImportTask extends ActiveRecordObject
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 896326851460511494L;
	@Getter @Setter private Long referentialId;
    @Getter @Setter private String status;
    @Getter @Setter private JSONObject parameters;
	@Getter @Setter private Long userId;
	@Getter @Setter private String userName;
    @Getter @Setter private JSONObject progressInfo;
    @Getter @Setter private JSONObject result;
    @Getter @Setter private CompilanceCheckTask compilanceCheckTask;

}
