package fr.certu.chouette.exchange.gtfs.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class GtfsNetwork extends GtfsBean 
{
	@Getter @Setter private String name;

	public GtfsNetwork(String name)
	{
		this.name = name;
	}
	@Override
	public String getCSVLine() 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
