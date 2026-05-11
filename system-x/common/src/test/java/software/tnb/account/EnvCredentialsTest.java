package software.tnb.account;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.account.util.TestAccountWithId;
import software.tnb.common.account.AccountFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.junitpioneer.jupiter.SetEnvironmentVariable;

@Tag("unit")
public class EnvCredentialsTest {
    private static final String USER_NAME = "testuser";
    private static final String PASSWORD = "password";

    @BeforeAll
    public static void beforeAll() {
        // ensure clean state not affected by other tests
        AccountFactory.setCredentialsLoader(null);
    }

    @Test
    @SetEnvironmentVariable(key = "TNB_TEST_ACC_USER_NAME", value = USER_NAME)
    @SetEnvironmentVariable(key = "TNB_TEST_ACC_PASSWORD", value = PASSWORD)
    public void loadCredentialsFromEnvVariablesTest() {
        TestAccountWithId acc = AccountFactory.create(TestAccountWithId.class);

        assertThat(acc.getUserName()).isEqualTo(USER_NAME);
        assertThat(acc.getPassword()).isEqualTo(PASSWORD);
    }
}
