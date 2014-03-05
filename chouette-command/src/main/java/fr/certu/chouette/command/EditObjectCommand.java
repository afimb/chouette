/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.command;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.NoArgsConstructor;

import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

/**
 *
 */
/**
 * @author mamadou
 *
 */
@NoArgsConstructor
public class EditObjectCommand extends AbstractCommand
{

	private static final Logger logger = Logger.getLogger(EditObjectCommand.class);



	/**
	 * convert date string to calendar
	 * @param simpleval
	 * @return
	 */
	protected Calendar toCalendar(String simpleval)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try
		{
			Date d = sdf.parse(simpleval);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			return c;
		}
		catch (ParseException e)
		{
			logger.error("invalid date format : "+ simpleval+" dd/MM/yyyy expected");
			throw new RuntimeException("invalid date format : "+ simpleval+" dd/MM/yyyy expected");
		}

	}


	/**
	 * @param beans
	 * @param manager
	 * @param parameters 
	 * @throws ChouetteException
	 */
	public List<NeptuneIdentifiedObject> executeNew(
			INeptuneManager<NeptuneIdentifiedObject> manager, 
			Map<String, List<String>> parameters)
					throws ChouetteException 
					{

		NeptuneIdentifiedObject bean = 	manager.getNewInstance(null);
		List<NeptuneIdentifiedObject> beans = new ArrayList<NeptuneIdentifiedObject>();
		beans.add(bean);
		return beans;

					}



	/**
	 * @param beans
	 * @param parameters 
	 * @throws Exception 
	 */
	public void executeSet(List<NeptuneIdentifiedObject> beans, Map<String, List<String>> parameters) throws Exception 
	{
		updateAttribute("SET", beans, parameters);
	}

	/**
	 * @param beans
	 * @param parameters 
	 * @throws Exception 
	 */
	public void executeAdd(List<NeptuneIdentifiedObject> beans, Map<String, List<String>> parameters) throws Exception 
	{
		updateAttribute("ADD", beans, parameters);
	}
	/**
	 * @param beans
	 * @param parameters 
	 * @throws Exception 
	 */
	public void executeRemove(List<NeptuneIdentifiedObject> beans, Map<String, List<String>> parameters) throws Exception 
	{
		updateAttribute("REMOVE", beans, parameters);
	}


	/**
	 * @param cmd
	 * @param beans
	 * @param parameters
	 * @throws Exception
	 */
	private void updateAttribute(String cmd,List<NeptuneIdentifiedObject> beans, Map<String, List<String>> parameters) throws Exception 
	{
		if (beans.size() == 0)
		{
			throw new Exception("no bean to update, process stopped ");
		}
		if (beans.size() > 1)
		{
			throw new Exception("multiple beans to update, process stopped ");
		}
		NeptuneIdentifiedObject bean = beans.get(0);
		List<String> args = parameters.get("attr");
		if (args != null)
		{
			if (args.isEmpty())
			{
				throw new Exception ("command set -attr : missing arguments : name value");
			}
			String attrname = args.get(0);
			String value = null;
			if (args.size() > 1)
			{
				value = args.get(1);
			}
			ATTR_CMD c = ATTR_CMD.valueOf(cmd+"_VALUE");
			followAttribute(c, bean,attrname, value);
		}
		else
		{
			args = parameters.get("ref");
			if (args == null)
			{
				throw new Exception ("command set must have -attr or -ref argument");
			}
			if (args.isEmpty())
			{
				throw new Exception ("command set -ref : missing arguments : ref objectId");
			}
			String attrname = args.get(0);
			String value = null;
			if (args.size() > 1)
			{
				value = args.get(1);
			}
			ATTR_CMD c = ATTR_CMD.valueOf(cmd+"_REF");
			followAttribute(c, bean,attrname, value);
		}

	}
	/**
	 * @param beans
	 * @param parameters 
	 * @throws Exception 
	 */
	public void executeInfo(INeptuneManager<NeptuneIdentifiedObject> manager) throws Exception 
	{
		Object object = manager.getNewInstance(null);
		printFields(object,"");


	}

	/**
	 * @param object
	 * @throws Exception
	 */
	private void printFields(Object object,String indent) throws Exception 
	{
		try
		{
			Class<?> c = object.getClass();
			Field[] fields = c.getSuperclass().getDeclaredFields();
			for (Field field : fields) 
			{
				int m = field.getModifiers();
				if (Modifier.isPrivate(m) && !Modifier.isStatic(m))
				{
					printField(c,field,indent);
				}


			}

			fields = c.getDeclaredFields();
			for (Field field : fields) 
			{
				int m = field.getModifiers();
				if (Modifier.isPrivate(m) && !Modifier.isStatic(m))
				{
					printField(c,field,indent);
				}


			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @param objectType
	 * @param field
	 * @param indent
	 * @throws Exception
	 */
	private void printField(Class<?> objectType, Field field,String indent) throws Exception
	{
		String fieldName = field.getName().toLowerCase();
		if (fieldName.equals("importeditems")) return;
		if (fieldName.endsWith("id") || fieldName.endsWith("ids"))
		{
			if (!fieldName.equals("objectid") && !fieldName.equals("creatorid") && !fieldName.equals("areacentroid"))
				return;
		}
		if (findAccessor(objectType, field.getName(), "get", false) == null 
				&& findAccessor(objectType, field.getName(), "is", false) == null )	
		{
			return;
		}
		Class<?> type = field.getType();

		if (type.isPrimitive())
		{
			System.out.print(indent+"- "+field.getName());
			System.out.print(" : type "+type.getName());
			if (findAccessor(objectType, field.getName(), "set", false) == null)	
			{
				System.out.print(" (readonly)");
			}
		}
		else
		{
			if (type.getSimpleName().equals("List"))
			{
				String name = field.getName();
				name = name.substring(0,name.length()-1);
				ParameterizedType ptype = (ParameterizedType) field.getGenericType();
				Class<?> itemType = (Class<?>) ptype.getActualTypeArguments()[0];
				System.out.print(indent+"- "+name);
				System.out.print(" : collection of type "+itemType.getSimpleName());
				if (findAccessor(objectType, name, "add", false) != null)	
				{
					System.out.print(" (add allowed)");
				}
				if (findAccessor(objectType, name, "remove", false) != null)	
				{
					System.out.print(" (remove allowed)");
				}
				type = itemType;
			}
			else
			{
				System.out.print(indent+"- "+field.getName());
				System.out.print(" : type "+type.getSimpleName());
				if (findAccessor(objectType, field.getName(), "set", false) == null)	
				{
					System.out.print(" (readonly)");
				}
			}
		}
		System.out.println("");
		if (!type.isPrimitive())
			printFieldDetails(type, indent);
	}

	/**
	 * @param itemType
	 * @param indent
	 * @throws Exception
	 */
	private void printFieldDetails(Class<?> itemType, String indent)
			throws Exception 
			{
		String itemName = itemType.getName();
		if (itemName.startsWith("fr.certu.chouette.model.neptune.type."))
		{
			if (itemName.endsWith("Enum"))
			{
				Field[] fields = itemType.getDeclaredFields();
				System.out.print(indent+"     ");

				String text = "";
				for (Field field : fields) 
				{
					int m = field.getModifiers();
					if (Modifier.isPublic(m) && Modifier.isStatic(m) && Modifier.isFinal(m))
					{
						Object instance = field.get(null);
						String name = instance.toString();
						if (text.length() + name.length() > 79)
						{
							System.out.print(text+"\n"+indent+"     ");
							text = "";
						}
						text += name+" ";
					}
				}
				System.out.println(text);
			}
			else
			{
				Object instance = itemType.newInstance();
				printFields(instance, indent+"     ");
			}
		}
		else if (itemName.startsWith("fr.certu.chouette.model.neptune."))
		{
			Object instance = itemType.newInstance();
			if (instance instanceof NeptuneIdentifiedObject)
			{
				String simpleName = itemType.getSimpleName();
				if (simpleName.equals("AreaCentroid"))
				{
					printFields(instance, indent+"     ");
				}
			}
			else
			{
				printFields(instance, indent+"     ");
			}


		}
			}


	/**
	 * 
	 * @param object
	 * @param bean 
	 * @param attrname
	 * @param value
	 * @throws Exception
	 */
	private void followAttribute(ATTR_CMD cmd, Object object, String attrname,
			String value) 
					throws Exception
					{
		if (attrname.contains("."))
		{
			Class<?> type = object.getClass();
			String basename = attrname.substring(0,attrname.indexOf("."));
			Object target = null;
			if (basename.endsWith("]"))
			{
				String srank = basename.substring(basename.indexOf("[")+1, basename.indexOf("]"));
				basename = basename.substring(0, basename.indexOf("["));
				if (srank.equalsIgnoreCase("new"))
				{
					Method add = findAdder(type, basename);
					target = add.getParameterTypes()[0].newInstance();
					add.invoke(object, target);
				}
				else 
				{
					Method getter= findGetter(type, basename+"s");
					List<?> collection = (List<?>) getter.invoke(object);
					if (collection == null || collection.isEmpty()) 
					{
						throw new Exception("empty collection "+basename);
					}
					if (srank.equalsIgnoreCase("last"))
					{
						target = collection.get(collection.size()-1);
					}
					else
					{
						int rank = Integer.parseInt(srank);
						if (rank < 0 || rank >= collection.size())
						{
							throw new Exception("index "+rank+" out of collection bounds "+collection.size());
						}
						target = collection.get(rank);
					}
				}
			}
			else
			{
				Method getter = findGetter(type, basename);
				target = getter.invoke(object);
				if (target == null)
				{
					Class<?> targetType = getter.getReturnType();
					target = targetType.newInstance();
					Method setter = findSetter(type, basename);
					setter.invoke(object, target);
				}
			}
			attrname = attrname.substring(attrname.indexOf(".")+1);
			followAttribute(cmd, target, attrname, value);
		}
		else
		{
			switch (cmd)
			{
			case SET_VALUE : setAttribute(object, attrname, value); break;
			case ADD_VALUE : addAttribute(object, attrname, value); break;
			case REMOVE_VALUE : removeAttribute(object, attrname, value); break;
			case SET_REF : setReference(object, attrname, value); break;
			case ADD_REF : addReference(object, attrname, value); break;
			case REMOVE_REF : removeReference(object, attrname, value); break;
			}

		}

					}

	private void removeAttribute(Object object, String attrname, String value) throws Exception 
	{
		Class<?> beanClass = object.getClass();
		Method adder = findAdder(beanClass,attrname);
		Class<?> type = adder.getParameterTypes()[0];
		if (type.getName().startsWith("fr.certu.chouette.model.neptune") &&
				!type.getName().startsWith("Enum"))
		{
			type = Integer.TYPE;
		}
		else
		{

		}
		Method remover = findRemover(beanClass, attrname,type);
		Object arg = null;
		if (type.isEnum())
		{
			arg = toEnum(type,value);
		}
		else if (type.isPrimitive())
		{
			arg = toPrimitive(type,value);
		}
		else
		{
			arg = toObject(type,value);
		}
		remover.invoke(object, arg);

	}

	private void addAttribute(Object object, String attrname, String value) throws Exception 
	{
		Class<?> beanClass = object.getClass();
		Method adder = findAdder(beanClass, attrname);
		Class<?> type = adder.getParameterTypes()[0];
		Object arg = null;
		if (type.isEnum())
		{
			arg = toEnum(type,value);
		}
		else if (type.isPrimitive())
		{
			arg = toPrimitive(type,value);
		}
		else
		{
			arg = toObject(type,value);
		}
		adder.invoke(object, arg);

	}


	/**
	 * @param object
	 * @param attrname
	 * @param value
	 * @throws Exception
	 */
	private void setAttribute(Object object, String attrname, String value) throws Exception 
	{
		String name = attrname.toLowerCase();
		if (name.equals("id")) 
		{
			throw new Exception("non writable attribute id for any object , process stopped ");
		}
		if (!name.equals("objectid") && !name.equals("creatorid") && !name.equals("areacentroid")&& name.endsWith("id")) 
		{
			throw new Exception("non writable attribute "+attrname+" use setReference instand , process stopped ");
		}
		Class<?> beanClass = object.getClass();
		Method setter = findSetter(beanClass, attrname);
		Class<?> type = setter.getParameterTypes()[0];
		if (type.isArray() || type.getSimpleName().equals("List"))
		{
			throw new Exception("list attribute "+attrname+" for object "+beanClass.getName()+" must be update with (add/remove)Attribute, process stopped ");
		}
		Object arg = null;
		if (type.isEnum())
		{
			arg = toEnum(type,value);
		}
		else if (type.isPrimitive())
		{
			arg = toPrimitive(type,value);
		}
		else
		{
			arg = toObject(type,value);
		}
		setter.invoke(object, arg);
	}

	/**
	 * @param object
	 * @param refName
	 * @param objectId
	 * @throws Exception
	 */
	private void setReference(Object object,String refName, String objectId) throws Exception 
	{
		Class<?> beanClass = object.getClass();
		Method method = findSetter(beanClass, refName);
		updateReference(object, objectId, method);
	}

	/**
	 * @param object
	 * @param refName
	 * @param objectId
	 * @throws Exception
	 */
	private void addReference(Object object,String refName, String objectId) throws Exception 
	{
		Class<?> beanClass = object.getClass();
		Method method = findAdder(beanClass, refName);
		updateReference(object, objectId, method);
	}

	/**
	 * @param object
	 * @param refName
	 * @param objectId
	 * @throws Exception
	 */
	private void removeReference(Object object,String refName, String objectId) throws Exception 
	{
		Class<?> beanClass = object.getClass();
		Method method = findRemover(beanClass, refName,String.class);
		updateReference(object, objectId, method);
	}

	/**
	 * @param object
	 * @param objectId
	 * @param setter
	 * @throws Exception
	 */
	private void updateReference(Object object, String objectId, Method method)
			throws Exception {
		Class<?> type = method.getParameterTypes()[0];

		String typeName = type.getSimpleName().toLowerCase();
		INeptuneManager<NeptuneIdentifiedObject> manager = managers.get(typeName);
		if (manager == null)
		{
			throw new Exception("unknown object "+typeName+ ", only "+Arrays.toString(managers.keySet().toArray())+" are managed");
		}
		Filter filter = Filter.getNewEqualsFilter("objectId", objectId);
		NeptuneIdentifiedObject reference = manager.get(null, filter);
		if (reference != null) 
		{
			method.invoke(object, reference);
		}
		else
		{
			throw new Exception(typeName+" with ObjectId = "+objectId+" does not exists");
		}
	}



	/**
	 * @param beanClass
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	private Method findSetter(
			Class<?> beanClass, String attribute)
					throws Exception {
		return findAccessor(beanClass, attribute, "set",true);
	}
	/**
	 * @param beanClass
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	private Method findAccessor(
			Class<?> beanClass, String attribute, String prefix, boolean ex)
					throws Exception {
		String methodName = prefix+attribute;
		Method[] methods = beanClass.getMethods();
		Method accessor = null;
		for (Method method : methods) 
		{
			if (method.getName().equalsIgnoreCase(methodName))
			{
				accessor = method;
				break;
			}
		}
		if (ex && accessor == null)
		{
			throw new Exception("unknown accessor "+prefix+" for attribute "+attribute+" for object "+beanClass.getName()+", process stopped ");
		}
		return accessor;
	}
	/**
	 * @param beanClass
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	private Method findGetter(
			Class<?> beanClass, String attribute)
					throws Exception 
					{
		return findAccessor(beanClass, attribute, "get",true);
					}

	/**
	 * @param beanClass
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	private Method findAdder(
			Class<?> beanClass, String attribute)
					throws Exception {
		return findAccessor(beanClass, attribute, "add",true);
	}

	/**
	 * @param beanClass
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	private Method findRemover(
			Class<?> beanClass, String attribute, Class<?> argType)
					throws Exception 
					{
		String methodName = "remove"+attribute;
		Method[] methods = beanClass.getMethods();
		Method accessor = null;
		for (Method method : methods) 
		{
			if (method.getName().equalsIgnoreCase(methodName))
			{
				Class<?> parmType = method.getParameterTypes()[0];
				if (argType.equals(parmType))
				{
					accessor = method;
					break;
				}
			}
		}
		if (accessor == null)
		{
			throw new Exception("unknown accessor remove for attribute "+attribute+" for object "+beanClass.getName()+" with argument type = "+argType.getSimpleName()+", process stopped ");
		}
		return accessor;
					}


	private Object toObject(Class<?> type, String value) throws Exception 
	{
		if (value == null) return null;
		String name = type.getSimpleName();
		if (name.equals("String")) return value;
		if (name.equals("Long")) return Long.valueOf(value);
		if (name.equals("Boolean")) return Boolean.valueOf(value);
		if (name.equals("Integer")) return Integer.valueOf(value);
		if (name.equals("Float")) return Float.valueOf(value);
		if (name.equals("Double")) return Double.valueOf(value);
		if (name.equals("BigDecimal")) return BigDecimal.valueOf(Double.parseDouble(value));
		if (name.equals("Date")) 
		{
			DateFormat dateFormat = null;
			if (value.contains("-") && value.contains(":"))
			{
				dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
			}
			else if (value.contains("-") )
			{
				dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			}
			else if ( value.contains(":"))
			{
				dateFormat = new SimpleDateFormat("HH:mm:ss");
			}
			else
			{
				throw new Exception("unable to convert "+value+" to Date");
			}
			Date date = dateFormat.parse(value);
			if (type.equals(Date.class))
			{
			   return date;
			}
			else
			{
				return new java.sql.Date(date.getTime());
			}
				
		}
		if (name.equals("Time")) 
		{
			DateFormat dateFormat = null;
			if ( value.contains(":"))
			{
				dateFormat = new SimpleDateFormat("H:m:s");
			}
			else
			{
				throw new Exception("unable to convert "+value+" to Time");
			}
			Date date = dateFormat.parse(value);
			Time time = new Time(date.getTime());
			return time;
		}

		throw new Exception("unable to convert String to "+type.getCanonicalName());
	}

	private Object toPrimitive(Class<?> type, String value) throws Exception 
	{
		if (value == null) throw new Exception("primitive type "+type.getName()+" cannot be set to null");
		String name = type.getName();
		if (name.equals("long")) return Long.valueOf(value);
		if (name.equals("boolean")) return Boolean.valueOf(value);
		if (name.equals("int")) return Integer.valueOf(value);
		if (name.equals("float")) return Float.valueOf(value);
		if (name.equals("double")) return Double.valueOf(value);
		throw new Exception("unable to convert String to "+type.getName());
	}

	private Object toEnum(Class<?> type, String value) throws Exception 
	{
		Method m = type.getMethod("valueOf", String.class);
		return m.invoke(null, value);
	}




}
