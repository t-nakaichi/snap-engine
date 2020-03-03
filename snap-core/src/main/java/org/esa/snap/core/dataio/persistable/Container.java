package org.esa.snap.core.dataio.persistable;

import java.util.List;

public interface Container<E> extends Item {
    List<Property<E>> getProperties();

    List<Container<E>> getContainer();

    Property<E> getProperty(String name);

    Container<E> getContainer(String name);

    void add(Property<E> property);

    void add(Container<E> container);
}
