package fr.certu.chouette.model.neptune;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/*
id         | bigint                      | not null default nextval('tatro.footnotes_id_seq'::regclass)
line_id    | bigint                      | 
code       | character varying(255)      | 
label      | character varying(255)      | 
created_at | timestamp without time zone | not null
updated_at | timestamp without time zone | not null
*/

@Log4j
@Entity
@Table(name = "footnotes")
@NoArgsConstructor
public class Footnote extends NeptuneObject
{
   /**
    * 
    */
   private static final long serialVersionUID = -6223882293500225313L;


   /**
    * referenced line
    * 
    * @param line
    *           new line
    * @return The actual line
    */
   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "line_id")
   private Line line;

   /**
    * label
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "label")
   private String  label;
   /**
    * set label <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setLabel(String value)
   {
      label = dataBaseSizeProtectedValue(value, "label", log);
   }

   /**
    * code
    * 
    * @return The actual value
    */
   @Getter
   @Column(name = "code")
   private String  code;
   /**
    * set code <br/>
    * truncated to 255 characters if too long
    * 
    * @param value
    *           New value
    */
   public void setCode(String value)
   {
      code = dataBaseSizeProtectedValue(value, "code", log);
   }

   /**
    * creation time
    * 
    * @param createdAt
    *           new creation time
    * @return The actual creation time
    */
   @Getter
   @Setter
   @Column(name = "created_at")
   private Date createdAt = GregorianCalendar.getInstance().getTime();

   /**
    * last update time
    * 
    * @param updatedAt
    *           new last update time
    * @return The actual last update time
    */
   @Getter
   @Setter
   @Column(name = "updated_at")
   private Date updatedAt = new Date(createdAt.getTime());

   /**
    * relative key for inport/export
    * 
    * should be unique for each line
    * 
    * @param key
    *           new key
    * @return The actual key
    */
   @Getter
   @Setter
   @Transient
   private String key;

   
   @Override
   public <T extends NeptuneObject> boolean compareAttributes(T another)
   {
      // TODO Auto-generated method stub
      return false;
   }


}
