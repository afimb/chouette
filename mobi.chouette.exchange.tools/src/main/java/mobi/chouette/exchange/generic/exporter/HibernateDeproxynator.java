package mobi.chouette.exchange.generic.exporter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.NeptuneObject;
import mobi.chouette.model.StopPoint;

@Log4j
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

	private T deepDeproxy(final Object maybeProxy, final HashSet<Object> visited)
			throws ClassCastException {
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

//		log.info("Deproxying " + maybeProxy.getClass().getSimpleName() + " with id "
//				+ (maybeProxy instanceof NeptuneIdentifiedObject ? ((NeptuneIdentifiedObject) maybeProxy).getObjectId()
//						: "<>"));
//
		T ret = (T) deepDeproxy(maybeProxy, clazz);
		if (visited.contains(ret)) {
//			log.info("Already finished with object " + ret.getClass().getSimpleName() + " with id "
//					+ (ret instanceof NeptuneIdentifiedObject ? ((NeptuneIdentifiedObject) ret).getObjectId() : "<>"));
			return ret;

		}
		visited.add(ret);

//		if (hashSet.contains(maybeProxy) && maybeProxy instanceof PersistentCollection) {
//			log.info("Already finished with proxy " + maybeProxy.getClass().getSimpleName() + " with id "
//					+ (maybeProxy instanceof NeptuneIdentifiedObject
//							? ((NeptuneIdentifiedObject) maybeProxy).getObjectId() : "<>"));
//			return (T) maybeProxy;
//
//		}
//
//		if (maybeProxy instanceof PersistentCollection) {
//			log.info("Adding collection proxy to finished set " + maybeProxy.getClass().getSimpleName());
//			hashSet.add(maybeProxy);
//		}

		
		if(ret instanceof NeptuneObject) {
			((NeptuneObject)ret).setId(null);
		}

		for (PropertyDescriptor property : PropertyUtils.getPropertyDescriptors(ret)) {
			try {
				String name = property.getName();
				if (!"owner".equals(name) && property.getWriteMethod() != null) {
					Object value = PropertyUtils.getProperty(ret, name);

//					if(ret instanceof StopPoint && name.equals("containedInStopArea")) {
//						log.info("StopPoint.containedInStopArea");
//					}

//					if(hashSet.contains(value)) {
//						log.info("Seen proxy "+value+" before, skipping");
//						continue;
//					}
//					
//					if(value instanceof AbstractPersistentCollection) {
//						hashSet.add(value);
//					}

					Object originalValue = value;
					boolean needToSetProperty = false;
					if (value instanceof HibernateProxy) {
						value = deepDeproxy(value, visited);
						needToSetProperty = true;
					}
					
					
					if (value instanceof Object[]) {
						Object[] valueArray = (Object[]) value;
						Object[] result = (Object[]) Array.newInstance(value.getClass(), valueArray.length);
//
//						if (!hashSet.contains(originalValue)) {
//							hashSet.add(originalValue);
//
							for (int i = 0; i < valueArray.length; i++) {
								result[i] = deepDeproxy(valueArray[i], visited);
							}
//							hashSet.remove(originalValue);
							value = result;
							needToSetProperty = true;
//						}
					} else if (value instanceof Set) {
						Set valueSet = (Set) value;
						Set result = new HashSet();
//						if (!hashSet.contains(originalValue)) {
//							hashSet.add(originalValue);
							for (Object o : valueSet) {
								result.add(deepDeproxy(o, visited));
							}
//							hashSet.remove(originalValue);
							value = result;
							needToSetProperty = true;
//						}
					} else if (value instanceof Map) {
						Map valueMap = (Map) value;
						Map result = new HashMap();
//						if (!hashSet.contains(originalValue)) {
//							hashSet.add(originalValue);
							for (Object o : valueMap.keySet()) {
								result.put(deepDeproxy(o, visited),
										deepDeproxy(valueMap.get(o), visited));
							}
//							hashSet.remove(originalValue);
							value = result;
							needToSetProperty = true;
//						}
					} else if (value instanceof List) {
						List valueList = (List) value;
						List result = new ArrayList(valueList.size());

						
						
//						log.info("About to iterate over " + ret.getClass().getSimpleName() + "." + name + " / "
//						+ valueList.hashCode() + " with id " + (ret instanceof NeptuneIdentifiedObject
//								? ((NeptuneIdentifiedObject) ret).getObjectId() : "<>"));
//						if (!hashSet.contains(originalValue)) {
						//	hashSet.add(originalValue);

//							log.info("About to iterate over " + ret.getClass().getSimpleName() + "." + name + " / "
//									+ valueList.hashCode() + " with id " + (ret instanceof NeptuneIdentifiedObject
//											? ((NeptuneIdentifiedObject) ret).getObjectId() : "<>"));
							
//							try {
								Object[] array = valueList.toArray();
								
								for (Object o : array) {
									result.add(deepDeproxy(o, visited));
								}
								
//							} catch (ConcurrentModificationException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							log.info("Completed iterating over " + ret.getClass().getSimpleName() + "." + name + " / "
//									+ valueList.hashCode() + " with id " + (ret instanceof NeptuneIdentifiedObject
//											? ((NeptuneIdentifiedObject) ret).getObjectId() : "<>"));
//
//							hashSet.remove(originalValue);

							value = result;
							needToSetProperty = true;
//						} else {
//							log.info("Already iterating over " + ret.getClass().getSimpleName() + "." + name + " / "
//									+ valueList.hashCode() + " with id " + (ret instanceof NeptuneIdentifiedObject
//											? ((NeptuneIdentifiedObject) ret).getObjectId() : "<>"));
//
//						}
					}
					if (needToSetProperty) {
//						log.info("Updating "+ret.getClass().getSimpleName()+"."+name+" with value "+value.getClass().getSimpleName());
						PropertyUtils.setProperty(ret, name, value);
						
					}
					if (value instanceof NeptuneObject) {
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
