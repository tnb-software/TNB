package software.tnb.hawtio.validation;

import software.tnb.common.validation.Validation;
import software.tnb.hawtio.client.HawtioClient;

public class HawtioValidation implements Validation {

    private final HawtioClient client;

    public HawtioValidation(HawtioClient client) {
        this.client = client;
    }

    /**
     * return true if the connection is available from console, using proxy
     * @param jmxUrl String complete endpoint for proxy, without base eg: /proxy/http/localhost/8778/jolokia/
     * @return boolean
     */
    public boolean isConnectionAvailable(String jmxUrl) {
        return client.isConnectionAvailable(jmxUrl);
    }

    /**
     * Return row format for jmx query
     * @param jmxUrl String complete endpoint for proxy, without base eg: <pre>/proxy/http/localhost/8778/jolokia/</pre>
     * @param rawQuery String, raw data in JSON format to use as request
     *         eg: <pre>{"type":"read","mbean":"org.apache.camel:context=CamelJolokia,type=context,name=\"CamelJolokia\""}</pre>
     * @return String, in JSON format
     */
    public String jmxQuery(String jmxUrl, String rawQuery) {
        return client.jmxQuery(jmxUrl, rawQuery);
    }

}
