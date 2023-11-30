package software.tnb.common.account;

import software.tnb.common.util.ReflectionUtil;
import software.tnb.common.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Properties;

public interface Account {
    /**
     * Create a Properties instance from this account.
     *
     * @return properties instance
     */
    default Properties toProperties() {
        Properties properties = new Properties();
        for (Field field : ReflectionUtil.getAllFields(new ArrayList<>(), this.getClass())) {
            try {
                field.setAccessible(true);
                // Null values can't be stored in properties
                if (field.get(this) != null) {
                    properties.put(StringUtils.replaceUnderscoreWithCamelCase(field.getName()), field.get(this));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to get field " + field.getName() + " value: ", e);
            }
        }

        return properties;
    }
}
