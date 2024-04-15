package software.tnb.kudu.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class KuduConfiguration extends ServiceConfiguration {

    private static final String MASTER_NUM = "kudu.master.num";
    private static final String TABLET_NUM = "kudu.tablet.num";

    public KuduConfiguration withMasterNumber(Integer masterNumber) {
        set(MASTER_NUM, masterNumber);
        return this;
    }

    public Integer masterNumber() {
        return get(MASTER_NUM, Integer.class);
    }

    public KuduConfiguration withTabletNumber(Integer tabletNumber) {
        set(TABLET_NUM, tabletNumber);
        return this;
    }

    public Integer tabletNumber() {
        return get(TABLET_NUM, Integer.class);
    }
}
