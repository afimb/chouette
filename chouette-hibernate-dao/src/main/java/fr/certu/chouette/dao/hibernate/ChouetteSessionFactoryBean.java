/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.dao.hibernate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.hibernate.classic.Session;
import org.hibernate.jdbc.Work;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

import fr.certu.chouette.dao.hibernate.exception.HibernateDaoExceptionCode;
import fr.certu.chouette.dao.hibernate.exception.HibernateDaoRuntimeException;

/**
 * @author michel
 * 
 */
public class ChouetteSessionFactoryBean extends LocalSessionFactoryBean
{
   private static final Logger logger              = Logger.getLogger(ChouetteSessionFactoryBean.class);

   @Setter
   private String              strategy            = "validate";

   @Setter
   private String              getForeignKeysSql;
   @Setter
   private String              dropConstraintKeySql;
   @Setter
   private String              addForeignKeySql;

   @Setter
   private Map<String, String> cascadeDeleteFkeys  = new HashMap<String, String>();
   @Setter
   private Map<String, String> setNullDeleteFkeys  = new HashMap<String, String>();
   @Setter
   private Map<String, String> noActionDeleteFkeys = new HashMap<String, String>();

   @Setter
   private String              getPrimaryKeysSql;
   @Setter
   private String              addPrimaryKeySql;

   @Setter
   private List<PrimaryKeyDef> primaryKeys;

   @Override
   protected void afterSessionFactoryCreation() throws Exception
   {
      super.afterSessionFactoryCreation();
      logger.info("check schema after Hibernate processing");
      Session session = getSessionFactory().openSession();
      Work work = new CheckForeignKeysWork();
      session.doWork(work);
      work = new CheckPrimaryKeysWork();
      session.doWork(work);
      session.close();
   }

   private class CheckForeignKeysWork implements Work
   {

      @Override
      public void execute(Connection connection) throws SQLException
      {
         Statement stmt = connection.createStatement();
         // get existing Fkeys
         Map<String, FkeyConstraint> foreignKeys = new HashMap<String, FkeyConstraint>();
         ResultSet rst = stmt.executeQuery(getForeignKeysSql);
         while (rst.next())
         {
            FkeyConstraint fkey = new FkeyConstraint(rst);
            foreignKeys.put(fkey.getConstraintName(), fkey);
         }
         rst.close();

         for (String keyName : cascadeDeleteFkeys.keySet())
         {
            FkeyConstraint fkey = foreignKeys.remove(keyName);
            if (fkey == null)
            {
               logger.error("Foreign key " + keyName + " missing in schema");
               if (strategy.equals("update"))
               {
                  addRule(stmt, keyName, cascadeDeleteFkeys.get(keyName), "CASCADE");
               }
               else
               {
                  throw new HibernateDaoRuntimeException(HibernateDaoExceptionCode.DATABASE_SCHEMA_MISSING_FOREIGN_KEY,
                        keyName);
               }
            }
            else
            {
               checkRule(stmt, fkey, "CASCADE");
            }
         }
         for (String keyName : setNullDeleteFkeys.keySet())
         {
            FkeyConstraint fkey = foreignKeys.remove(keyName);
            if (fkey == null)
            {
               logger.error("Foreign key " + keyName + " missing in schema");
               if (strategy.equals("update"))
               {
                  addRule(stmt, keyName, setNullDeleteFkeys.get(keyName), "SET NULL");
               }
               else
               {
                  throw new HibernateDaoRuntimeException(HibernateDaoExceptionCode.DATABASE_SCHEMA_MISSING_FOREIGN_KEY,
                        keyName);
               }
            }
            else
            {
               checkRule(stmt, fkey, "SET NULL");
            }
         }
         for (String keyName : noActionDeleteFkeys.keySet())
         {
            FkeyConstraint fkey = foreignKeys.remove(keyName);
            if (fkey == null)
            {
               logger.error("Foreign key " + keyName + " missing in schema");
               if (strategy.equals("update"))
               {
                  addRule(stmt, keyName, noActionDeleteFkeys.get(keyName), "SET NULL");
               }
               else
               {
                  throw new HibernateDaoRuntimeException(HibernateDaoExceptionCode.DATABASE_SCHEMA_MISSING_FOREIGN_KEY,
                        keyName);
               }
            }
            else
            {
               checkRule(stmt, fkey, "NO ACTION");
            }
         }
         for (String keyName : foreignKeys.keySet())
         {
            Object[] args = foreignKeys.get(keyName).toArray();
            String message = MessageFormat.format("ForeignKey {1} on table {0},column {2} is out of check scope", args);
            logger.warn(message);
            if (strategy.startsWith("create") || (!keyName.endsWith("_fkey") && strategy.startsWith("update")))
            {
               String sql = MessageFormat.format(dropConstraintKeySql, args);
               stmt.executeUpdate(sql);
            }
         }
         stmt.close();
      }

      /**
       * @param stmt
       * @param fkey
       * @param rule
       * @throws SQLException
       */
      private void checkRule(Statement stmt, FkeyConstraint fkey, String rule) throws SQLException
      {
         if (!fkey.getDeleteRule().equals(rule))
         {
            if (strategy.equals("validate"))
            {
               throw new HibernateDaoRuntimeException(HibernateDaoExceptionCode.DATABASE_SCHEMA_WRONG_FOREIGN_KEY_TYPE,
                     fkey.getConstraintName(), rule);
            }
            fkey.setDeleteRule(rule);
            Object[] args = fkey.toArray();
            String sql = MessageFormat.format(dropConstraintKeySql, args);
            stmt.executeUpdate(sql);
            sql = MessageFormat.format(addForeignKeySql, args);
            stmt.executeUpdate(sql);
            String message = MessageFormat.format(
                  "ForeignKey {1} on table {0},column {2}: ON DELETE rule changed to {7}", args);
            logger.debug(message);
         }
      }

      /**
       * @param stmt
       * @param fkey
       * @param rule
       * @throws SQLException
       */
      private void addRule(Statement stmt, String keyName, String keyInfo, String rule) throws SQLException
      {

         FkeyConstraint fkey = new FkeyConstraint(keyName, keyInfo, rule);

         Object[] args = fkey.toArray();
         String sql = MessageFormat.format(addForeignKeySql, args);
         stmt.executeUpdate(sql);
         String message = MessageFormat.format("ForeignKey {1} on table {0},column {2}: ON DELETE rule created {7}",
               args);
         logger.debug(message);

      }

      private class FkeyConstraint
      {
         @Getter
         private String constraintName;
         @Getter
         private String tableName;
         @Getter
         private String columnName;
         @Getter
         private String targetTable;
         @Getter
         private String targetColumn;
         @Getter
         private String matchOption;
         @Getter
         private String updateRule;
         @Getter
         @Setter
         private String deleteRule;

         public FkeyConstraint(ResultSet rst) throws SQLException
         {
            constraintName = rst.getString("constraint_name");
            tableName = rst.getString("table_name");
            columnName = rst.getString("column_name");
            targetTable = rst.getString("target_table");
            targetColumn = rst.getString("target_column");
            matchOption = rst.getString("match_option");
            if (matchOption.equals("NONE"))
               matchOption = "SIMPLE";
            updateRule = rst.getString("update_rule");
            deleteRule = rst.getString("delete_rule");
         }

         public FkeyConstraint(String keyName, String keyInfo, String rule)
         {
            constraintName = keyName;
            updateRule = "NO ACTION";
            deleteRule = rule;
            matchOption = "SIMPLE";
            String[] infos = keyInfo.split(";");
            tableName = infos[0];
            columnName = infos[1];
            targetTable = infos[2];
            targetColumn = infos[3];
         }

         public Object[] toArray()
         {
            return new Object[] { tableName, constraintName, columnName, targetTable, targetColumn, matchOption,
                  updateRule, deleteRule };
         }
      }
   }

   private class CheckPrimaryKeysWork implements Work
   {

      @Override
      public void execute(Connection connection) throws SQLException
      {
         Statement stmt = connection.createStatement();
         // get existing PrimaryKeys
         Map<String, PkeyConstraint> pKeys = new HashMap<String, PkeyConstraint>();
         ResultSet rst = stmt.executeQuery(getPrimaryKeysSql);
         if (rst.next())
         {
            PkeyConstraint pkey = new PkeyConstraint(rst);
            pKeys.put(pkey.getTableName(), pkey);
            while (rst.next())
            {
               if (!pkey.addColumnName(rst))
               {
                  pkey = new PkeyConstraint(rst);
                  pKeys.put(pkey.getTableName(), pkey);
               }
            }
         }
         rst.close();
         // compare with definition
         for (PrimaryKeyDef keyDef : primaryKeys)
         {
            PkeyConstraint constraint = pKeys.get(keyDef.getTable());
            if (constraint == null)
            {
               if (strategy.equals("validate"))
               {
                  throw new HibernateDaoRuntimeException(HibernateDaoExceptionCode.DATABASE_SCHEMA_MISSING_PRIMARY_KEY,
                        keyDef.getKey(), keyDef.getTable());
               }
            }
            else
            {
               if (!constraint.getConstraintName().equals(keyDef.getKey())
                     || constraint.getColumnNames().equals(keyDef.getColumns()))
               {
                  if (strategy.equals("validate"))
                  {
                     throw new HibernateDaoRuntimeException(
                           HibernateDaoExceptionCode.DATABASE_SCHEMA_WRONG_PRIMARY_KEY, constraint.getConstraintName(),
                           keyDef.getTable());
                  }
                  String sql = MessageFormat.format(dropConstraintKeySql, constraint.toArray());
                  stmt.executeUpdate(sql);
                  logger.debug("removing old primary key definition " + constraint.getConstraintName() + " on "
                        + constraint.getTableName());
               }
               else
               {
                  continue;
               }
            }
            String sql = MessageFormat.format(addPrimaryKeySql, keyDef.toArray());
            stmt.executeUpdate(sql);
            logger.debug("creating primary key definition " + keyDef.getKey() + " on " + keyDef.getTable());
         }

         stmt.close();

      }

      private class PkeyConstraint
      {
         @Getter
         private String       constraintName;
         @Getter
         private String       tableName;
         @Getter
         private List<String> columnNames = new ArrayList<String>();

         public PkeyConstraint(ResultSet rst) throws SQLException
         {
            constraintName = rst.getString("constraint_name");
            tableName = rst.getString("table_name");
            columnNames.add(rst.getString("column_name"));
         }

         public boolean addColumnName(ResultSet rst) throws SQLException
         {
            String name = rst.getString("constraint_name");
            if (name.equals(constraintName))
            {
               columnNames.add(rst.getString("column_name"));
               return true;
            }
            return false;
         }

         public Object[] toArray()
         {
            return new Object[] { tableName, constraintName };
         }
      }
   }

}
