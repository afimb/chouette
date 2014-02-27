/**
 * 
 */
package fr.certu.chouette.plugin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.Type;
import org.json.JSONObject;

/**
 * @author michel
 * 
 */
@Entity
@Table(name = "import_tasks")
@NoArgsConstructor
public class ImportTask extends ActiveRecordObject
{

   private static final long serialVersionUID = 896326851460511494L;
   
   @Getter
   @Setter
   @Column(name = "referential_id", nullable = false, unique = true)
   private Long referentialId;
   
   @Getter
   @Setter
   @Column(name = "status")
   private String status;
   
   @Getter
   @Setter
   @Column(name = "parameter_set")
   @Type(type="fr.certu.chouette.plugin.model.JsonTextUserType")
   private JSONObject parameters;
   
   @Getter
   @Setter
   @Column(name = "user_id")
   private Long userId;
   
   @Getter
   @Setter
   @Column(name = "user_name")
   private String userName;
   
   @Getter
   @Setter
   @Column(name = "progress_info")
   @Type(type="fr.certu.chouette.plugin.model.JsonTextUserType")
   private JSONObject progressInfo;
   
   @Getter
   @Setter
   @Column(name = "result")
   @Type(type="fr.certu.chouette.plugin.model.JsonTextUserType")
   private JSONObject result;
   
   @Getter
   @Setter
   @OneToOne(fetch=FetchType.LAZY, mappedBy = "importTask")
   private CompilanceCheckTask compilanceCheckTask;

}
