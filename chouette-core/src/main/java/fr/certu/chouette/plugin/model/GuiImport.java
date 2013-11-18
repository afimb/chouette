/**
 * 
 */
package fr.certu.chouette.plugin.model;


import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * @author michel
 *
 */
public class GuiImport extends ActiveRecordObject
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 896326851460511494L;
	@Getter @Setter private Long referentialId;
    @Getter @Setter private String status;
    @Getter @Setter private String format;
    @Getter @Setter private boolean noSave;
    @Getter @Setter private JSONObject parameters;
	@Getter @Setter private Long userId;
	@Getter @Setter private String userName;
    @Getter @Setter private JSONObject progressInfo;
    @Getter @Setter private JSONObject result;

}
