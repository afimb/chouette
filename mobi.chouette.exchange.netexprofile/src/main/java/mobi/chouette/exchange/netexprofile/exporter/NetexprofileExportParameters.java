package mobi.chouette.exchange.netexprofile.exporter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractExportParameter;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "netex-export")
@NoArgsConstructor
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"projectionType","addExtension"})
public class NetexprofileExportParameters extends AbstractExportParameter {

    @Getter
    @Setter
    @XmlElement(name = "projection_type")
    private String projectionType;

    @Getter @Setter
    @XmlElement(name = "add_extension")
    private boolean addExtension = false;

    @Getter
    @Setter
    @XmlElement(name = "valid_codespaces", required = false)
    private String validCodespaces;

}
