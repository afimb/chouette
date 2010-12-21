// @@LICENCE@@
//----------------------------------------------------------------------------
/**
 * Socit DRYADE
 *
 * Projet chouette : dfinition des types de donnes spcifiques 
 * 					 mis  disposition du mapping Base <-> Objet 
 * 					(package fr.certu.chouette.customtypes)
 *
 * DPOITypeUserType.java : Type de donne bas sur l'numration WordOrderType
 *
 * Historique des modifications :
 * Date         | Auteur         | Libll
 * -------------+----------------+-----------------------------------------------
 * 16 nov. 2004 |Marc FLORISSON  | Cration
 * -------------+----------------+-----------------------------------------------
 */
//----------------------------------------------------------------------------
package fr.certu.chouette.dao.hibernate.customtype;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import chouette.schema.types.WordOrderType;


//----------------------------------------------------------------------------
/**
 * Type de donne bas sur l'numration WordOrderType
 * 
 * La structure implmente l'interface des dfinitions de type UserType.
 *  
 * Version : $Revision: 1.3 $
 */
//----------------------------------------------------------------------------
public class WordOrderTypeUserType implements UserType 
{
	//------------------------------------------------------------
	/**************** Variables d'instance **********************/
	private static final int[] SQL_TYPES = {Types.VARCHAR};
	
	//-------------------------------------------------------------------
	/************** Mthodes de l'interface UserType *******************/

	public Object deepCopy(Object arg0) throws HibernateException {
		return arg0;
	}
	public boolean equals(Object arg0, Object arg1) throws HibernateException {
		return arg0 == arg1;
	}
	public boolean isMutable() {
		return false;
	}
	public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
			throws HibernateException, SQLException 
	{
		String name = resultSet.getString(names[0]);
		return resultSet.wasNull() ? null : WordOrderType.fromValue(name);	
	}
	public void nullSafeSet(PreparedStatement statement, Object value, int index)
			throws HibernateException, SQLException 
	{
		if (value == null) 
		{
			statement.setNull(index, Types.VARCHAR);
		} else {
			statement.setString(index, value.toString());
		}
	}
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
       return cached;
   }

   public Serializable disassemble(Object value) throws HibernateException {
       return (Serializable)value;
   }

   public Object replace(Object original, Object target, Object owner) throws HibernateException {
       return original;
   }
   public int hashCode(Object x) throws HibernateException {
       return x.hashCode();
   }
	public Class returnedClass() {
		return WordOrderType.class;
	}
	public int[] sqlTypes() {
		return SQL_TYPES;
	}
}
