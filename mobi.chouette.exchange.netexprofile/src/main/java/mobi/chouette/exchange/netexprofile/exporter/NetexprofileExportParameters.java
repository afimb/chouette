package mobi.chouette.exchange.netexprofile.exporter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractExportParameter;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "netexprofile-export")
@NoArgsConstructor
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"projectionType","addExtension", "object_id_prefix", "validCodespaces"})
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
    @XmlElement(name = "object_id_prefix", required = true)
    private String objectIdPrefix;

    @Getter
    @Setter
    @XmlElement(name = "valid_codespaces", required = true)
    private String validCodespaces;

}
