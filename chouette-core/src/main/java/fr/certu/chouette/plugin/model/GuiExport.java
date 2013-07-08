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
public class GuiExport extends ActiveRecordObject
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8663496142103716594L;
	@Getter @Setter private Long referentialId;
    @Getter @Setter private String status;
    @Getter @Setter private String type;
    @Getter @Setter private String options;
    @Getter @Setter private String referencesType;
    @Getter @Setter private String referenceIds;

}
