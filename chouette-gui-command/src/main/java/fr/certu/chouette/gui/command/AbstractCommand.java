package fr.certu.chouette.gui.command;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public abstract class AbstractCommand 
{
	@Getter @Setter protected Map<String,INeptuneManager<NeptuneIdentifiedObject>> managers;

}
