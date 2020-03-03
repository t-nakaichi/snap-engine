package org.esa.snap.core.dataio.persistable;

import org.jdom.Element;

import java.util.*;

public class JsonSupport implements MarkupLanguageSupport<Map<String, Object>> {
    final List<Element> elements = new ArrayList<>();

    //    @Override
//    public Container<Map<String, Object>> createRootContainer(String name) {
//        final Element element = new Element(name);
//        elements.add(element);
//        return new XmlContainer(element);
//    }

    @Override
    public Container<Map<String, Object>> createRootContainer(String name) {
        return null;
    }

    //    @Override
//    public List<Element> getCreated() {
//        return Collections.unmodifiableList(elements);
//    }
    @Override
    public List<Map<String, Object>> getCreated() {
        return null;
    }

    @Override
    public List<Item> convert(Map<String, Object> o) {
        return null;
    }

    @Override
    public Property<Map<String, Object>> createProperty(String name, Object value) {
        return null;
    }

    @Override
    public Container<Map<String, Object>> createContainer(String name) {
        return null;
    }

//    @Override
//    public List<Item> convert(Element o) {
//        return null;
//    }
//
//    @Override
//    public Property<Element> createProperty(String name, Object value) {
//        final Element element = new Element(name);
//        element.setText(value.toString());
//        return new XmlProperty(element);
//    }

    private static class XmlContainer implements Container<Element> {

        private final Element element;
        private final HashMap<String, Property<Element>> properties = new HashMap<>();
        private final HashMap<String, Container<Element>> containers = new HashMap<>();

        public XmlContainer(Element element) {
            this.element = element;
            initialize();
        }

        private void initialize() {
            final List<Element> children = element.getChildren();
            for (Element child : children) {
                final String name = child.getName();
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

        }

        @Override
        public String getName() {
            return element.getName();
        }

    }

    private static class XmlProperty implements Property<Element> {
        private Element element;

        public XmlProperty(Element element) {
            this.element = element;
        }

        @Override
        public Object getValue() {
            return element.getValue();
        }

        @Override
        public String getValueString() {
            return element.getValue();
        }

        @Override
        public Double getValueDouble() {
            final String value = element.getValue();
            if (value != null && !value.trim().isEmpty()) {
                return Double.valueOf(value);
            }
            return null;
        }

        @Override
        public Float getValueFloat() {
            final String value = element.getValue();
            if (value != null && !value.trim().isEmpty()) {
                return Float.valueOf(value);
            }
            return null;
        }

        @Override
        public Long getValueLong() {
            final String value = element.getValue();
            if (value != null && !value.trim().isEmpty()) {
                return Long.valueOf(value);
            }
            return null;
        }

        @Override
        public Integer getValueInt() {
            final String value = element.getValue();
            if (value != null && !value.trim().isEmpty()) {
                return Integer.valueOf(value);
            }
            return null;
        }

        @Override
        public Short getValueShort() {
            final String value = element.getValue();
            if (value != null && !value.trim().isEmpty()) {
                return Short.valueOf(value);
            }
            return null;
        }

        @Override
        public Byte getValueByte() {
            final String value = element.getValue();
            if (value != null && !value.trim().isEmpty()) {
                return Byte.valueOf(value);
            }
            return null;
        }

        @Override
        public Element get() {
            return element;
        }

        @Override
        public String getName() {
            return element.getName();
        }
    }
}
