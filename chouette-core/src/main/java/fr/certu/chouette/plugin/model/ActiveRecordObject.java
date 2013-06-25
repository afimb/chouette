/**
 * 
 */
package fr.certu.chouette.plugin.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.NeptuneObject;

/**
 * @author michel
 *
 */
@SuppressWarnings("serial")
public abstract class ActiveRecordObject extends NeptuneObject
{
	@Getter @Setter private Date createdAt;
	@Getter @Setter private Date updatedAt;

}
