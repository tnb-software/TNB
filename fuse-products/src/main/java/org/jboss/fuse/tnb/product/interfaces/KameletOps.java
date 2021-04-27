package org.jboss.fuse.tnb.product.interfaces;

import org.jboss.fuse.tnb.product.ck.generated.Kamelet;
import org.jboss.fuse.tnb.product.ck.generated.KameletBinding;

import java.io.File;

public interface KameletOps {

    public abstract void loadKamelet(File file);

    public abstract void loadKameletBinding(File file);

    public abstract void deleteKamelet(String name);

    public abstract void deleteKamelet(Kamelet kamelet);

    public abstract void deleteKameletBinding(String kamelet);

    public abstract void deleteKameletBinding(KameletBinding kameletBinding);

    public abstract void createKamelet(Kamelet kamelet);

    public abstract void createKameletBinding(KameletBinding kameletBinding);

    public abstract boolean isKameletBindingReady(KameletBinding kameletBinding);

    public abstract boolean isKameletReady(Kamelet kamelet);

    public abstract boolean isKameletReady(String name);

    public abstract Kamelet getKameletByName(String name);

    public abstract KameletBinding getKameletBindingByName(String name);
}
