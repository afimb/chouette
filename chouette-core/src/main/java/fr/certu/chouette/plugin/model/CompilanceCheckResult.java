package fr.certu.chouette.plugin.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "compliance_check_results", schema = "public")
@NoArgsConstructor
public class CompilanceCheckResult extends ActiveRecordObject
{
   private static final long serialVersionUID = 4117569061261296218L;

   @Getter
   @Column(name = "rule_code")
   private String ruleCode;

   @Getter
   @Setter
   @Column(name = "rule_format")
   private String ruleFormat;

   @Getter
   @Setter
   @Column(name = "rule_target")
   private String ruleTarget;

   @Getter
   @Setter
   @Column(name = "rule_level")
   private Integer ruleLevel;

   @Getter
   @Setter
   @Column(name = "rule_number")
   private Integer ruleNumber;

   @Getter
   @Setter
   @Column(name = "severity")
   private String severity;

   @Getter
   @Setter
   @Column(name = "status")
   private String status;

   @Getter
   @Setter
   @Column(name = "violation_count")
   private Integer violationCount;

   @Getter
   @Setter
   @Type(type = "fr.certu.chouette.plugin.model.JsonTextUserType")
   private JSONObject detail;

   @Getter
   @Setter
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "compliance_check_task_id")
   private CompilanceCheckTask compilanceCheckTask;

   public void setRuleCode(String code)
   {
      ruleCode = code;
      String[] items = code.split("-");
      if (items.length == 4)
      {
         ruleLevel = Integer.valueOf(items[0]);
         ruleFormat = items[1];
         ruleTarget = items[2];
         ruleNumber = Integer.valueOf(items[3]);
      }
      else if (items.length == 3)
      {
         ruleLevel = Integer.valueOf(items[0]);
         ruleFormat = "";
         ruleTarget = items[1];
         ruleNumber = Integer.valueOf(items[2]);
      }
   }

}
