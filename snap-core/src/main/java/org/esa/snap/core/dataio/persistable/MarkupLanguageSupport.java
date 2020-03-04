package org.esa.snap.core.dataio.persistable;

import org.jdom.Element;

import java.util.List;

public interface MarkupLanguageSupport<T> {
    Container<T> createRootContainer(String name);

    List<T> getCreated();

    List<Item> convert(T... o);

    Property<T> createProperty(String name, Object value);

    Container<T> createContainer(String name);
}
