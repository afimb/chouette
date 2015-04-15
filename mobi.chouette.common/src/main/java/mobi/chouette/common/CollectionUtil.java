package mobi.chouette.common;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionUtil {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Collection<Pair<T, T>> intersection(
			Collection<T> oldList, Collection<T> newList,
			java.util.Comparator comparator) {
		Collection<Pair<T, T>> result = new ArrayList<Pair<T, T>>();
		for (T oldValue : oldList) {
			for (T newValue : newList) {
				if (comparator.compare(oldValue, newValue) == 0) {
					result.add(Pair.of(oldValue, newValue));
				}
			}
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Collection<T> substract(Collection<T> oldList,
			Collection<T> newList, java.util.Comparator comparator) {
		Collection<T> result = new ArrayList<T>(oldList);
		for (T oldValue : oldList) {
			for (T newValue : newList) {
				if (comparator.compare(oldValue, newValue) == 0) {
					result.remove(oldValue);
				}
			}
		}
		return result;
	}

//	public static <O, N> Collection<Pair<O, N>> intersection(
//			Collection<O> oldList, Collection<N> newList,
//			Comparator<O, N> comparator) {
//		Collection<Pair<O, N>> result = new ArrayList<Pair<O, N>>();
//		for (O oldValue : oldList) {
//			for (N newValue : newList) {
//				if (comparator.compare(oldValue, newValue) == 0) {
//					result.add(Pair.of(oldValue, newValue));
//				}
//			}
//		}
//		return result;
//	}
//
//	public static <O, N> Collection<O> substract(Collection<O> oldList,
//			Collection<N> newList, Comparator<O, N> comparator) {
//		Collection<O> result = new ArrayList<O>(oldList);
//		for (O oldValue : oldList) {
//			for (N newValue : newList) {
//				if (comparator.compare(oldValue, newValue) == 0) {
//					result.remove(oldValue);
//				}
//			}
//		}
//		return result;
//	}
//
//	public interface Comparator<O, N> {
//
//		int compare(O oldValue, N newValue);
//	}

	
}
