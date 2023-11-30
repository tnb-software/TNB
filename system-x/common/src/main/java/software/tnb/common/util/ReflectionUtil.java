package software.tnb.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    /**
     * This method walks though parent classes until the parent class is equal to the first argument of the method. Then it returns an array of
     * generic types of the class.
     *
     * @param parent parent class that is generic
     * @param clazz current class
     * @return an array of generic types of the parent class
     */
    public static Type[] getGenericTypesOf(Class<?> parent, Class<?> clazz) {
        Class<?> current = clazz;
        while (true) {
            Type superClass = current.getGenericSuperclass();
            if (superClass instanceof ParameterizedType && ((ParameterizedType) superClass).getRawType().equals(parent)) {
                break;
            } else {
                current = current.getSuperclass();
            }
        }

        return ((ParameterizedType) (current.getGenericSuperclass())).getActualTypeArguments();
    }

    /**
     * Returns the name of the superclass for the given class.
     *
     * @param current class to check
     * @return name of the superclass
     */
    public static String getSuperClassName(Class<?> current) {
        final Class<?> superclass = current.getSuperclass();
        if (Object.class.equals(superclass)) {
            throw new IllegalStateException("Current class " + current.getSimpleName() + " does not extend any other class"
                + " and was expected to do so, check what's wrong as this shouldn't happen");
        }
        return superclass.getSimpleName();
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
    public static List<Field> getAllFields(List<Field> fields, Class<?> clazz) {
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        if (clazz.getSuperclass() != null) {
            getAllFields(fields, clazz.getSuperclass());
        }

        return fields;
    }
}
