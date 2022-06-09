package software.tnb.servicenow.dto;

/**
 * Represents link/value json structure.
 */
public class LinkValue {
    private String link;
    private String value;

    public LinkValue() {
    }

    public LinkValue(String value) {
        this.value = value;
    }

    public String getLink() {
        return this.link;
    }

    public String getValue() {
        return this.value;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return "LinkValue(link=" + this.getLink() + ", value=" + this.getValue() + ")";
    }
}
