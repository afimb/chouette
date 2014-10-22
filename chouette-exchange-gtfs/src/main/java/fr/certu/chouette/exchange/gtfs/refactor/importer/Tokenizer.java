package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer
{

   public static final List<String> tokenize(String line)
   {
      final char DELIMITER = ',';
      final char DQUOTE = '"';

      final StringBuilder sb = new StringBuilder();
      final List<String> token = new ArrayList<String>();

      State state = State.NORMAL;

      int pointer = 0;
      int length = line.length();

      while (pointer < line.length())
      {
         final char c = line.charAt(pointer);

         switch (state)
         {
         case NORMAL:
            if (c == DELIMITER)
            {
               token.add(sb.toString());
               sb.delete(0, sb.length());
            }

            else if (c == DQUOTE)
            {
               if (sb.length() == 0)
               {
                  state = State.QUOTED;
               } else if ((pointer + 1 < line.length())
                     && line.charAt(pointer + 1) == DQUOTE && sb.length() > 0)
               {
                  sb.append(c);
                  pointer++;
               } else if ((pointer + 1 < line.length())
                     && line.charAt(pointer + 1) != DQUOTE)
               {
                  state = State.QUOTED;
               }
            } else
            {
               sb.append(c);
            }
            break;

         case QUOTED:

            if (c == DQUOTE)
            {
               if ((pointer + 1 < line.length())
                     && line.charAt(pointer + 1) == DQUOTE)
               {
                  sb.append(c);
                  pointer++;
                  break;
               } else
               {
                  state = State.NORMAL;
               }
            } else
            {
               sb.append(c);
            }
            break;
         }

         pointer++;
      }
      return token;
   }

   private enum State
   {
      NORMAL, QUOTED
   }
}
