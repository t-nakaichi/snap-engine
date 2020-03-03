package org.esa.snap.core.dataio.persistable.xml;

import org.esa.snap.core.dataio.persistable.Container;
import org.esa.snap.core.dataio.persistable.Property;
import org.esa.snap.core.dataio.persistable.xml.XmlSupport;
import org.esa.snap.core.dataop.downloadable.XMLSupport;
import org.jdom.Element;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class XmlSupportTest {

    @Test
    public void createRootContainer_invalid_name() {
        //preparation
        final String illegalRootName = "   first root   ";
        final String validXmlRootName = "first_root";
        final String illegalPropName = "   prop name   ";
        final String validXmlPropName = "prop_name";

        //execution
        final XmlSupport xmlSupport = new XmlSupport();
        final Container<Element> rootContainer = xmlSupport.createRootContainer(illegalRootName);
        rootContainer.add(xmlSupport.createProperty(illegalPropName, "    prop   value   "));

        //verification
        assertNotNull(rootContainer);
        assertThat(rootContainer.getName(), is(illegalRootName));
        assertThat(rootContainer.getProperties().size(), is(1));
        assertThat(rootContainer.getProperties().get(0).getName(), is(illegalPropName));
        assertThat(rootContainer.getProperties().get(0), is(sameInstance(rootContainer.getProperty(illegalPropName))));

        final List<Element> created = xmlSupport.getCreated();
        assertNotNull(created);
        assertThat(created.size(), is(1));
        final Element xmlRoot = created.get(0);
        assertNotNull(xmlRoot);
        assertThat(xmlRoot.getName(), is(validXmlRootName));
        assertThat(xmlRoot.getChildren().size(), is(1));
        assertThat(xmlRoot.getAttribute("___unmodified_name___").getValue(), is(illegalRootName));
        assertThat(xmlRoot.getChild(validXmlPropName).getValue(), is("    prop   value   "));
    }

    @Test
    public void ContainerInsideContainer() {
        final XmlSupport xmlSupport = new XmlSupport();
        final Container<Element> root = xmlSupport.createRootContainer("roo");
        root.add(xmlSupport.createContainer("inside"));

        assertThat(root.getContainer().size(), is(1));
        assertThat(root.getProperties().size(), is(0));
        assertThat(root.getContainer("inside"), is(sameInstance(root.getContainer().get(0))));

        final List<Element> created = xmlSupport.getCreated();
        assertThat(created.size(), is(1));
        final Element rootElem = created.get(0);
        assertThat(rootElem.getName(), is("roo"));
        assertThat(rootElem.getAttributes().size(), is(0));
        assertThat(rootElem.getChildren().size(), is(1));
        assertThat(rootElem.getChild("inside"), is(sameInstance(rootElem.getChildren().get(0))));
        assertThat(rootElem.getChild("inside").getAttributes().size(), is(0));
    }

    @Test
    public void createTwoRootContainersWithProperties() {
        final XmlSupport xmlSupport = new XmlSupport();

        Container<Element> container;

        container = xmlSupport.createRootContainer("root");
        container.add(xmlSupport.createProperty("some", "aaa"));
        container.add(xmlSupport.createProperty("different", 3.58571236548123691235416549715));
        container.add(xmlSupport.createProperty("property", 21987430L));

        assertThat(container.getProperties().size(), is(3));

        container = xmlSupport.createRootContainer("  second root  ");
        container.add(xmlSupport.createProperty("And", "bbbbbb"));
        container.add(xmlSupport.createProperty("Other", 3.58571236548123691235416549715f));
        container.add(xmlSupport.createProperty("Characteristics", (short) 2198));

        assertThat(container.getProperties().size(), is(3));


        final List<Element> created = xmlSupport.getCreated();
        assertNotNull(created);
        assertThat(created.size(), is(2));

        final Element firstRoot = created.get(0);
        assertNotNull(firstRoot);
        assertThat(firstRoot.getName(), is("root"));
        assertThat(firstRoot.getChildren().size(), is(3));
        assertThat(firstRoot.getChild("some").getValue(), is("aaa"));
        assertThat(firstRoot.getChild("different").getValue(), is("3.585712365481237"));
        assertThat(firstRoot.getChild("property").getValue(), is("21987430"));

        final Element secondRoot = created.get(1);
        assertNotNull(secondRoot);
        assertThat(secondRoot.getName(), is("second_root"));
        assertThat(secondRoot.getChildren().size(), is(3));
        assertThat(secondRoot.getChild("And").getValue(), is("bbbbbb"));
        assertThat(secondRoot.getChild("Other").getValue(), is("3.5857124"));
        assertThat(secondRoot.getChild("Characteristics").getValue(), is("2198"));
    }

    @Test
    public void propertyGetter() {
        final XmlSupport xmlSupport = new XmlSupport();
        final Property<Element> numba = xmlSupport.createProperty("Numba", 123456.78910111213141516);

        assertThat(numba.getValueString(), is("123456.78910111212"));
        assertThat(numba.getValueDouble(), is(123456.78910111212));
        assertThat(numba.getValueFloat(), is(123456.79F));

        final Property<Element> numbaLong = xmlSupport.createProperty("NumbaLong", Long.MAX_VALUE);

        assertThat(numbaLong.getValueLong(), is(Long.MAX_VALUE));
        assertThat(numbaLong.getValueInt(), is(-1));
        assertThat(numbaLong.getValueShort(), is((short) -1));
        assertThat(numbaLong.getValueByte(), is((byte) -1));

        final Property<Element> numbaInt = xmlSupport.createProperty("NumbaInt", Integer.MAX_VALUE);

        assertThat(numbaInt.getValueLong(), is((long) Integer.MAX_VALUE));
        assertThat(numbaInt.getValueInt(), is(Integer.MAX_VALUE));
        assertThat(numbaInt.getValueShort(), is((short) -1));
        assertThat(numbaInt.getValueByte(), is((byte) -1));

        final Property<Element> numbaShort = xmlSupport.createProperty("NumbaShort", Short.MAX_VALUE);

        assertThat(numbaShort.getValueLong(), is((long) Short.MAX_VALUE));
        assertThat(numbaShort.getValueInt(), is((int) Short.MAX_VALUE));
        assertThat(numbaShort.getValueShort(), is(Short.MAX_VALUE));
        assertThat(numbaShort.getValueByte(), is((byte) -1));

        final Property<Element> numbaByte = xmlSupport.createProperty("NumbaInt", Byte.MAX_VALUE);

        assertThat(numbaByte.getValueLong(), is((long) Byte.MAX_VALUE));
        assertThat(numbaByte.getValueInt(), is((int) Byte.MAX_VALUE));
        assertThat(numbaByte.getValueShort(), is((short) Byte.MAX_VALUE));
        assertThat(numbaByte.getValueByte(), is(Byte.MAX_VALUE));
    }
}