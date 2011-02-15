package fr.certu.chouette.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

public class CommandArgument 
{
    @Getter private Map<String, List<String>> parameters;
	@Getter private String name;
	
	public CommandArgument(String name) 
	{
		this.name = name;
		this.parameters = new HashMap<String, List<String>>();
	}
	
}
