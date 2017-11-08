package mobi.chouette.exchange.gtfs.exporter;


import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.model.Company;

public class ExportableData  extends mobi.chouette.exchange.exporter.ExportableData{

	/**
	 * Companies that are referred to as agencies by gtfs routes.
	 */
	@Getter
	@Setter
	private Set<Company> agencyCompanies = new HashSet<>();

	@Override
	public void clear() {
		super.clear();
		agencyCompanies.clear();
	}
}
