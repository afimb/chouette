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
public class Referential extends ActiveRecordObject
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 662218958334287711L;
	@Getter @Setter private Long organisationId;
    @Getter @Setter private String name;
    @Getter @Setter private String slug;
    @Getter @Setter private String prefix;
    @Getter @Setter private String projectionType;
    @Getter @Setter private String timeZone;
    @Getter @Setter private String bounds;
}
