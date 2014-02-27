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
public class Organisation extends ActiveRecordObject
{
   private static final long serialVersionUID = 8673888363745200271L;
   
   @Getter
   @Setter
   @Column(name="name")
   private String name;
}
