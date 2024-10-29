package software.tnb.hawtio.client;

public interface HawtioClient {

    boolean isConnectionAvailable(String jmxUrl);

    String jmxQuery(String jmxUrl, String rawQuery);
}
