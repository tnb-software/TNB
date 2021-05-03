package org.jboss.fuse.tnb.product.interfaces;

import org.jboss.fuse.tnb.product.ck.generated.Kamelet;

import java.io.File;
import java.util.Properties;

public interface KameletOps {
    void createKamelet(File file);

    void createKamelet(Kamelet kamelet);

    boolean isKameletReady(String name);

    void createApplicationPropertiesSecretForKamelet(String kameletName, Properties credentials);

    void deleteSecretForKamelet(String kameletName);

    void removeKamelet(String kameletName);
}
