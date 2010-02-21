package fr.certu.chouette.util;

public final class ClassUtils {

	public static Object getClassInstanceFromPackageAndName (final String packageName, final String className) throws Exception {
		
		if (packageName == null || packageName.isEmpty() || className == null || className.isEmpty()) {
			
			throw new IllegalArgumentException("UN NOM DE PACKAGE ET UN NOM DE CLASSE DOIVENT ETRE OBLIGATOIREMENT FOURNITS AFIN DE CREER UNE INSTANCE D'UNE CLASSE ! LE NOM DE PACKAGE FOURNIT EST / " + packageName + " ET LE NOM DE CLASSE FOURNIT EST / " + className);
		}

		return getClassInstanceFromFullName (packageName + "." + className);
	}
	
	public static Object getClassInstanceFromFullName (final String classFullName) throws Exception {
		
		try {
			
			return Class.forName(classFullName).newInstance();
			
		} catch (Exception e) {

			// ClassNotFoundException, InstantiationException, IllegalAccessException
			throw new Exception("UNE ERREUR EST SURVENUE A L'INSTANTIATION DE LA CLASSE / " + classFullName + " ", e);
		} 
	}
}
