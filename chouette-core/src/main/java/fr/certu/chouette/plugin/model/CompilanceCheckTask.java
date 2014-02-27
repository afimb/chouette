/**
 * 
 */
package fr.certu.chouette.plugin.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.hibernate.engine.spi.CascadingAction;
import org.json.JSONObject;

/**
 * @author michel
 * 
 */

@Entity
@Table(name = "compliance_check_tasks", schema = "public")
@NoArgsConstructor
public class CompilanceCheckTask extends ActiveRecordObject
{

   private static final long serialVersionUID = 5725000704712085992L;

   @Getter
   @Setter
   @ManyToOne
   @JoinColumn(name = "referential_id", nullable=false)
   private Referential referential;
   
   @Getter
   @Setter
   @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
   @OnDelete(action = OnDeleteAction.CASCADE)
   @JoinColumn(name="import_task_id")
   private ImportTask importTask;

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
   @OneToMany(mappedBy = "compilanceCheckTask", cascade=CascadeType.ALL)
   private List<CompilanceCheckResult> results;
   
   @Getter
   @Setter
   @Column(name = "references_type")
   private String referencesType;
   
   @Getter
   @Setter
   @Column(name = "reference_ids")
   private String referenceIds;

   public void addResult(CompilanceCheckResult result)
   {
      if (results == null)
         results = new ArrayList<CompilanceCheckResult>();
      results.add(result);
      result.setCompilanceCheckTask(this);
   }

   public void addAllResults(List<CompilanceCheckResult> results)
   {
      for (CompilanceCheckResult result : results)
      {
         addResult(result);
      }
   }

}
