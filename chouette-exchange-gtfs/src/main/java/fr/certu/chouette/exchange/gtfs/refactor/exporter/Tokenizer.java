package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer
{
   public static final char LF = '\n';
   public static final char CR = '\r';
   public static final char DELIMITER = ',';
   public static final char DQUOTE = '"';

   public static final List<String> tokenize(String text)
   {

      final StringBuilder builder = new StringBuilder();
      final List<String> tokens = new ArrayList<String>();
      boolean escape = false;
      int length = text.length();

      // System.out.println("[DSU] text : " + text);

      for (int i = 0; i < length; i++)
      {
         final char c = text.charAt(i);
         if (!escape)
         {
            if (c == DELIMITER)
            {
               tokens.add(builder.toString());
               builder.delete(0, builder.length());
            } else if (i + 1 == length)
            {
               builder.append(c);
               tokens.add(builder.toString());
               builder.delete(0, builder.length());
            } else if (c == DQUOTE)
            {
               if (i == 0)
               {
                  escape = true;
               } else
               {
                  if (text.charAt(i + 1) == DQUOTE)
                  {
                     if (i + 2 < length)
                     {
                        if (text.charAt(i + 2) != DELIMITER)
                        {
                           // ""A
                           builder.append(c);
                           i++;
                           escape = true;
                        } else
                        {
                           // "",
                           escape = true;
                        }
                     } else
                     {
                        // ""eol
                        escape = true;
                     }
                  } else
                  {
                     escape = true;
                  }
               }
            } else
            {
               builder.append(c);
            }

         } else
         {
            if (c == DQUOTE)
            {
               if (i + 1 < length)
               {
                  if (text.charAt(i + 1) == DQUOTE)
                  {
                     builder.append(c);
                     i++;
                  } else
                  {
                     escape = false;
                  }
               } else
               {
                  escape = false;
                  tokens.add(builder.toString());
                  builder.delete(0, builder.length());
               }
            } else
            {
               builder.append(c);
            }
         }

      }
      // System.out.println("[DSU] text : " + tokens);

      return tokens;
   }

}
