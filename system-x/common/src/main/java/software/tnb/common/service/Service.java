package software.tnb.common.service;

import software.tnb.common.account.Account;
import software.tnb.common.account.AccountFactory;
import software.tnb.common.util.ReflectionUtil;
import software.tnb.common.validation.Validation;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;

public abstract class Service<A extends Account, C, V extends Validation> implements BeforeAllCallback, AfterAllCallback {
    protected A account;
    protected C client;
    protected V validation;

    public A account() {
        if (account == null) {
            Class<A> accountClass = (Class<A>) ReflectionUtil.getGenericTypesOf(Service.class, this.getClass())[0];
            account = AccountFactory.create(accountClass);
        }
        return account;
    }

    protected C client() {
        return client;
    }

    public V validation() {
        return validation;
    }
}
