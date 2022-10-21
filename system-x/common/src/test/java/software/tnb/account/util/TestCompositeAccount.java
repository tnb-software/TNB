package software.tnb.account.util;

public class TestCompositeAccount extends TestAWSAccount {
    private String new_key;

    @Override
    public String credentialsId() {
        return "aws-composite";
    }

    public String new_key() {
        return new_key;
    }

    public void setNew_key(String new_key) {
        this.new_key = new_key;
    }
}
