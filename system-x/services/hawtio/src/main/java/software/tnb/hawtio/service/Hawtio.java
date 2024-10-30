package software.tnb.hawtio.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.service.Service;
import software.tnb.hawtio.client.HawtioClient;
import software.tnb.hawtio.validation.HawtioValidation;

public abstract class Hawtio extends Service<NoAccount, HawtioClient, HawtioValidation> {

    public abstract String getHawtioUrl();

    @Override
    public HawtioValidation validation() {
        return new HawtioValidation(client());
    }
}
