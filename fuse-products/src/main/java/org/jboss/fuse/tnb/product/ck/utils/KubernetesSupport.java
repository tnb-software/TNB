package org.jboss.fuse.tnb.product.ck.utils;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

public final class KubernetesSupport {
    private KubernetesSupport() {
        // prevent instantiation of utility class
    }

    public static Yaml yaml() {
        Representer representer = new Representer() {
            @Override
            protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
                // if value of property is null, ignore it.
                if (propertyValue == null) {
                    return null;
                } else {
                    return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                }
            }
        };
        representer.getPropertyUtils().setSkipMissingProperties(true);
        return new Yaml(representer);
    }

    public static void createResource(KubernetesClient k8sClient, String namespace, CustomResourceDefinitionContext context, String yaml) {
        try {
            k8sClient.customResource(context).createOrReplace(namespace, yaml);
        } catch (IOException e) {
            throw new UnsupportedOperationException("Failed to create resource", e);
        }
    }
}
