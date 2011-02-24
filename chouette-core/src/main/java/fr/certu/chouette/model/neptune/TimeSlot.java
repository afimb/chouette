package fr.certu.chouette.model.neptune;

import fr.certu.chouette.filter.DetailLevelEnum;

public class TimeSlot extends NeptuneIdentifiedObject 
{
	private static final long serialVersionUID = 7510494886757866590L;

	@Override
	public void expand(DetailLevelEnum level) 
	{
		// to avoid circular call check if level is already set according to this level
		if (getLevel().ordinal() >= level.ordinal()) return;
		super.expand(level);
	}

}
