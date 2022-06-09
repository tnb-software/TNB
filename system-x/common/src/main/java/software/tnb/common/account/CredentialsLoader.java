package software.tnb.common.account;

public interface CredentialsLoader {

    <T extends Account> T get(String accountId, Class<T> accountClass);
}
