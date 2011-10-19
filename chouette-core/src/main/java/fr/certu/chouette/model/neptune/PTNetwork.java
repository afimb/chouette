/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;

/**
 * Neptune Public Transport Network
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
@SuppressWarnings("serial")
@NoArgsConstructor
public class PTNetwork extends NeptuneIdentifiedObject
{
   // TODO constant for persistence fields
   /**
    * name of comment attribute for {@link Filter} attributeName construction
    */
   public static final String    COMMENT                    = "comment";
   /**
    * Date when the network and it's dependencies has been referenced <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private Date                    versionDate;
   /**
    * A description of the network <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                  description;
   /**
    * Registration Number <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                  registrationNumber;
   /**
    * Source Name <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                  sourceName;
   /**
    * Source Identifier <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                  sourceIdentifier;
   /**
    * Database foreign key referring to the line's network<br/>
    * Meaningless after import action <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String                  comment;
   /**
    * Source Type <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private PTNetworkSourceTypeEnum pTNetworkSourceType;
   /**
    * List of the network lines Neptune Ids<br/>
    * After import, may content only lines imported<br/>
    * Meaningless after database loading <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<String>            lineIds;
   /**
    * List of the network lines <br/>
    * After import, may content only lines imported<br/>
    * readable from database but not updatable <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<Line>              lines;

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#toString(java.
    * lang.String, int)
    */
   @Override
   public String toString(String indent, int level)
   {
      SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
      StringBuilder sb = new StringBuilder(super.toString(indent, level));
      if (versionDate != null)
         sb.append("\n").append(indent).append("versionDate = ").append(f.format(versionDate));
      sb.append("\n").append(indent).append("description = ").append(description);
      sb.append("\n").append(indent).append("registrationNumber = ").append(registrationNumber);
      sb.append("\n").append(indent).append("sourceName = ").append(sourceName);
      sb.append("\n").append(indent).append("sourceIdentifier = ").append(sourceIdentifier);
      sb.append("\n").append(indent).append("comment = ").append(comment);

      if (lineIds != null)
      {
         sb.append("\n").append(indent).append(CHILD_ARROW).append("lineIds");
         for (String lineId : lineIds)
         {
            sb.append("\n").append(indent).append(CHILD_LIST_ARROW).append(lineId);
         }
      }

      return sb.toString();
   }

   /**
    * add a line Id to the network
    * 
    * @param lineId
    *           the line id to add
    */
   public void addLineId(String lineId)
   {
      if (lineIds == null)
         lineIds = new ArrayList<String>();
      lineIds.add(lineId);
   }

   /**
    * add a line Id to the network
    * 
    * @param line
    *           the line to add
    */
   public void addLine(Line line)
   {
      if (lines == null)
         lines = new ArrayList<Line>();
      if (!lines.contains(line))
         lines.add(line);
   }

   /**
    * remove a line
    * 
    * @param line
    *           the lien to remove
    */
   public void removeLine(Line line)
   {
      if (lines == null)
         lines = new ArrayList<Line>();
      if (lines.contains(line))
         lines.remove(line);
   }

}
