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
public class Organisation extends ActiveRecordObject
{
	private static final long serialVersionUID = 8673888363745200271L;
    @Getter @Setter private String name;
}
