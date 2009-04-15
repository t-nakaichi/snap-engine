/*
 * $Id: $
 *
 * Copyright (C) 2009 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.bc.ceres.binding.swing;

import com.bc.ceres.binding.ValueDescriptor;
import com.bc.ceres.binding.swing.internal.TextFieldEditor;
import com.bc.ceres.core.Assert;
import com.bc.ceres.core.ServiceRegistry;
import com.bc.ceres.core.ServiceRegistryFactory;

import java.util.ServiceLoader;

/**
 * A registry for {@link ValueEditor}.
 *
 * @author Marco Zuehlke
 * @version $Revision$ $Date$
 * @since BEAM 4.6
 */
public class ValueEditorRegistry {
    private static final ServiceRegistry<ValueEditor> REGISTRY;
    private static final ValueEditor DEFAULT_EDITOR;
    
    static {
        REGISTRY = ServiceRegistryFactory.getInstance().getServiceRegistry(ValueEditor.class);

        final ServiceLoader<ValueEditor> serviceLoader = ServiceLoader.load(ValueEditor.class);
        for (final ValueEditor valueEditor : serviceLoader) {
            REGISTRY.addService(valueEditor);
        }
        DEFAULT_EDITOR = REGISTRY.getService(TextFieldEditor.class.getName());
    }
    
    private ValueEditorRegistry() {
    }
    
    /**
     * Get a value editor by its class name.
     * @param className The class name of the value editor.
     *  
     * @return the value editor or {@code null} if no editor exist for the given class name.
     */
    public static ValueEditor getValueEditor(String className) {
        return REGISTRY.getService(className);
    }
    
    /**
     * Finds a matching {@link ValueEditor} for the given {@link ValueDescriptor}.
     * 
     * At first , if set, the property {@code "valueEditor"} of the value descriptor
     * is used. Afterwards all registered {@link ValueEditor}s are tested,
     * whether the can provide an editor. As a fallback a {@link TextFieldEditor} is returned.
     *  
     * @param valueDescriptor the value descriptor
     * @return the editor that can edit values described by the value descriptor
     */
    public static ValueEditor findValueEditor(ValueDescriptor valueDescriptor) {
        Assert.notNull(valueDescriptor, "valueDescriptor must not be null");
        ValueEditor valueEditor = (ValueEditor) valueDescriptor.getProperty("valueEditor");
        if (valueEditor != null) {
            return valueEditor;
        }
        for (ValueEditor editor : REGISTRY.getServices()) {
            if (editor.isValidFor(valueDescriptor)) {
                return editor;
            }
        }
        return DEFAULT_EDITOR;
    }
}
