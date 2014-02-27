/**
 * 
 */
package fr.certu.chouette.plugin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author michel
 * 
 */
@Entity
@Table(name = "compliance_check_tasks")
@NoArgsConstructor
public class Referential extends ActiveRecordObject
{

   private static final long serialVersionUID = 662218958334287711L;
   
   @Getter
   @Setter
   @Column(name = "organisation_id", nullable = false, unique = true)
   private Long organisationId;
   
   @Getter
   @Setter
   @Column(name="name")
   private String name;
   
   @Getter
   @Setter
   @Column(name="slug")
   private String slug;
   
   @Getter
   @Setter
   @Column(name="projection_type")
   private String prefix;
   
   @Getter
   @Setter
   @Column(name="")
   private String projectionType;
   
   @Getter
   @Setter
   @Column(name="time_zone")
   private String timeZone;
   
   @Getter
   @Setter
   @Column(name="bounds")
   private String bounds;
   
}
