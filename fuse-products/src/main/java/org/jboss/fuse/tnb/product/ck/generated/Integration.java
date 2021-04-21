
package org.jboss.fuse.tnb.product.ck.generated;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

import org.jboss.fuse.tnb.product.ck.utils.CamelKSettings;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSupport;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Group(CamelKSupport.CAMELK_CRD_GROUP)
@Version(CamelKSettings.API_VERSION_DEFAULT)
public class Integration extends CustomResource<IntegrationSpec, IntegrationStatus> {

    public Integration() {
        super();
        this.status = null;
    }

    @Override
    public String getApiVersion() {
        return CamelKSupport.CAMELK_CRD_GROUP + "/" + CamelKSettings.getApiVersion();
    }

    /**
     * Fluent builder
     */
    public static class Builder {
        private Map<String, IntegrationSpec.TraitConfig> traits;
        private List<String> dependencies;
        private List<IntegrationSpec.Configuration> configuration;
        private String source;
        private String name;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder dependencies(List<String> dependencies) {
            this.dependencies = Collections.unmodifiableList(dependencies);
            return this;
        }

        public Builder traits(Map<String, IntegrationSpec.TraitConfig> traits) {
            this.traits = Collections.unmodifiableMap(traits);
            return this;
        }

        public Builder configuration(List<IntegrationSpec.Configuration> configuration) {
            this.configuration = Collections.unmodifiableList(configuration);
            return this;
        }

        public Integration build() {
            Integration i = new Integration();
            String metadataName = name.contains(".") ? name.substring(0, name.indexOf(".")) : name;
            i.getMetadata().setName(metadataName);
            i.getSpec().setSources(Collections.singletonList(new IntegrationSpec.Source(name, source)));
            i.getSpec().setDependencies(dependencies);
            i.getSpec().setTraits(traits);
            i.getSpec().setConfiguration(configuration);
            return i;
        }

        public Integration build(IntegrationSpec integrationSpec) {
            Integration i = new Integration();
            i.spec = integrationSpec;
            String metadataName = name.contains(".") ? name.substring(0, name.indexOf(".")) : name;
            i.getMetadata().setName(metadataName);
            return i;
        }
    }
}
