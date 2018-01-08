package mobi.chouette.exchange.netexprofile.exporter.writer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.rutebanken.netex.model.DayTypeAssignment;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DayTypeAssignmentExportComparatorTest {


	@Test
	public void testCompare() {
		Set<DayTypeAssignment> dayTypeAssignmentSet = new TreeSet<>(new DayTypeAssignmentExportComparator());
		DayTypeAssignment a1 = new DayTypeAssignment().withId("a1").withIsAvailable(false);
		dayTypeAssignmentSet.add(a1);


		DayTypeAssignment a2 = new DayTypeAssignment().withId("a2").withIsAvailable(true);
		dayTypeAssignmentSet.add(a2);


		DayTypeAssignment a3 = new DayTypeAssignment().withId("a3").withIsAvailable(null);
		dayTypeAssignmentSet.add(a3);

		Assert.assertEquals(new ArrayList(dayTypeAssignmentSet), Arrays.asList(a2, a3, a1));
	}

}
