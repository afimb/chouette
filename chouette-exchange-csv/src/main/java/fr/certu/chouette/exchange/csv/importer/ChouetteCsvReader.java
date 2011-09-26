/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.csv.importer;

import java.io.IOException;
import java.io.Reader;

import lombok.Getter;

import au.com.bytecode.opencsv.CSVReader;

/**
 *
 */
public class ChouetteCsvReader extends CSVReader
{

   @Getter private int rowNum = 0;
   /**
    * @param arg0
    */
   public ChouetteCsvReader(Reader arg0)
   {
      super(arg0);
      // TODO Auto-generated constructor stub
   }

   /**
    * @param arg0
    * @param arg1
    */
   public ChouetteCsvReader(Reader arg0, char arg1)
   {
      super(arg0, arg1);
      // TODO Auto-generated constructor stub
   }

   /**
    * @param arg0
    * @param arg1
    * @param arg2
    */
   public ChouetteCsvReader(Reader arg0, char arg1, char arg2)
   {
      super(arg0, arg1, arg2);
      // TODO Auto-generated constructor stub
   }

   /**
    * @param arg0
    * @param arg1
    * @param arg2
    * @param arg3
    */
   public ChouetteCsvReader(Reader arg0, char arg1, char arg2, int arg3)
   {
      super(arg0, arg1, arg2, arg3);
      // TODO Auto-generated constructor stub
   }

   /* (non-Javadoc)
    * @see au.com.bytecode.opencsv.CSVReader#readNext()
    */
   @Override
   public String[] readNext() throws IOException
   {
      rowNum++;
      return super.readNext();
   }

   
}
