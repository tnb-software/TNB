package software.tnb.product.deploystrategy.impl.custom;

import software.tnb.common.product.ProductType;
import software.tnb.product.csb.configuration.SpringBootConfiguration;
import software.tnb.product.deploystrategy.OpenshiftDeployStrategy;
import software.tnb.product.deploystrategy.OpenshiftDeployStrategyType;

import com.google.auto.service.AutoService;

@AutoService(OpenshiftDeployStrategy.class)
public class JKubeCliStrategy extends CustomJKubeStrategy {

    public static final String OPENSHIFT_MAVEN_PLUGIN_AID = "openshift-maven-plugin";

    private static final String oc = String.format("%s:%s:%s", SpringBootConfiguration.openshiftMavenPluginGroupId()
        , OPENSHIFT_MAVEN_PLUGIN_AID
        , SpringBootConfiguration.openshiftMavenPluginVersion());

    public JKubeCliStrategy() {
        super(new String[]{"clean", "package", oc + ":resource", oc + ":build", oc + ":apply"}, new String[]{"openshift"});
        createTnbDeployment(true);
    }

    @Override
    public ProductType[] products() {
        return new ProductType[] {ProductType.CAMEL_SPRINGBOOT};
    }

    @Override
    public OpenshiftDeployStrategyType deployType() {
        return OpenshiftDeployStrategyType.CUSTOM;
    }
}
