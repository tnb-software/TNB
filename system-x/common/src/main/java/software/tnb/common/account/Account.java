package software.tnb.common.account;

import software.tnb.common.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public interface Account {
    /**
     * Create a Properties instance from this account.
     *
     * @return properties instance
     */
    default Properties toProperties() {
        Properties properties = new Properties();
        for (Field field : getAllFields(new ArrayList<>(), this.getClass())) {
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

    /**
     * Get all fields from a given class.
     * <p>
     * It is called recursively to obtain fields from a superclass, if the class has some.
     *
     * @param fields list of fields
     * @param clazz class
     * @return list of fields
     */
    private List<Field> getAllFields(List<Field> fields, Class<?> clazz) {
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        if (clazz.getSuperclass() != null) {
            getAllFields(fields, clazz.getSuperclass());
        }

        return fields;
    }
}
