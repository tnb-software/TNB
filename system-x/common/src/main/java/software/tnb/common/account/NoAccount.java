package software.tnb.common.account;

import java.util.Properties;

public class NoAccount implements Account {
    @Override
    public Properties toProperties() {
        throw new RuntimeException("This account shouldn't be used, as the service doesn't require an account");
    }
}
