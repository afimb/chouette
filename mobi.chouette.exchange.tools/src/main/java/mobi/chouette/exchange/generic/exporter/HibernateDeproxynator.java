package mobi.chouette.exchange.generic.exporter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import mobi.chouette.model.NeptuneObject;

public class HibernateDeproxynator<T> {
	public List deepDeproxy(final List maybeProxy) throws ClassCastException {
		if (maybeProxy == null)
			return null;
		HashSet<Object> visited = new HashSet<>();

		List<T> results = new ArrayList<T>();
		for (Object x : maybeProxy) {
			T ret = deepDeproxy(x, visited);
			results.add(ret);
		}

		return results;
	}

	private T deepDeproxy(final Object maybeProxy, final HashSet<Object> visited) throws ClassCastException {
		if (maybeProxy == null)
			return null;
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
			return ret;
		}
		visited.add(ret);

		if (ret instanceof NeptuneObject) {
			((NeptuneObject) ret).setId(null);
		}

		for (PropertyDescriptor property : PropertyUtils.getPropertyDescriptors(ret)) {
			try {
				String name = property.getName();
				if (!"owner".equals(name) && property.getWriteMethod() != null) {
					Object value = PropertyUtils.getProperty(ret, name);

					boolean needToSetProperty = false;
					if (value instanceof HibernateProxy) {
						value = deepDeproxy(value, visited);
						needToSetProperty = true;
					}

					if (value instanceof Object[]) {
						Object[] valueArray = (Object[]) value;
						Object[] result = (Object[]) Array.newInstance(value.getClass(), valueArray.length);
						for (int i = 0; i < valueArray.length; i++) {
							result[i] = deepDeproxy(valueArray[i], visited);
						}
						value = result;
						needToSetProperty = true;
						// }
					} else if (value instanceof Set) {
						Set valueSet = (Set) value;
						Set result = new HashSet();
						for (Object o : valueSet) {
							result.add(deepDeproxy(o, visited));
						}
						value = result;
						needToSetProperty = true;
						// }
					} else if (value instanceof Map) {
						Map valueMap = (Map) value;
						Map result = new HashMap();
						for (Object o : valueMap.keySet()) {
							result.put(deepDeproxy(o, visited), deepDeproxy(valueMap.get(o), visited));
						}
						value = result;
						needToSetProperty = true;
					} else if (value instanceof List) {
						List valueList = (List) value;
						List result = new ArrayList(valueList.size());
						// Iterating over collection creates a new iterator
						// which gives ConcurrentModificationException when
						// traversing the Chouette graph
						Object[] array = valueList.toArray();

						for (Object o : array) {
							result.add(deepDeproxy(o, visited));
						}
						value = result;
						needToSetProperty = true;
					}
					if (needToSetProperty) {
						PropertyUtils.setProperty(ret, name, value);
					}
					if (value instanceof NeptuneObject) {
						// Follow any Neptune data relations to discover more
						// proxies
						deepDeproxy(value, visited);
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
