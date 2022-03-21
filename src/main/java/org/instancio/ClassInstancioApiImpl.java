package org.instancio;

public class ClassInstancioApiImpl<T> extends InstancioApiImpl<T> implements InstancioOfClassApi<T> {

    public ClassInstancioApiImpl(Class<T> klass) {
        super(klass);
    }

    @Override
    public InstancioApi<T> withTypeParameters(Class<?>... type) {
        super.addTypeParameters(type);
        return this;
    }

}
