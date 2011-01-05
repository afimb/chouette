package fr.certu.chouette.exchange;

import java.util.List;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

import lombok.Getter;
import lombok.Setter;

public class PluginContainer<T extends NeptuneIdentifiedObject>
{
	public static PluginContainer<Line> createLinePluginContainer()
	{
		return new PluginContainer<Line>();
	}
	
@Getter @Setter private List<IImportPlugin<T>> importPlugins;
@Getter @Setter private List<IExportPlugin<T>> exportPlugins;
}
