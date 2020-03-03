package org.esa.snap.core.dataio.persistable;

public interface Property<E> extends Item {
    Object getValue();

    String getValueString();

    Double getValueDouble();

    Float getValueFloat();

    Long getValueLong();

    Integer getValueInt();

    Short getValueShort();

    Byte getValueByte();

    E get();
}
