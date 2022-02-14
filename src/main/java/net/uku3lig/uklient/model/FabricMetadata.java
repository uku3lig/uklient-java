package net.uku3lig.uklient.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class FabricMetadata {
    @XmlElement(name = "versioning")
    private final Versioning versioning;

    @Data
    public static class Versioning {
        private final String release;
    }
}
