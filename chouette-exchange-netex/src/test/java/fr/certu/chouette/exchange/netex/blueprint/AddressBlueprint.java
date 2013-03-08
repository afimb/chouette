package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import fr.certu.chouette.model.neptune.type.Address;

@Blueprint(Address.class)
public class AddressBlueprint {
    
    @Default
    String streetName = "Mon adresse";

}
