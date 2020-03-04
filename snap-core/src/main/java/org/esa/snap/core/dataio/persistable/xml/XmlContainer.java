package org.esa.snap.core.dataio.persistable.xml;

import org.esa.snap.core.dataio.persistable.Container;
import org.esa.snap.core.dataio.persistable.Property;
import org.jdom.Element;

import java.util.*;

class XmlContainer implements Container<Element> {

    final Element element;
    private final Map<String, Property<Element>> properties = new LinkedHashMap<>();
    private final Map<String, Container<Element>> containers = new LinkedHashMap<>();

    XmlContainer(Element element) {
        this.element = element;
        initialize();
    }

    private void initialize() {
        final List<Element> children = element.getChildren();
        for (Element child : children) {
            final String name = XmlSupport.fetchNameFrom(child);
            final int numChildren = child.getChildren().size();
            if (numChildren == 0) {
                properties.put(name, new XmlProperty(child));
            } else {
                containers.put(name, new XmlContainer(child));
            }
        }
    }

    @Override
    public List<Property<Element>> getProperties() {
        return Collections.unmodifiableList(new ArrayList<>(properties.values()));
    }

    @Override
    public List<Container<Element>> getContainer() {
        return Collections.unmodifiableList(new ArrayList<>(containers.values()));
    }

    @Override
    public Property<Element> getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public Container<Element> getContainer(String name) {
        return containers.get(name);
    }

    @Override
    public void add(Property<Element> property) {
        Element e = property.get();
        element.addContent(e);
        properties.put(property.getName(), property);
    }

    @Override
    public void add(Container<Element> container) {
        element.addContent(((XmlContainer) container).element);
        containers.put(container.getName(), container);
    }

    @Override
    public String getName() {
        return XmlSupport.fetchNameFrom(element);
    }
}
