/**
 * 
 */
package fr.certu.chouette.plugin.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author michel
 * 
 */

@Entity
@Table(name = "exports", schema = "public")
@NoArgsConstructor
public class GuiExport extends ActiveRecordObject
{

   private static final long serialVersionUID = -8663496142103716594L;

   @Getter
   @Setter
   @ManyToOne
   @JoinColumn(name = "referential_id", nullable = false)
   private Referential referential;

   @Getter
   @Setter
   @Column(name = "status")
   private String status;

   @Getter
   @Setter
   @Column(name = "type")
   private String type;

   @Getter
   @Setter
   @Column(name = "options")
   private String options;

   @Getter
   @Setter
   @Column(name = "references_type")
   private String referencesType;

   @Getter
   @Setter
   @Column(name = "reference_ids")
   private String referenceIds;

   @Column(name = "referential_id", insertable = false, updatable = false)
   private Long referentialId;

}
