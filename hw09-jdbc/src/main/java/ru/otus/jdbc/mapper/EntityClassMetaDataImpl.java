package ru.otus.jdbc.mapper;

import ru.otus.crm.annotations.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData {
    private final Class<T> entityClass;

    public EntityClassMetaDataImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public String getName() {
        return entityClass.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        Map<Constructor<T>, Integer> constructors = new HashMap();
        for (Constructor c : entityClass.getDeclaredConstructors()) {
            constructors.put(c, c.getParameterCount());
        }
        return Collections.max(constructors.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    @Override
    public Field getIdField() {
        List<Field> fields = Arrays.asList(entityClass.getDeclaredFields());
        return fields.stream().filter(f -> f.isAnnotationPresent(Id.class)).collect(Collectors.toList()).get(0);
    }

    @Override
    public List<Field> getAllFields() {
        return List.of(entityClass.getDeclaredFields());
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        List<Field> fields = Arrays.asList(entityClass.getDeclaredFields());
        return fields.stream().filter(f -> !f.isAnnotationPresent(Id.class)).collect(Collectors.toList());
    }
}
