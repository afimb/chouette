package mobi.chouette.scheduler;

import org.apache.commons.lang.StringUtils;

public class CommandNamingRules {

	public static String getCommandName(String action, String type)
	{
		type = type == null ? "" : type;

		return "mobi.chouette.exchange."
				+ (type.isEmpty() ? "" : type + ".") + action + "."
				+ StringUtils.capitalize(type)
				+ StringUtils.capitalize(action) + "Command";
	}

}
