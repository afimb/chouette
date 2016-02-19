package mobi.chouette.exchange.gtfs.model.importer;

import java.util.HashSet;

public class GtfsErrorsHashSet<E> extends HashSet<E> {

	private static final long serialVersionUID = 1L;
	private Integer count = 0;
	
	@Override
	public boolean add(E e) {
		boolean result = super.add(e);
		if (result) {
			count++;
//			System.out.println("###### ERROR COUNT = "+count);
		}
		return result;
	}
}
