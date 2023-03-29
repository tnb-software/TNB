package software.tnb.product.mapstruct;

import software.tnb.common.config.Configuration;

public class MapstructConfiguration extends Configuration {
    public static final String MAPSTRUCT_MAPPER_VERSION = "mapstruct.mapper.version";

    public static String mapstructMapperVersion() {
        return getProperty(MAPSTRUCT_MAPPER_VERSION, "1.5.2.Final");
    }
}
