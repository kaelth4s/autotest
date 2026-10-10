package ru.kaelth4s.framework.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;

public class RandomFiller {
    private RandomFiller() {}

    public static <T> T fill(T record) {
        if (record == null) return null;
        return fillInternal(record);
    }

    @SuppressWarnings("unchecked")
    private static <T> T fillInternal(T record) {
        Class<?> clazz = record.getClass();
        if (!clazz.isRecord()) {
            throw new IllegalArgumentException("Only records are supported");
        }

        RecordComponent[] components = clazz.getRecordComponents();
        Object[] values = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            RecordComponent comp = components[i];
            try {
                Object current = comp.getAccessor().invoke(record);
                Class<?> compType = comp.getType();

                RandomGenerated ann = findAnnotation(comp, clazz);

                if (ann != null) {
                    values[i] = generateValue(ann.value());
                } else {
                    if (compType.isRecord() && current != null) {
                        values[i] = fillInternal(current);
                    } else {
                        values[i] = current;
                    }
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(String.format("Error processing %s", comp.getName()), e);
            }
        }

        try {
            Class<?>[] paramTypes = Arrays.stream(components)
                    .map(RecordComponent::getType)
                    .toArray(Class<?>[]::new);
            Constructor<?> ctor = clazz.getDeclaredConstructor(paramTypes);
            ctor.setAccessible(true);
            return (T) ctor.newInstance(values);
        } catch (InvocationTargetException | NoSuchMethodException |
                 InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException("Failed to instantiate record", e);
        }
    }

    private static RandomGenerated findAnnotation(RecordComponent comp, Class<?> clazz) {
        RandomGenerated ann = comp.getAnnotation(RandomGenerated.class);
        if (ann == null) {
            try {
                var field = clazz.getDeclaredField(comp.getName());
                ann = field.getAnnotation(RandomGenerated.class);
            } catch (NoSuchFieldException _) { }
        }
        return ann;
    }

    private static Object generateValue(String methodName) {
        try {
            Method method = RandomUtil.class.getMethod(methodName);
            return method.invoke(null);
        } catch (InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException e)
        {
            throw new RuntimeException(String.format("Error calling RandomUtil.%s", methodName), e);
        }
    }
}