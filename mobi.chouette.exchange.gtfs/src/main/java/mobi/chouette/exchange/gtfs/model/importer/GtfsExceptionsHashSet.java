package mobi.chouette.exchange.gtfs.model.importer;

import java.util.HashSet;
	
public class GtfsExceptionsHashSet<E extends GtfsException> extends HashSet<E> {
	
	private static final long serialVersionUID = 1L;
        private Integer count = 0;
	private Integer totalCount = 0;

	@Override
	public boolean add(E e) {
	        totalCount++;
		boolean result = super.add(e);
		if (result) {
			count++;
		}
		return result;
	}
}
