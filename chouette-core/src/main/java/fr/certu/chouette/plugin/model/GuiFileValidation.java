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
public class GuiFileValidation extends ActiveRecordObject 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5725000704712085992L;
	@Getter @Setter private Long organisationId;
    @Getter @Setter private String status;
    @Getter @Setter private String options;
    @Getter @Setter private String fileType;
    @Getter @Setter private String fileName;

}
