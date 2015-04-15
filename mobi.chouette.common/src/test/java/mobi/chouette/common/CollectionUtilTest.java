package mobi.chouette.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CollectionUtilTest {

	@Test(groups = { "collectionUtil" }, description = "substract")
	public void testSubstract() throws Exception {

		Collection<String> a = new ArrayList<String>();
		a.add("a");
		a.add("b");
		a.add("c");
		a.add("d");
		Collection<String> b = new ArrayList<String>();
		b.add("a");
		b.add("D");
		b.add("e");
		b.add("f");

		Collection<String> c = CollectionUtil.substract(a, b, new StringComparator());

		Assert.assertEquals(c.size(), 2, "collection size");
		Assert.assertTrue(c.contains("b"), "collection should contain value");
		Assert.assertTrue(c.contains("c"), "collection should contain value");

	}

	@Test(groups = { "collectionUtil" }, description = "intersect")
	public void testIntersect() throws Exception {

		Collection<String> a = new ArrayList<String>();
		a.add("a");
		a.add("b");
		a.add("c");
		a.add("d");
		Collection<String> b = new ArrayList<String>();
		b.add("A");
		b.add("D");
		b.add("E");
		b.add("F");

		Collection<Pair<String, String>> c = CollectionUtil.intersection(a, b, new StringComparator());

		Assert.assertEquals(c.size(), 2, "collection size");
		Assert.assertTrue(c.contains(new Pair<String, String>("a", "A")), "collection should contain pair");
		Assert.assertTrue(c.contains(new Pair<String, String>("d", "D")), "collection should contain pair");

	}

	private class StringComparator implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {
			return o1.compareToIgnoreCase(o2);
		}

	}

}
