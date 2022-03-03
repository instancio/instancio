package org.instancio;

public class Instancio {

    public static <C, T extends CreationSettingsAPI<C, T>> ObjectCreationSettingsAPI<C, T> of(Class<C> klass) {
        return new ObjectCreationSettingsAPI<>(klass);
    }

    public static <C, T extends CreationSettingsAPI<C, T>> CollectionCreationSettingsAPI<C, T> ofList(Class<C> klass) {
        return new CollectionCreationSettingsAPI<>(klass);
    }
}
