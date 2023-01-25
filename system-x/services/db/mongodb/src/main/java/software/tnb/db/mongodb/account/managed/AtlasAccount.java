package software.tnb.db.mongodb.account.managed;

import software.tnb.common.account.WithId;
import software.tnb.db.mongodb.account.MongoDBAccount;

public class AtlasAccount extends MongoDBAccount implements WithId {
    @Override
    public String credentialsId() {
        return "mongodb_atlas";
    }
}
