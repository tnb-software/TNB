package software.tnb.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
}
