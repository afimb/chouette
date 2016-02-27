package mobi.chouette.exchange.exporter;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class SharedDataKeys {
	// private Network network;
	
	@Getter
	@Setter
	private Set<String> networkIds = new HashSet<>();
	@Getter
	@Setter
	private Set<String> companyIds = new HashSet<>();
	@Getter
	@Setter
	private Set<String> groupOfLineIds = new HashSet<>();
	@Getter
	@Setter
	private Set<String> stopAreaIds = new HashSet<>();
	@Getter
	@Setter
	private Set<String> connectionLinkIds = new HashSet<>();
	@Getter
	@Setter
	private Set<String> accessLinkIds = new HashSet<>();
	@Getter
	@Setter
	private Set<String> accessPointIds = new HashSet<>();
	@Getter
	@Setter
	private Set<String> timetableIds = new HashSet<>();
	@Getter
	@Setter
	private Set<String> restrictionConstraints = new HashSet<>();
	
	public void clear()
	{
		networkIds.clear();
		companyIds.clear();
		groupOfLineIds.clear();
		stopAreaIds.clear();
		connectionLinkIds.clear();
		accessLinkIds.clear();
		accessPointIds.clear();
		timetableIds.clear();
		restrictionConstraints.clear();
	}
}
