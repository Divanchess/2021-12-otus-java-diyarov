package ru.otus.appcontainer;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.appcontainer.exceptions.InitializeConfigException;
import ru.otus.appcontainer.exceptions.InstantiateBeanException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        Object configInstance = initializeConfig(configClass);

        List<Method> methods = getAppComponentMethods(configClass);

        for (Method method : methods) {
            try {
                Object[] initializedArgs = Arrays.stream(method.getParameterTypes()).map(c -> getAppComponent(c)).toArray();
                Object beanInstance = method.invoke(configInstance, initializedArgs);
                String beanName = method.getDeclaredAnnotation(AppComponent.class).name();
                appComponents.add(beanInstance);
                appComponentsByName.put(beanName, beanInstance);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new InstantiateBeanException(e);
            }
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private Object initializeConfig(Class<?> configClass) {
        try {
            return configClass.getDeclaredConstructor().newInstance();
        } catch ( InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new InitializeConfigException(e);
        }
    }

    private List<Method> getAppComponentMethods(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparingInt(
                        method -> method.getAnnotation(AppComponent.class).order()))
                .toList();
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        Optional<Object> component = appComponents.stream().filter(c -> componentClass.isAssignableFrom(c.getClass())).findFirst();
        return (C) (component.orElse(null));
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        return (C) appComponentsByName.get(componentName);
    }
}
