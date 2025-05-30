package software.tnb.operatorhub.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.validation.NoValidation;
import software.tnb.operatorhub.service.configuration.OperatorHubSubscriptionConfiguration;

public abstract class OperatorHubSubscription extends ConfigurableService<NoAccount, NoClient, NoValidation, OperatorHubSubscriptionConfiguration> {

}
