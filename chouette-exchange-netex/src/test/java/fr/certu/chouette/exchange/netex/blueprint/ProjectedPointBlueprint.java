/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import java.math.BigDecimal;
import java.util.UUID;

@Blueprint(ProjectedPoint.class)
public class ProjectedPointBlueprint {
    
    @Default
    String projectionType = "EPSG:9801";
    
    @Default
    BigDecimal x = new BigDecimal( 602747 + ( UUID.randomUUID().getLeastSignificantBits()%100));
    
    @Default
    BigDecimal y = new BigDecimal( 2431390 + ( UUID.randomUUID().getLeastSignificantBits()%100));
    
}
