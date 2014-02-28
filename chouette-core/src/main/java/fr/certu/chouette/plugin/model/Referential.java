/**
 * 
 */
package fr.certu.chouette.plugin.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author michel
 * 
 */
@Entity
@Table(name = "referentials", schema = "public")
@NoArgsConstructor
public class Referential extends ActiveRecordObject
{

   private static final long serialVersionUID = 662218958334287711L;

   @Getter
   @Setter
   @ManyToOne
   @JoinColumn(name = "organisation_id", nullable = false)
   private Organisation organisation;

   @Getter
   @Setter
   @Column(name = "name")
   private String name;

   @Getter
   @Setter
   @Column(name = "slug")
   private String slug;

   @Getter
   @Setter
   @Column(name = "projection_type")
   private String prefix;

   @Getter
   @Setter
   @Column(name = "projection_type", insertable = false, updatable = false)
   private String projectionType;

   @Getter
   @Setter
   @Column(name = "time_zone")
   private String timeZone;

   @Getter
   @Setter
   @Column(name = "bounds")
   private String bounds;

   @Getter
   @Setter
   @OneToMany(mappedBy = "referential")
   private List<ImportTask> importTasks = new ArrayList<ImportTask>(0);

   @Getter
   @Setter
   @OneToMany(mappedBy = "referential")
   private List<CompilanceCheckTask> compilanceCheckTasks = new ArrayList<CompilanceCheckTask>(0);

   @Getter
   @Setter
   @OneToMany(mappedBy = "referential")
   private List<GuiExport> guiExports = new ArrayList<GuiExport>(0);

   @Column(name = "geographical_bounds")
   private String geographicalBounds;

   @Column(name = "organisation_id", insertable = false, updatable = false)
   private Long organisationId;

   @Column(name = "user_id")
   private Long userId;

   @Column(name = "user_name")
   private String userName;

}
