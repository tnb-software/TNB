package org.jboss.fuse.tnb.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.jboss.fuse.tnb.account.util.TestAccountNoId;
import org.jboss.fuse.tnb.account.util.TestAccountWithId;
import org.jboss.fuse.tnb.account.util.TestAccountWithMissingId;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.config.TestConfiguration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

@Tag("unit")
public class AccountsTest {
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
        TestAccountWithId acc = Accounts.get(TestAccountWithId.class);
        assertThat(acc.getUsername()).isEqualTo("John");
        assertThat(acc.getPassword()).isEqualTo("Secret");
        assertThat(acc.accountId()).isEqualTo(999);
    }

    @Test
    public void shouldThrowExceptionWhenIdNotPresentTest() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> Accounts.get(TestAccountWithMissingId.class));
    }

    @Test
    public void shouldInitInstanceWithNoIdTest() {
        TestAccountNoId acc = Accounts.get(TestAccountNoId.class);
        assertThat(acc.getUsername()).isEqualTo("Hello");
        assertThat(acc.getPassword()).isEqualTo("World");
    }
}
