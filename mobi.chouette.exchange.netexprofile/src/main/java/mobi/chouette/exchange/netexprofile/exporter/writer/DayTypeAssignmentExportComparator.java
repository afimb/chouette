package mobi.chouette.exchange.netexprofile.exporter.writer;

import java.util.Comparator;

import org.rutebanken.netex.model.DayTypeAssignment;

/**
 * Sort DayTypeAssignments for export.
 * <p>
 * Most specific assignments (exclusions) should be sorted after less specific (inclusions) to allow readers to apply assignments sequencially.
 */
public class DayTypeAssignmentExportComparator implements Comparator<DayTypeAssignment> {

	@Override
	public int compare(DayTypeAssignment o1, DayTypeAssignment o2) {

		if (Boolean.FALSE.equals(o1.isIsAvailable())) {
			if (!Boolean.FALSE.equals(o2.isIsAvailable())) {
				return 1;
			}
		} else if (Boolean.FALSE.equals(o2.isIsAvailable())) {
			return -1;
		}

		return o1.getId().compareTo(o2.getId());
	}
}
