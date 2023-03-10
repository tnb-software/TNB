package software.tnb.jaeger.validation.model;

import java.util.Objects;
import java.util.StringJoiner;

public class KTVItem {
    private String key;
    private String type;
    private String value;

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final KTVItem ktvItem = (KTVItem) o;
        return key.equals(ktvItem.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KTVItem.class.getSimpleName() + "[", "]")
            .add("key='" + key + "'")
            .add("type='" + type + "'")
            .add("value='" + value + "'")
            .toString();
    }
}
