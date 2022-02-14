package org.jboss.fuse.tnb.product.interfaces;

import java.util.Properties;

import io.fabric8.camelk.v1alpha1.Kamelet;

public interface KameletOps {
    void createKamelet(Kamelet kamelet);

    boolean isKameletReady(String name);

    void createApplicationPropertiesSecretForKamelet(String kameletName, Properties credentials);

    void deleteSecretForKamelet(String kameletName);

    void removeKamelet(String kameletName);
}
