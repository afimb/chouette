/**
 * 
 */
package fr.certu.chouette.plugin.model;


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
    @Getter @Setter private String type;
    @Getter @Setter private String options;
    @Getter @Setter private String fileType;

}
