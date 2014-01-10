package fr.certu.chouette.model.neptune;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Neptune GroupOfLine : to associate lines with common purpose
 * <p/>
 * Note for fields comment : <br/>
 * when readable is added to comment, a implicit getter is available <br/>
 * when writable is added to comment, a implicit setter is available
 */
public class GroupOfLine extends NeptuneIdentifiedObject
{

   private static final long serialVersionUID = 2900948915585746984L;
   /**
    * grouped Lines ObjectIds <br/>
    * (import/export usage) <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<String>      lineIds;
   /**
    * grouped Lines <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private List<Line>        lines;
   /**
    * Comment <br/>
    * <i>readable/writable</i>
    */
   @Getter
   @Setter
   private String            comment;

   /**
    * add a lineId to list only if not already present <br/>
    * do not affect lines list
    * 
    * @param lineId
    *           lineId to remove
    */
   public void addLineId(String lineId)
   {
      if (lineIds == null)
         lineIds = new ArrayList<String>();
      if (!lineIds.contains(lineId))
         lineIds.add(lineId);
   }

   /**
    * add a line to list only if not already present <br/>
    * do not affect lineIds list
    * 
    * @param line
    *           line to add
    */
   public void addLine(Line line)
   {
      if (lines == null)
         lines = new ArrayList<Line>();
      if (!lines.contains(line))
         lines.add(line);
   }

   /**
    * remove a line from the group
    * 
    * @param line
    */
   public void removeLine(Line line)
   {
      if (lines == null)
         lines = new ArrayList<Line>();
      if (lines.contains(line))
         lines.remove(line);
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.model.neptune.NeptuneIdentifiedObject#complete()
    */
   @Override
   public void complete()
   {
      if (isCompleted()) return;
      super.complete();

      if (lines != null)
      {
         lineIds = extractObjectIds(lines);
      }
      else
      {
         lineIds = new ArrayList<String>();
      }
   }
   
	@Override
	public <T extends NeptuneObject> boolean compareAttributes(
			T anotherObject) {
		if (anotherObject instanceof GroupOfLine)
		{
			GroupOfLine another = (GroupOfLine) anotherObject;
			if (!sameValue(this.getObjectId(), another.getObjectId())) return false;
			if (!sameValue(this.getObjectVersion(), another.getObjectVersion())) return false;
			if (!sameValue(this.getName(), another.getName())) return false;
			if (!sameValue(this.getComment(), another.getComment())) return false;
			if (!sameValue(this.getRegistrationNumber(), another.getRegistrationNumber())) return false;

			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public String toURL() 
	{
		return "group_of_lines/"+getId();
	}


}
