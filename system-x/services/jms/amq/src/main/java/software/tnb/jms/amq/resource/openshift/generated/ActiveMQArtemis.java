package software.tnb.jms.amq.resource.openshift.generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.processing.Generated;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "apiVersion",
    "kind",
    "metadata",
    "spec",
    "status"
})
@Generated("jsonschema2pojo")
@Group("broker.amq.io")
@Version("v1beta1")
public class ActiveMQArtemis extends CustomResource<ActiveMQArtemisSpec, ActiveMQArtemisStatus> {

}
