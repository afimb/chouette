/**
 * 
 */
package fr.certu.chouette.plugin.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "organisations", schema = "public")
@NoArgsConstructor
public class Organisation extends ActiveRecordObject
{
   private static final long serialVersionUID = 8673888363745200271L;
   
   @Getter
   @Setter
   @Column(name="name")
   private String name;
   
   @Getter
   @Setter
   @OneToMany(mappedBy = "organisation")
   private List<Referential> referentials = new ArrayList<Referential>(0);
}
