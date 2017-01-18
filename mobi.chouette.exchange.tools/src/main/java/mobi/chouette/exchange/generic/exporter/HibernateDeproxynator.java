package mobi.chouette.exchange.generic.exporter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.NeptuneObject;

@Log4j
public class HibernateDeproxynator<T> {
	public List deepDeproxy(final List maybeProxy) throws ClassCastException {
		if (maybeProxy == null)
			return null;
		HashSet<Object> visited = new HashSet<>(100000);
		HashSet<Object> moreObjectsToFollow = new HashSet<>(100000);

		List<T> results = new ArrayList<T>();
		for (Object x : maybeProxy) {
			AtomicInteger stackCounter = new AtomicInteger();
			// log.info("Deproxying
			// "+x.getClass().getSimpleName()+"/"+x.hashCode());
			T ret = deepDeproxy(x, visited, moreObjectsToFollow, stackCounter);
			// log.info("Deproxying finished
			// "+x.getClass().getSimpleName()+"/"+x.hashCode());
			results.add(ret);
		}

		// Attempt to reduce stack depth. Note using iterator to allow recursive
		// method to add more elements
		AtomicInteger stackCounter = new AtomicInteger();

		boolean finished = false;
		while (!finished) {
			HashSet<Object> newObjectsToFollow = new HashSet<>(10000);
			log.info("[" + stackCounter.get() + "] Objects to follow queue size=" + moreObjectsToFollow.size());
			Iterator it = moreObjectsToFollow.iterator();
			while (it.hasNext()) {
				// try disconnecting vertices
				Object next = it.next();
				if (!visited.contains(next)) {
					deepDeproxy(next, visited, newObjectsToFollow, stackCounter);
				}
			}

			moreObjectsToFollow.clear();
			log.info("Adding "+newObjectsToFollow.size()+" objects to follow");
			moreObjectsToFollow = newObjectsToFollow;
			it = moreObjectsToFollow.iterator();
			finished = !it.hasNext();
		}

		// Ease garbage collection
		visited.clear();
		moreObjectsToFollow.clear();

		return results;
	}

	private T deepDeproxy(final Object maybeProxy, final Set<Object> visited, HashSet<Object> moreObjectsToFollow,
			AtomicInteger stackCounter) throws ClassCastException {
		if (maybeProxy == null)
			return null;
		stackCounter.incrementAndGet();

		Class clazz;
		Hibernate.initialize(maybeProxy);
		if (maybeProxy instanceof HibernateProxy) {
			HibernateProxy proxy = (HibernateProxy) maybeProxy;
			LazyInitializer li = proxy.getHibernateLazyInitializer();
			clazz = li.getImplementation().getClass();
		} else {
			clazz = maybeProxy.getClass();
		}

		T ret = (T) deepDeproxy(maybeProxy, clazz);
		if (visited.contains(ret)) {
			// log.info("["+stackCounter.get()+"] Already finished
			// "+ret.getClass().getSimpleName()+"/"+ret.hashCode());
			stackCounter.decrementAndGet();
			return ret;
		}
		// log.info("["+stackCounter.get()+"] Deproxying
		// "+ret.getClass().getSimpleName()+"/"+ret.hashCode());
		visited.add(ret);

		if (ret instanceof NeptuneObject) {
			((NeptuneObject) ret).setId(null);
			((NeptuneObject) ret).setDetached(true);
			
		}

		if (ret instanceof Object[] || ret instanceof Set || ret instanceof Map || ret instanceof List) {
			// Deproxy elements of collection
			if (ret instanceof Object[]) {
				Object[] valueArray = (Object[]) ret;
				for (int i = 0; i < valueArray.length; i++) {
					valueArray[i] = deepDeproxy(valueArray[i], visited, moreObjectsToFollow, stackCounter);
				}
			} else if (ret instanceof Set) {
				Set valueSet = (Set) ret;
				Set result = new HashSet();
				for (Object o : valueSet) {
					result.add(deepDeproxy(o, visited, moreObjectsToFollow, stackCounter));
				}
				valueSet.clear();
				valueSet.addAll(result);
			} else if (ret instanceof Map) {
				Map valueMap = (Map) ret;
				Map result = new HashMap();
				for (Object o : valueMap.keySet()) {
					result.put(deepDeproxy(o, visited, moreObjectsToFollow, stackCounter),
							deepDeproxy(valueMap.get(o), visited, moreObjectsToFollow, stackCounter));
				}
				valueMap.clear();
				valueMap.putAll(result);
			} else if (ret instanceof List) {
				List valueList = (List) ret;
				for (int i = 0; i < valueList.size(); i++) {
					valueList.set(i, deepDeproxy(valueList.get(i), visited, moreObjectsToFollow, stackCounter));
				}
			}
		} else {
			for (PropertyDescriptor property : PropertyUtils.getPropertyDescriptors(ret)) {
				try {
					String name = property.getName();
					if (!"owner".equals(name) && property.getWriteMethod() != null) {
						Object value = PropertyUtils.getProperty(ret, name);

						boolean needToSetProperty = false;
						if (value instanceof HibernateProxy) {
							value = deepDeproxy(value, visited, moreObjectsToFollow, stackCounter);
							needToSetProperty = true;
						}

						if (value instanceof Object[]) {
							Object[] valueArray = (Object[]) value;
							Object[] result = (Object[]) Array.newInstance(value.getClass(), valueArray.length);
							for (int i = 0; i < valueArray.length; i++) {
								result[i] = valueArray[i];
								// result[i] = deepDeproxy(valueArray[i],
								// visited, moreObjectsToFollow);
							}
							value = result;
							needToSetProperty = true;
							moreObjectsToFollow.add(result);
							if (result.length > 0) {
								moreObjectsToFollow.add(result);
							}
						} else if (value instanceof Set) {
							Set valueSet = (Set) value;
							Set result = new HashSet();
							for (Object o : valueSet) {
								// result.add(deepDeproxy(o, visited,
								// moreObjectsToFollow));
								result.add(o);
							}
							value = result;
							needToSetProperty = true;
							valueSet.clear();
							if (result.size() > 0) {
								moreObjectsToFollow.add(result);
							}
						} else if (value instanceof Map) {
							Map valueMap = (Map) value;
							Map result = new HashMap();
							for (Object o : valueMap.keySet()) {
								// result.put(deepDeproxy(o, visited,
								// moreObjectsToFollow),
								// deepDeproxy(valueMap.get(o), visited,
								// moreObjectsToFollow));
								result.put(o, valueMap.get(o));
							}
							value = result;
							needToSetProperty = true;
							valueMap.clear();
							if (result.size() > 0) {
								moreObjectsToFollow.add(result);
							}
						} else if (value instanceof List) {
							List valueList = (List) value;
							List result = new ArrayList(valueList.size());
							// Iterating over collection creates a new iterator
							// which gives ConcurrentModificationException when
							// traversing the Chouette graph
							Object[] array = valueList.toArray();

							for (Object o : array) {
								result.add(o);
								// result.add(deepDeproxy(o, visited,
								// moreObjectsToFollow));
							}
							value = result;
							needToSetProperty = true;
							valueList.clear();
							if (result.size() > 0) {
								moreObjectsToFollow.add(result);
							}
						}
						if (needToSetProperty) {
							PropertyUtils.setProperty(ret, name, value);
						}
						if (value instanceof NeptuneObject) {
							// Follow any Neptune data relations to discover
							// more
							// proxies
							// log.info("Delaying following of object
							// "+value.getClass().getSimpleName());
							moreObjectsToFollow.add(value);
						}
					}
				} catch (java.lang.IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}

		}

		stackCounter.decrementAndGet();
		return ret;
	}

	private <T> T deepDeproxy(Object maybeProxy, Class<T> baseClass) throws ClassCastException {
		if (maybeProxy == null)
			return null;
		if (maybeProxy instanceof HibernateProxy) {
			return baseClass.cast(((HibernateProxy) maybeProxy).getHibernateLazyInitializer().getImplementation());
		} else {
			return baseClass.cast(maybeProxy);
		}
	}
}
