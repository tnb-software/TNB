package software.tnb.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import software.tnb.account.util.TestAccountNoId;
import software.tnb.account.util.TestAccountWithId;
import software.tnb.account.util.TestAccountWithMissingId;
import software.tnb.account.util.TestCompositeAccount;
import software.tnb.common.account.AccountFactory;
import software.tnb.common.config.TestConfiguration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

@Tag("unit")
public class AccountFactoryTest {
    @BeforeAll
    public static void beforeAll() {
        System.setProperty(TestConfiguration.CREDENTIALS_FILE, Paths.get("src", "test", "resources", "credentials.yaml").toAbsolutePath().toString());
    }

    @AfterAll
    public static void afterAll() {
        System.clearProperty(TestConfiguration.CREDENTIALS_FILE);
    }

    @Test
    public void shouldParseAccountTest() {
        TestAccountWithId acc = AccountFactory.create(TestAccountWithId.class);
        assertThat(acc.getUsername()).isEqualTo("John");
        assertThat(acc.getPassword()).isEqualTo("Secret");
        assertThat(acc.accountId()).isEqualTo(999);
    }

    @Test
    public void shouldThrowExceptionWhenIdNotPresentTest() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> AccountFactory.create(TestAccountWithMissingId.class));
    }

    @Test
    public void shouldInitInstanceWithNoIdTest() {
        TestAccountNoId acc = AccountFactory.create(TestAccountNoId.class);
        assertThat(acc.getUsername()).isEqualTo("Hello");
        assertThat(acc.getPassword()).isEqualTo("World");
    }

    @Test
    public void shouldCreateAccountFromMultipleIDsTest() {
        TestCompositeAccount acc = AccountFactory.create(TestCompositeAccount.class);
        assertThat(acc.access_key()).isEqualTo("access");
        assertThat(acc.secret_key()).isEqualTo("secret");
        assertThat(acc.new_key()).isEqualTo("new_value");
    }

    @Test
    public void shouldIgnoreMissingIDForCompositeAccountTest() {
        System.setProperty("testawsaccount.id", "aws-nonexistent");
        try {
            TestCompositeAccount acc = AccountFactory.create(TestCompositeAccount.class);
            assertThat(acc.access_key()).isNull();
            assertThat(acc.secret_key()).isNull();
            assertThat(acc.new_key()).isEqualTo("new_value");
        } finally {
            System.clearProperty("testawsaccount.id");
        }
    }

    @Test
    public void shouldLoadParentValuesForCompositeAccountTest() {
        System.setProperty("testcompositeaccount.id", "aws-composite-complete");
        try {
            TestCompositeAccount acc = AccountFactory.create(TestCompositeAccount.class);
            assertThat(acc.access_key()).isEqualTo("access");
            assertThat(acc.secret_key()).isEqualTo("secret");
            assertThat(acc.new_key()).isEqualTo("new_value");
        } finally {
            System.clearProperty("testcompositeaccount.id");
        }
    }

    @Test
    public void shouldOverrideIDsWithSystemPropertyTest() {
        System.setProperty("testaccountwithid.id", "test-acc-property");
        try {
            TestAccountWithId acc = AccountFactory.create(TestAccountWithId.class);
            assertThat(acc.getUsername()).isEqualTo("John");
            assertThat(acc.getPassword()).isEqualTo("Secret");
            assertThat(acc.accountId()).isEqualTo(999);
        } finally {
            System.clearProperty("testaccountwithid.id");
        }
    }
}
